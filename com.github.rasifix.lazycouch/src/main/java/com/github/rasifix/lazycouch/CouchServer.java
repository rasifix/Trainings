package com.github.rasifix.lazycouch;

public interface CouchServer {
	
	CouchDatabase getDatabase(String databaseName);
	
}
