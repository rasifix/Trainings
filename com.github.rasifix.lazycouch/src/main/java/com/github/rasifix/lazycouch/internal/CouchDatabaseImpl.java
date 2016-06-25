package com.github.rasifix.lazycouch.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.rasifix.lazycouch.CouchDatabase;
import com.github.rasifix.lazycouch.CouchQuery;
import com.github.rasifix.lazycouch.Document;
import com.github.rasifix.lazycouch.DocumentRevision;
import com.github.rasifix.lazycouch.NotFoundException;
import com.github.rasifix.lazycouch.RowMapper;
import com.github.rasifix.lazycouch.UpdateConflictException;
import com.github.rasifix.lazycouch.ViewResult;

public class CouchDatabaseImpl implements CouchDatabase {

	private String hostname;
	private int port;
	private String databaseName;
	
	private DefaultHttpClient client = new DefaultHttpClient();

	public CouchDatabaseImpl(String hostname, int port, String databaseName) {
		this.hostname = hostname;
		this.port = port;
		this.databaseName = databaseName;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	private URI createURI(String relativePath, String query) throws URISyntaxException {
		return URIUtils.createURI("http", hostname, port, "/" + databaseName + "/" + relativePath, query, null);
	}

	@Override
	public Document get(String id) throws IOException {		
		try {
			URI uri = createURI(id, null);
			HttpGet request = new HttpGet(uri);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode result = mapper.readTree(entity.getContent());
			
			Document doc = new Document(id);
			
			Iterator<Entry<String, JsonNode>> fields = result.fields();
			while (fields.hasNext()) {
				Entry<String, JsonNode> entry = fields.next();
				doc.set(entry.getKey(), entry.getValue());
			}
			
			return doc;
			
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void put(Document doc) throws IOException {
		try {
			HttpPut request = new HttpPut(createURI(doc.getId(), null));
			String content = toString(doc);
			request.setEntity(new StringEntity(content, "UTF-8"));
			
			HttpResponse response = client.execute(request);
			HttpEntity responseEntitity = response.getEntity();
			if (responseEntitity != null) {
				String responseString = EntityUtils.toString(responseEntitity);
				JsonNode result = parseJson(responseString);
				doc.put("_rev", result.path("rev").asText());
			}
			
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 must be a supported encoding", e);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	protected static JsonNode parseJson(HttpResponse response) throws IOException {
		HttpEntity responseEntitity = response.getEntity();
		if (responseEntitity != null) {
			return parseJson(EntityUtils.toString(responseEntitity));
		}
		return null;
		
	}

	private static ObjectNode parseJson(String responseString) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = mapper.readTree(responseString);
		return (ObjectNode) result;
	}

	private String toString(Document doc) throws JsonGenerationException, JsonMappingException, IOException {
		final StringWriter buffer = new StringWriter();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.writer().writeValue(buffer, doc.getNode());
		buffer.close();
		
		return buffer.toString();
	}

	@Override
	public void delete(Document doc) throws IOException {
		delete(doc.getId(), doc.getRev());
	}
	
	@Override
	public void delete(String id, String revision) throws IOException {
		try {
			HttpDelete delete = new HttpDelete(createURI(id, "rev=" + revision));
	
			HttpResponse response = client.execute(delete);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode >= 200 && statusCode < 300) {
				// OK
			} else if (statusCode == 404) {
				throw new NotFoundException("document with id '" + id + "' does not exist");
			} else if (statusCode == 409) {
				throw new UpdateConflictException("update conflict on deletion of '" + id + "'");
			} else {
				throw new HttpResponseException(statusCode, response.getStatusLine().getReasonPhrase());
			}
			
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public CouchQuery createQuery(String designDocumentName, String viewName) {
		return new CouchQueryImpl(this, designDocumentName, viewName);
	}
	
	@Override
	public Collection<DocumentRevision> getAllDesignDocuments() throws IOException {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("startkey", "\"_design/\""));
		params.add(new BasicNameValuePair("endkey", "\"_design0\""));
		try {
			HttpGet request = new HttpGet(createURI("_all_docs", URLEncodedUtils.format(params, null)));
			ViewResult result = client.execute(request, new ResponseHandler<ViewResult>() {
				@Override
				public ViewResult handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					HttpEntity responseEntitity = response.getEntity();
					if (responseEntitity != null) {
						return new ViewResult(parseJson(EntityUtils.toString(responseEntitity)));
					}
					return null;
				}
			});
			return result.map(new RowMapper<DocumentRevision>() {
				@Override
				public DocumentRevision mapRow(String id, Object key, Object value) {
					return new DocumentRevision(id, ((ObjectNode) value).path("rev").asText());
				}
			});
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static void main(String[] args) throws Exception {
//		CouchDatabaseImpl db = new CouchDatabaseImpl("localhost", 5984, "trainings");
//		CouchQuery query = db.createQuery("trainings", "overview");
//		query.setStartKey("\"2012-01-10\"");
//		List<String> result = query.query(new RowMapper<String>() {
//			@Override
//			public String mapRow(String id, Object key, Object value) {
//				return (String) key;
//			}
//		});
//		
//		System.out.println(result);
		
//		HttpClient client = new DefaultHttpClient();
//		HttpHead head = new HttpHead("http://localhost:5984/trainings/1888af4523cc43189388d2d00eb76180");
//		HttpResponse response = client.execute(head);
//		System.out.println(response.getStatusLine().getStatusCode());
//		System.out.println(response.getStatusLine().getReasonPhrase());
//		
//		for (Header header : response.getAllHeaders()) {
//			System.out.println(header.getName() + "\t" + header.getValue());
//		}
		
		CouchDatabaseImpl db = new CouchDatabaseImpl("localhost", 5984, "trainings");
		for (DocumentRevision revision : db.getAllDesignDocuments()) {
			System.out.println(revision.getId() + " @ " + revision.getRevision());
		}

	}

}
