package com.github.rasifix.lazycouch;

import java.util.List;

public interface CouchQuery {

	CouchQuery key(String key);
	
	CouchQuery descending();
	
	CouchQuery setStartKey(String key);
	
	CouchQuery setEndKey(String key);
	
	CouchQuery reduce(boolean reduce);
	
	/**
	 * Limit the number of documents in the output to <var>limit</var>.
	 * 
	 * @param limit the number of documents in the output
	 * @return this object
	 */
	CouchQuery limit(int limit);
	
	/**
	 * Skip n number of documents.
	 * 
	 * @param n the number of documents to skip
	 * @return this object
	 */
	CouchQuery skip(int n);
	
	<T> List<T> query(RowMapper<T> mapper);
	
}
