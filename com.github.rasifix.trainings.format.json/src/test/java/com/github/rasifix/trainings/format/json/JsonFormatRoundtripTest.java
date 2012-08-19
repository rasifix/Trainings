package com.github.rasifix.trainings.format.json;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.ActivityImpl;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;

public class JsonFormatRoundtripTest {
	
	@Test
	@Ignore
	public void testRoundtrip() throws Exception {
		JsonActivityWriter out = new JsonActivityWriter();
		
		Date startTime = new Date();
		ActivityImpl activity = new ActivityImpl(startTime);
		
		Track t1 = new Track(startTime);
		
		Trackpoint tp1 = new Trackpoint(0);
		tp1.addAttribute(new HeartRateAttribute(120));
		tp1.addAttribute(new PositionAttribute(47, 7));
		tp1.addAttribute(new AltitudeAttribute(500));
		tp1.addAttribute(new DistanceAttribute(0));
		t1.addTrackpoint(tp1);
		
		Trackpoint tp2 = new Trackpoint(10);
		tp2.addAttribute(new HeartRateAttribute(124));
		tp2.addAttribute(new PositionAttribute(47, 7.001));
		tp2.addAttribute(new AltitudeAttribute(500));
		tp2.addAttribute(new DistanceAttribute(50));
		t1.addTrackpoint(tp2);
		
		activity.addTrack(t1);
		
		StringWriter writer = new StringWriter();
		out.writeActivity(activity, writer);
		
		JsonActivityReader reader = new JsonActivityReader();
		Activity clone = reader.readActivity(new StringReader(writer.toString()));
		
		assertEquals(activity.getStartTime(), clone.getStartTime());
		assertEquals(activity.getTrackCount(), clone.getTrackCount());
		
		for (int trackIdx = 0; trackIdx < activity.getTrackCount(); trackIdx++) {
			Track originalTrack = activity.getTrack(trackIdx);
			Track clonedTrack = clone.getTrack(trackIdx);
			
			assertEquals(originalTrack.getStartTime(), clonedTrack.getStartTime());
			assertEquals(originalTrack.getSport(), clonedTrack.getSport());
			assertEquals(originalTrack.getTrackpoints().size(), clonedTrack.getTrackpoints().size());
			
			for (int trackpointIdx = 0; trackpointIdx < originalTrack.getTrackpointCount(); trackpointIdx++) {
				Trackpoint originalTrackpoint = originalTrack.getTrackpoint(trackpointIdx);
				Trackpoint clonedTrackpoint = clonedTrack.getTrackpoint(trackpointIdx);
				
				assertEquals(originalTrackpoint.getPosition(), clonedTrackpoint.getPosition());
				assertEquals(originalTrackpoint.getAttribute(AltitudeAttribute.class), clonedTrackpoint.getAttribute(AltitudeAttribute.class));
			}
		}
	}
	
}
