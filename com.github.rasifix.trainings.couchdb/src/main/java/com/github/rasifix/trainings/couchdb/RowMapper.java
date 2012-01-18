package com.github.rasifix.trainings.couchdb;


public interface RowMapper<T> {
	
	T mapRow(String id, Object key, Object value);
	
}
