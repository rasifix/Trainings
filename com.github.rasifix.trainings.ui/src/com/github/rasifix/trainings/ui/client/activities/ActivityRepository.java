package com.github.rasifix.trainings.ui.client.activities;

import com.github.rasifix.trainings.ui.client.ViewResult;
import com.google.gwt.http.client.Request;

public interface ActivityRepository {
	
	void findActivities(int month, int year, Callback<ViewResult<ActivityOverview>> callback);
	
	interface Callback<T> {
		
		void onSuccess(T result);
		
		void onError(Request request, Throwable exception);
		
	}
	
}
