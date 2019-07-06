package com.github.rasifix.trainings.strava;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.github.rasifix.trainings.ActivityKey;
import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE, property = "name=strava")
public class StravaRepository implements ActivityRepository {

	private static final String BASE_URI = "https://www.strava.com/api/v3/";
	
	private String token;
	private DefaultHttpClient client;

	@interface StravaConfig {
		String token();
	}
	
	@Activate
	public void activate(StravaConfig config) throws IOException {
		this.token = config.token();
		this.client = new DefaultHttpClient();
	}

	@Override
	public ActivityKey addActivity(Activity activity) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeActivity(String activityId, String revision) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Activity getActivity(String activityId) throws IOException {
		HttpGet activityRequest = new HttpGet(BASE_URI + "activities/" + activityId);
		activityRequest.setHeader("Authorization", "Bearer " + token);
		HttpResponse activityResponse = client.execute(activityRequest);
		
		ActivityParser parser = new ActivityParser();
		Activity activity = parser.parseActivity(activityResponse.getEntity().getContent());
		
		HttpGet streamsRequest = new HttpGet(BASE_URI + "activities/" + activityId + "/streams/latlng,time,distance,altitude,heartrate,cadence");
		streamsRequest.setHeader("Authorization", "Bearer " + token);
		HttpResponse streamsResponse = client.execute(streamsRequest);
		
		List<Track> tracks = new StreamsParser().parseStreams(activity.getStartTime(), streamsResponse.getEntity().getContent());
		tracks.stream().forEach(track -> track.setSport(activity.getSport()));
		activity.getTracks().addAll(tracks);
		
		return activity;
	}

	@Override
	public List<ActivityOverview> findActivities(Date startDate, Date endDate) throws IOException {
		HttpGet request = new HttpGet(BASE_URI + "activities?per_page=200");
		request.setHeader("Authorization", "Bearer " + token);
		
		HttpResponse response = client.execute(request);
		
		ActivitiesParser parser = new ActivitiesParser();
		return parser.parseActivities(response.getEntity().getContent());
	}

}
