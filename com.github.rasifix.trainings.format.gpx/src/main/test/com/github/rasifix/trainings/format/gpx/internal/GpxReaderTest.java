package com.github.rasifix.trainings.format.gpx.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;

public class GpxReaderTest {
	
	@Test
	public void testReadGpxTrack() throws Exception {
		GpxReader reader = new GpxReader();
		InputStream inputStream = getClass().getResourceAsStream("/com/github/rasifix/trainings/format/gpx/internal/2012-01-01-1445.gpx");
		List<Activity> activities = reader.readActivities(inputStream);
		
		assertEquals(1, activities.size());
		
		Activity activity = activities.get(0);
		assertEquals(date("2012-01-01 15:45:47"), activity.getStartTime());
		assertEquals(1, activity.getTrackCount());
		
		Track track = activity.getTrack(0);
		assertEquals(date("2012-01-01 15:45:47"), track.getStartTime());
		assertEquals(703, track.getTrackpointCount());
		assertEquals(8325, track.getDistance());
		assertEquals(47 * 60 + 49, track.getDuration());
		
		assertTrackpoint(0, 47.015954000, 7.455408000, 563.0, track, 0);
		assertTrackpoint(73 * 1000, 47.016458000, 7.454054000, 561.6, track, 10);
		assertTrackpoint((47 * 60 + 49) * 1000, 47.016537000, 7.453624000, 562.0, track, 702);
	}

	private void assertTrackpoint(int elapsedTime, double lat, double lon, double ele, Track track, int trackpointIndex) {
		Trackpoint trackpoint = track.getTrackpoint(trackpointIndex);
		assertEquals("elapsedTime", elapsedTime, trackpoint.getElapsedTime());
		assertEquals("lat", lat, trackpoint.getPosition().getLatitude(), 1e-9);
		assertEquals("lon", lon, trackpoint.getPosition().getLongitude(), 1e-9);
		assertEquals("ele", ele, trackpoint.getAttribute(AltitudeAttribute.class).getAltitude(), 1e-9);
	}

	private Date date(String date) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
	}

	
	@Test
	public void testReadGpxTrackWithHr() throws Exception {
		GpxReader reader = new GpxReader();
		InputStream inputStream = getClass().getResourceAsStream("/com/github/rasifix/trainings/format/gpx/internal/gpx_with_hr.gpx");
		List<Activity> activities = reader.readActivities(inputStream);
		
		assertEquals(1, activities.size());
		
		Activity activity = activities.get(0);
		assertEquals(1, activity.getTrackCount());
		
		Track track = activity.getTrack(0);
		assertEquals(1538, track.getTrackpointCount());
		assertEquals(10751, track.getDistance());
		assertEquals(1537, track.getDuration());
		
		Trackpoint trackpoint = track.getTrackpoint(0);
		HeartRateAttribute hr = trackpoint.getAttribute(HeartRateAttribute.class);
		assertNotNull(hr);
		assertEquals(72, hr.getValue().intValue());
	}

}
