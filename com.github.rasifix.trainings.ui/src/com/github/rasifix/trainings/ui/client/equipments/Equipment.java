package com.github.rasifix.trainings.ui.client.equipments;

import com.google.gwt.core.client.JavaScriptObject;

public class Equipment extends JavaScriptObject {
	
	protected Equipment() { }
	
	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final native String getBrand() /*-{
		return this.brand;
	}-*/;
	
	public final native String dateOfPurchase() /*-{
		return this.dateOfPurchase;
	}-*/;

}
