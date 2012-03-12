package com.github.rasifix.trainings.ui.client.activities;

import com.google.gwt.core.client.JavaScriptObject;

public class ActivityOverview extends JavaScriptObject {
	
	protected ActivityOverview() { }
	
	public final native String getSport() /*-{
		return this.sport;
	}-*/;
	
	public final native int getTime() /*-{
		return this.totalTime;
	}-*/;
	
	public final native int getDistance() /*-{
		return this.distance;
	}-*/;
	
	public final native int getHr() /*-{
		if (this.hr && this.hr.avg) {
			return this.hr.avg;
		}
		return -1;
	}-*/;

	public final native double getSpeed() /*-{
		return this.speed;
	}-*/;
	
	public final native String getFirstPlace() /*-{
		return this.places[0];
	}-*/;
	
	public final native String getLastPlace() /*-{
		return this.places[this.places.length - 1];
	}-*/;
	
}
