package com.github.rasifix.trainings.couchdb;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.saj.dom.JsonArray;
import com.github.rasifix.saj.dom.JsonObject;

public class ViewResult {
	
	private final JsonObject result;

	public ViewResult(JsonObject result) {
		this.result = result;
	}
	
	public List<Row> getRows() {
		List<Row> list = new LinkedList<ViewResult.Row>();
		
		JsonArray array = result.getArray("rows");
		for (Object next : array) {
			list.add(new Row((JsonObject) next));
		}
		
		return list;
	}

	public Collection<DocumentRevision> map(RowMapper<DocumentRevision> mapper) {
		Collection<DocumentRevision> result = new LinkedList<DocumentRevision>();
		for (Row row : getRows()) {
			result.add(mapper.mapRow(row.getId(), row.getKey(), row.getValue()));
		}
		return result;
	}
	
	public static class Row {

		private final JsonObject row;

		Row(JsonObject row) {
			this.row = row;
		}
		
		public String getId() {
			return row.getString("id");
		}
		
		public Object getKey() {
			return row.get("key");
		}
		
		public Object getValue() {
			return row.get("value");
		}
		
	}
	
}
