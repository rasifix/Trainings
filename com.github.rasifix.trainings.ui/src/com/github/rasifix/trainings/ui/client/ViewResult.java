package com.github.rasifix.trainings.ui.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class ViewResult<T extends JavaScriptObject> extends JavaScriptObject {
	
	protected ViewResult() { }
	
	public final native int getTotalRows() /*-{
		return this["total_rows"];
	}-*/;
	
	public final native JsArray<ViewRow<T>> getRows() /*-{
		return this.rows;
	}-*/;
	
}
