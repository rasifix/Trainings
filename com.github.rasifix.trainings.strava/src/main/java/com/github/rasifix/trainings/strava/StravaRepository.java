package com.github.rasifix.trainings.strava;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.github.rasifix.trainings.ActivityKey;
import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;

@Component(property = { "name=strava", "token=fbdf85eb6bf4a59a365885b8679401ab27f02379" })
//@Designate(ocd = StravaConfig.class)
public class StravaRepository implements ActivityRepository {

	private static final String BASE_URI = "https://www.strava.com/api/v3/";
	
	private String token;
	private DefaultHttpClient client;

//	@ObjectClassDefinition
//	@interface StravaConfig {
//		String token();
//	}
	
	@Activate
	public void activate(ComponentContext context) throws IOException {
		System.out.println("... activating strava repository with pid = " + context.getServiceReference());
		this.token = (String) context.getProperties().get("token");
		this.client = new DefaultHttpClient();
		
		System.out.println(context.getProperties());
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
		
		System.out.println("token = " + token);
		
		HttpResponse response = client.execute(request);
		
		ActivitiesParser parser = new ActivitiesParser();
		return parser.parseActivities(response.getEntity().getContent());
	}

}
