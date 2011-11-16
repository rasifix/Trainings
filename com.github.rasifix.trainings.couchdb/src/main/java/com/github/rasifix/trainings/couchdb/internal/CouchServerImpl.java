package com.github.rasifix.trainings.couchdb.internal;

import com.github.rasifix.trainings.couchdb.CouchDatabase;
import com.github.rasifix.trainings.couchdb.CouchServer;

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
