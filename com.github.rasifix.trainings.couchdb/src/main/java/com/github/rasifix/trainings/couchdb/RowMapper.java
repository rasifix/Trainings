package com.github.rasifix.trainings.couchdb;

import com.github.rasifix.saj.dom.JsonObject;

public interface RowMapper<T> {
	
	T mapRow(JsonObject row);
	
}
