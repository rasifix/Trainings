package com.github.rasifix.trainings.couchdb;

public interface CouchServer {
	
	CouchDatabase getDatabase(String databaseName);
	
}
