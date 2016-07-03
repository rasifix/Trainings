package com.github.rasifix.trainings.strava;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.rasifix.trainings.model.Activity;

public class ActivityParserTest {

	@Test
	public void parseSample() throws Exception {
		ActivityParser parser = new ActivityParser();
		Activity activity = parser.parseActivity(getClass().getClassLoader().getResourceAsStream("activity.json"));
		
		assertEquals("625692047", activity.getId());
		assertEquals("Ride", activity.getSport());
	}
	
}
