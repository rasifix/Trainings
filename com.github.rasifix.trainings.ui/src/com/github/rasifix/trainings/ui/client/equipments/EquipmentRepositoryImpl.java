package com.github.rasifix.trainings.ui.client.equipments;

import com.github.rasifix.trainings.ui.client.ViewResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class EquipmentRepositoryImpl implements EquipmentRepository {
	
	protected final native ViewResult<Equipment> asViewResult(String json) /*-{
		return JSON.parse(json);
	}-*/;

	@Override
	public void findEquipments(final Callback<ViewResult<Equipment>> callback) {
		// Send request to server and catch any errors.
		String url = "http://localhost:5984/trainings/_design/app/_view/equipments";
		RequestBuilder builder = new RequestBuilder(
				RequestBuilder.GET,
				url);

		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					callback.onError(request, exception);
				}

				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						String text = response.getText();
						System.out.println(text);
						ViewResult<Equipment> result = asViewResult(text);
						callback.onSuccess(result);
						
					} else {
						GWT.log(response.getStatusText());
					}
				}
			});

		} catch (RequestException e) {
			GWT.log("", e);
		}
	}

}
