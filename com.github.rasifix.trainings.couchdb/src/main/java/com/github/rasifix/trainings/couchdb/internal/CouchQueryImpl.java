package com.github.rasifix.trainings.couchdb.internal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.github.rasifix.saj.JsonReader;
import com.github.rasifix.saj.dom.JsonArray;
import com.github.rasifix.saj.dom.JsonModelBuilder;
import com.github.rasifix.saj.dom.JsonObject;
import com.github.rasifix.trainings.couchdb.CouchQuery;
import com.github.rasifix.trainings.couchdb.RowMapper;

class CouchQueryImpl implements CouchQuery {
	
	private final CouchDatabaseImpl database;
	private final String designDocumentName;
	private final String viewName;
	private String startKey;
	private String endKey;

	CouchQueryImpl(CouchDatabaseImpl database, String designDocumentName, String viewName) {
		this.database = database;
		this.designDocumentName = designDocumentName;
		this.viewName = viewName;
	}
	
	String getDatabaseName() {
		return database.getDatabaseName();
	}
	
	String getHost() {
		return database.getHostname();
	}
	
	int getPort() {
		return database.getPort();
	}
	
	@Override
	public void setStartKey(String startKey) {
		this.startKey = startKey;
	}

	@Override
	public void setEndKey(String endKey) {
		this.endKey = endKey;
	}

	@Override
	public <T> List<T> query(RowMapper<T> mapper) {
		HttpClient client = new DefaultHttpClient();
		
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		if (startKey != null) {
			qparams.add(new BasicNameValuePair("startkey", startKey));
		}		
		if (endKey != null) {
			qparams.add(new BasicNameValuePair("endkey", endKey));
		}
		
		try {
			URI uri = URIUtils.createURI("http", getHost(), getPort(), "/" + getDatabaseName() + "/_design/" + designDocumentName + "/_view/" + viewName,
					URLEncodedUtils.format(qparams, "UTF-8"), null);
			HttpGet request = new HttpGet(uri);
			System.out.println(uri);
			
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			
			JsonModelBuilder builder = new JsonModelBuilder();
			JsonReader reader = new JsonReader(builder);
			reader.parseJson(entity.getContent());
			
			JsonObject viewResult = (JsonObject) builder.getResult();
			JsonArray rows = (JsonArray) viewResult.get("rows");
			
			List<T> result = new LinkedList<T>();
			for (Object row : rows) {
				JsonObject rowObject = (JsonObject) row;
				result.add(mapper.mapRow(rowObject));
			}
			return result;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return Collections.emptyList();
	}

}
