package com.github.rasifix.trainings.couchdb;

import java.io.IOException;

public interface CouchDatabase {
	
	Document getById(String id);
	
	void put(Document doc) throws IOException;
	
	void delete(Document doc);
	
}
