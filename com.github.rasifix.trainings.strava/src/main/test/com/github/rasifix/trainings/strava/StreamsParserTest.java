package com.github.rasifix.trainings.strava;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;

public class StreamsParserTest {

	@Test
	public void parseSample() throws Exception {
		StreamsParser parser = new StreamsParser();
		List<Track> tracks = parser.parseStreams(new Date(0), getClass().getClassLoader().getResourceAsStream("streams.json"));
		
		assertEquals(1, tracks.size());
		
		Track track = tracks.get(0);
		assertEquals(1618, track.getTrackpointCount());
		
		assertTrackpoint(0, 4.9, 565.6, 108, 46.932991, 7.417334, track.getTrackpoint(0));
		assertTrackpoint(6, 29.6, 565.7, 108, 46.93311, 7.417105, track.getTrackpoint(5));
	}

	private void assertTrackpoint(long time, double distance, double altitude, int hr, double lat, double lng, Trackpoint actual) {
		assertEquals(time, actual.getElapsedTime());
		assertEquals(distance, actual.getAttribute(DistanceAttribute.class).getValue().doubleValue(), 0.001);
		assertEquals(altitude, actual.getAttribute(AltitudeAttribute.class).getValue().doubleValue(), 0.001);
		assertEquals(hr, actual.getAttribute(HeartRateAttribute.class).getValue().intValue(), 0.001);
		assertEquals(lat, actual.getAttribute(PositionAttribute.class).getValue().getLatitude(), 0.001);
		assertEquals(lng, actual.getAttribute(PositionAttribute.class).getValue().getLongitude(), 0.001);
	}
	
}
