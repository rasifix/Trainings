package com.github.rasifix.lazycouch;

import java.io.IOException;
import java.util.Collection;

public interface CouchDatabase {
	
	Collection<DocumentRevision> getAllDesignDocuments() throws IOException;
	
	Document get(String id) throws IOException;
	
	void put(Document doc) throws IOException;
	
	void delete(Document doc) throws IOException;
	
	void delete(String id, String revision) throws IOException;
	
	CouchQuery createQuery(String designDocument, String viewName) throws IOException;
	
}
