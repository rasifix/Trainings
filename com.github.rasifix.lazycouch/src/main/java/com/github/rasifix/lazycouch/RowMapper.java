package com.github.rasifix.lazycouch;


public interface RowMapper<T> {
	
	T mapRow(String id, Object key, Object value);
	
}
