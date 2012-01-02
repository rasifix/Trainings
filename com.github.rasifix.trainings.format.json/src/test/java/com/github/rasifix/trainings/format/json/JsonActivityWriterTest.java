package com.github.rasifix.trainings.format.json;

import java.io.StringWriter;
import java.util.Date;

import org.junit.Test;

import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;


public class JsonActivityWriterTest {
	
	@Test
	public void testShouldWork() throws Exception {
		JsonActivityWriter out = new JsonActivityWriter();
		
		Date startTime = new Date();
		Activity activity = new Activity(startTime);
		
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
		
		System.out.println(writer.toString());		
	}
	
}
