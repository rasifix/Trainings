package com.github.rasifix.trainings.couchdb.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.github.rasifix.saj.JsonReader;
import com.github.rasifix.saj.JsonWriter;
import com.github.rasifix.saj.dom.JsonModelBuilder;
import com.github.rasifix.saj.dom.JsonObject;
import com.github.rasifix.trainings.couchdb.CouchDatabase;
import com.github.rasifix.trainings.couchdb.Document;

public class CouchDatabaseImpl implements CouchDatabase {

	private String hostname;
	private int port;
	private String databaseName;

	public CouchDatabaseImpl(String hostname, int port, String databaseName) {
		this.hostname = hostname;
		this.port = port;
		this.databaseName = databaseName;
	}

	@Override
	public Document getById(String id) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://" + hostname + ":" + port + "/" + databaseName + "/" + id);
		
		try {
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
			
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void put(Document doc) throws IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPut request = new HttpPut("http://" + hostname + ":" + port + "/" + databaseName + "/" + doc.getId());
		
		try {
			String content = toString(doc);
			StringEntity entity = new StringEntity(content, "UTF-8");
			request.setEntity(entity);
			
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
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public void delete(Document doc) {
		// TODO Auto-generated method stub

	}

}
