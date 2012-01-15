package com.github.rasifix.trainings.couchdb;

import java.util.List;

public interface CouchQuery {

	void setStartKey(String key);
	
	void setEndKey(String key);
	
	<T> List<T> query(RowMapper<T> mapper);
	
}
