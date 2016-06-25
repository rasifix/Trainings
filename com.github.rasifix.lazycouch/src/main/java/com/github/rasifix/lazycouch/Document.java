package com.github.rasifix.lazycouch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Document {

	private final ObjectNode node;
	
	public Document(String id) {
		this(id, null);
	}
	
	public Document(String id, String rev) {
		node = new ObjectNode(new JsonNodeFactory(false));
		put("_id", id);
		if (rev != null) {
			put("_rev", rev);
		}
	}
	
	public void put(String name, String value) {
		node.put(name, value);
	}
	
	public void set(String name, JsonNode value) {
		node.set(name, value);
	}
	
	public String getId() {
		return node.path("_id").asText();
	}
	
	public String getRev() {
		return node.path("_rev").asText();
	}
	
	public JsonNode path(String path) {
		return node.path(path);
	}

	public ObjectNode getNode() {
		return node;
	}
	
}
