package com.github.rasifix.trainings.ui.client;

import com.google.gwt.core.client.JavaScriptObject;

public class ViewRow<T> extends JavaScriptObject {
	
	protected ViewRow() { }
	
	public final native String getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getKey() /*-{
		return this.key;
	}-*/;
	
	public final native T getValue() /*-{
		return this.value;
	}-*/;
	
}
