package com.github.rasifix.lazycouch.internal;

import com.github.rasifix.lazycouch.CouchDatabase;
import com.github.rasifix.lazycouch.CouchServer;

public class CouchServerImpl implements CouchServer {

	private String hostname;
	
	private int port;

	public CouchServerImpl(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	@Override
	public CouchDatabase getDatabase(String databaseName) {
		return new CouchDatabaseImpl(hostname, port, databaseName);
	}

}
