package com.github.rasifix.lazycouch;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ViewResult {
	
	private final ObjectNode result;

	public ViewResult(ObjectNode result) {
		this.result = result;
	}
	
	public List<Row> getRows() {
		List<Row> list = new LinkedList<ViewResult.Row>();
		
		ArrayNode array = (ArrayNode) result.path("rows");
		for (Object next : array) {
			list.add(new Row((ObjectNode) next));
		}
		
		return list;
	}

	public Collection<DocumentRevision> map(RowMapper<DocumentRevision> mapper) {
		Collection<DocumentRevision> result = new LinkedList<DocumentRevision>();
		for (Row row : getRows()) {
			Object key = row.getKey();
			result.add(mapper.mapRow(row.getId(), key, row.getValue()));
		}
		return result;
	}
	
	public static class Row {

		private final ObjectNode row;

		Row(ObjectNode row) {
			this.row = row;
		}
		
		public String getId() {
			return row.path("id").asText();
		}
		
		public Object getKey() {
			return row.get("key");
		}
		
		public Object getValue() {
			return row.get("value");
		}
		
	}
	
}
