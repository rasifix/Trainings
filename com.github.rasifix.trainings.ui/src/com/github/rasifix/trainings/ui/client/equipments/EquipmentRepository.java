package com.github.rasifix.trainings.ui.client.equipments;

import com.github.rasifix.trainings.ui.client.ViewResult;
import com.google.gwt.http.client.Request;

public interface EquipmentRepository {
	
	void findEquipments(Callback<ViewResult<Equipment>> callback);
	
	interface Callback<T> {
		
		void onSuccess(T result);
		
		void onError(Request request, Throwable exception);
		
	}
	
}
