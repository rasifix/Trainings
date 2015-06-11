package com.github.rasifix.trainings.format.fit.internal;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.util.List;

import org.junit.Test;

import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.SpeedAttribute;

public class FitReaderTest {

	@Test
	public void testActivityWithStoppedTimer() throws Exception {
		FitReader reader = new FitReader();
		List<Activity> activities = reader.readActivities(new FileInputStream("20120126-165823-1-1345-ANTFS-4.FIT"));
		assertEquals(1, activities.size());
		
		Activity activity = activities.get(0);
		assertEquals(2, activity.getTrackCount());
		
		Track t1 = activity.getTrack(0);
		assertEquals("CYCLING", t1.getSport());
		
		Track t2 = activity.getTrack(1);
		assertEquals("CYCLING", t2.getSport());
		
		// TODO s
		// - track starts at distance 0?
		// - calculate time excluding stops (update distance?)
		// - filtered trackpoint list?
		//   - all trackpoints with position + distance?
		//   - all trackpoints with heart rate
		//   - ...
		// - track.getTrackpointSequence():Sequence<Trackpoint>
		//   - Sequence.getPrevious(tp:Trackpoint):Trackpoint
		//   - Sequence.getNext(tp:Trackpoint):Trackpoint
		//   - Sequence.withAttributes(DistanceAttribute.class):Sequence<Trackpoint> 
		//   - Sequence.delta(trackpoint, DistanceAttribute.class)
		//   - Trackpoint.getDelta(DistanceAttribute.class):double
		//   - Trackpoint.getTimeDelta():long
		// - Trackpoint is an interface? Allows efficient implementation of Sequence
		
		long totalTime = (long) t2.getDuration() * 1000; 
		Double lastDistance = null;
		Long lastElapsedTime = null;
		double deltasum = 0;
		for (Trackpoint trackpoint : t2.getTrackpoints()) {
			if (lastElapsedTime == null) {
				lastElapsedTime = trackpoint.getElapsedTime();
				continue;
			}
			if (trackpoint.hasAttribute(SpeedAttribute.class)) {
				double speed = trackpoint.getAttribute(SpeedAttribute.class).getValue();
				if (speed < 0.1) {
					if (lastDistance != null) {
						double delta = trackpoint.getAttribute(DistanceAttribute.class).getValue() - lastDistance;
						deltasum += delta;
						System.out.println(speed + " -- " + delta);
					}
					totalTime -= (trackpoint.getElapsedTime() - lastElapsedTime);
				}
				lastElapsedTime = trackpoint.getElapsedTime(); 
			}
			lastDistance = trackpoint.getAttribute(DistanceAttribute.class).getValue();
		}
		
		System.out.println(totalTime / 1000 + " / " + t2.getDuration());
		System.out.println(t2.getDistance() / (totalTime / 1000.0) * 3.6 + " / " + t2.getSpeed() * 3.6);
		System.out.println(deltasum);
	}

	@Test
	public void testBrokenActivity() throws Exception {
		FitReader reader = new FitReader();
		List<Activity> activities = reader.readActivities(getClass().getResourceAsStream("a.fit"));
		assertEquals(1, activities.size());
		
		Activity activity = activities.get(0);
		assertEquals(1, activity.getTrackCount());
		
		Track t1 = activity.getTrack(0);
		assertEquals("RUNNING", t1.getSport());
		
		// TODO s
		// - track starts at distance 0?
		// - calculate time excluding stops (update distance?)
		// - filtered trackpoint list?
		//   - all trackpoints with position + distance?
		//   - all trackpoints with heart rate
		//   - ...
		// - track.getTrackpointSequence():Sequence<Trackpoint>
		//   - Sequence.getPrevious(tp:Trackpoint):Trackpoint
		//   - Sequence.getNext(tp:Trackpoint):Trackpoint
		//   - Sequence.withAttributes(DistanceAttribute.class):Sequence<Trackpoint> 
		//   - Sequence.delta(trackpoint, DistanceAttribute.class)
		//   - Trackpoint.getDelta(DistanceAttribute.class):double
		//   - Trackpoint.getTimeDelta():long
		// - Trackpoint is an interface? Allows efficient implementation of Sequence
		
		long totalTime = (long) t1.getDuration() * 1000; 
		Double lastDistance = null;
		Long lastElapsedTime = null;
		double deltasum = 0;
		for (Trackpoint trackpoint : t1.getTrackpoints()) {
			if (lastElapsedTime == null) {
				lastElapsedTime = trackpoint.getElapsedTime();
				continue;
			}
			if (trackpoint.hasAttribute(SpeedAttribute.class)) {
				double speed = trackpoint.getAttribute(SpeedAttribute.class).getValue();
				if (speed < 0.1) {
					if (lastDistance != null) {
						double delta = trackpoint.getAttribute(DistanceAttribute.class).getValue() - lastDistance;
						deltasum += delta;
						System.out.println(speed + " -- " + delta);
					}
					totalTime -= (trackpoint.getElapsedTime() - lastElapsedTime);
				}
				lastElapsedTime = trackpoint.getElapsedTime(); 
			}
			if (trackpoint.hasAttribute(DistanceAttribute.class)) {
				lastDistance = trackpoint.getAttribute(DistanceAttribute.class).getValue();
			}
		}
		
		System.out.println(totalTime / 1000 + " / " + t1.getDuration());
		System.out.println(t1.getDistance() / (totalTime / 1000.0) * 3.6 + " / " + t1.getSpeed() * 3.6);
		System.out.println(deltasum);
	}

}
