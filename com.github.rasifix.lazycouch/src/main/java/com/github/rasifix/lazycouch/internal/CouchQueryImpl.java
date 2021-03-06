package com.github.rasifix.lazycouch.internal;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rasifix.lazycouch.CouchQuery;
import com.github.rasifix.lazycouch.RowMapper;

class CouchQueryImpl implements CouchQuery {
	
	private final CouchDatabaseImpl database;
	private final String designDocumentName;
	private final String viewName;
	private String startKey;
	private String endKey;
	private String key;
	private boolean descending;
	private Boolean reduce;
	private Integer limit;
	private Integer skip;

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
	public CouchQuery key(String key) {
		this.key = key;
		return this;
	}
	
	@Override
	public CouchQuery setStartKey(String startKey) {
		this.startKey = startKey;
		return this;
	}

	@Override
	public CouchQuery setEndKey(String endKey) {
		this.endKey = endKey;
		return this;
	}
	
	@Override
	public CouchQuery descending() {
		this.descending = true;
		return this;
	}
	
	@Override
	public CouchQuery reduce(boolean reduce) {
		this.reduce = Boolean.valueOf(reduce);
		return this;
	}
	
	@Override
	public CouchQuery limit(int limit) {
		this.limit = Integer.valueOf(limit);
		return this;
	}
	
	@Override
	public CouchQuery skip(int skip) {
		this.skip = Integer.valueOf(skip);
		return this;
	}

	@Override
	public <T> List<T> query(RowMapper<T> mapper) {
		HttpClient client = new DefaultHttpClient();
		
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		if (startKey != null) {
			qparams.add(new BasicNameValuePair("startkey", '"' + startKey + '"'));
		}		
		if (endKey != null) {
			qparams.add(new BasicNameValuePair("endkey", '"' + endKey + '"'));
		}
		if (key != null) {
			qparams.add(new BasicNameValuePair("key", '"' + key + '"'));
		}
		if (descending) {
			qparams.add(new BasicNameValuePair("descending", "true"));
		}
		if (reduce != null) {
			qparams.add(new BasicNameValuePair("reduce", reduce.toString()));
		}
		if (limit != null) {
			qparams.add(new BasicNameValuePair("limit", limit.toString()));
		}
		if (skip != null) {
			qparams.add(new BasicNameValuePair("skip", skip.toString()));
		}
		
		try {
			URI uri = URIUtils.createURI("http", getHost(), getPort(), "/" + getDatabaseName() + "/_design/" + designDocumentName + "/_view/" + viewName,
					URLEncodedUtils.format(qparams, "UTF-8"), null);
			HttpGet request = new HttpGet(uri);
			System.out.println(uri);
			
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode viewResult = objectMapper.readTree(entity.getContent());

			JsonNode rows = viewResult.path("rows");

			List<T> result = new LinkedList<T>();
			for (JsonNode row : rows) {
				String id = row.path("id").asText();
				JsonNode key = row.get("key");
				JsonNode value = row.get("value");
				result.add(mapper.mapRow(id, key, value));
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
