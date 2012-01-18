package com.github.rasifix.trainings.couchdb.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.github.rasifix.saj.JsonReader;
import com.github.rasifix.saj.JsonWriter;
import com.github.rasifix.saj.dom.JsonModelBuilder;
import com.github.rasifix.saj.dom.JsonObject;
import com.github.rasifix.trainings.couchdb.CouchDatabase;
import com.github.rasifix.trainings.couchdb.CouchQuery;
import com.github.rasifix.trainings.couchdb.Document;
import com.github.rasifix.trainings.couchdb.NotFoundException;
import com.github.rasifix.trainings.couchdb.UpdateConflictException;

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
	public Document getById(String id) throws IOException {		
		try {
			URI uri = createURI(id, null);
			HttpGet request = new HttpGet(uri);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			
			JsonModelBuilder builder = new JsonModelBuilder();
			JsonReader reader = new JsonReader(builder);
			reader.parseJson(entity.getContent());
			
			JsonObject result = (JsonObject) builder.getResult();
			Document doc = new Document(id);
			
			for (Entry<String, Object> entry : result.entrySet()) {
				doc.put(entry.getKey(), entry.getValue());
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
				JsonModelBuilder builder = new JsonModelBuilder();
				JsonReader reader = new JsonReader(builder);
				reader.parseJson(responseString);
				JsonObject result = (JsonObject) builder.getResult();
				doc.put("_rev", result.getString("rev"));
			}
			
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 must be a supported encoding", e);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	private String toString(Document doc) {
		final StringWriter buffer = new StringWriter();
		JsonWriter writer = new JsonWriter(buffer);
		doc.streamTo(writer);
		writer.close();
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
		
		HttpClient client = new DefaultHttpClient();
		HttpHead head = new HttpHead("http://localhost:5984/trainings/1888af4523cc43189388d2d00eb76180");
		HttpResponse response = client.execute(head);
		System.out.println(response.getStatusLine().getStatusCode());
		System.out.println(response.getStatusLine().getReasonPhrase());
		
		for (Header header : response.getAllHeaders()) {
			System.out.println(header.getName() + "\t" + header.getValue());
		}

	}

}
