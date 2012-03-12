package com.github.rasifix.trainings.ui.client.activities;

import com.github.rasifix.trainings.ui.client.ViewResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

public class ActivityRepositoryImpl implements ActivityRepository {
	
	protected final native ViewResult<ActivityOverview> asViewResult(String json) /*-{
		return JSON.parse(json);
	}-*/;

	@Override
	public void findActivities(int month, int year, final Callback<ViewResult<ActivityOverview>> callback) {
		String startYear = Integer.toString(year);
		String startMonth = pad(month);
		String endYear = month == 12 ? Integer.toString(year + 1) : Integer.toString(year);
		String endMonth = pad(month + 1);
		
		// Send request to server and catch any errors.
		String url = "http://localhost:5984/trainings/_design/app/_view/overview?"
		           + "startkey=" + key(startYear, startMonth) + "&endkey=" + key(endYear, endMonth);
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
						ViewResult<ActivityOverview> result = asViewResult(text);
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

	private String key(String year, String month) {
		return URL.encodeQueryString("\"" + year + "-" + month + "\"");
	}

	private String pad(int month) {
		if (month < 10) {
			return "0" + month;
		} else if (month > 12) {
			return "01";
		}
		return Integer.toString(month);
	}

}
