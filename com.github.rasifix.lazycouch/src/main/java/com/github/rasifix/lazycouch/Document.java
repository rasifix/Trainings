package com.github.rasifix.lazycouch;

import com.github.rasifix.saj.dom.JsonObject;


public class Document extends JsonObject {

	public Document(String id) {
		this(id, null);
	}
	
	public Document(String id, String rev) {
		put("_id", id);
		if (rev != null) {
			put("_rev", rev);
		}
	}
	
	public String getId() {
		return getString("_id");
	}
	
	public String getRev() {
		return getString("_rev");
	}
	
}
