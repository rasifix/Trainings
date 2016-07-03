package com.github.rasifix.trainings.strava;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.github.rasifix.trainings.ActivityRepository.ActivityOverview;

public class ActivitiesParserTest {

	@Test
	public void parseSample() throws Exception {
		ActivitiesParser parser = new ActivitiesParser();
		List<ActivityOverview> activities = parser.parseActivities(getClass().getClassLoader().getResourceAsStream("activities.json"));
		assertEquals(2, activities.size());
		
		assertActivityEquals("625692047", "Ride", date("2016-06-30T17:40:24Z"), 1618, 10832, 118, activities.get(0));
		assertActivityEquals("625441803", "Run", date("2016-06-30T11:38:15Z"), 1982, 5543, 132, activities.get(1));
	}

	private static void assertActivityEquals(String id, String sport, Date date, int duration, int distance, Integer averageHr, ActivityOverview actual) {
		assertEquals(id, actual.getActivityId());
		assertEquals(sport, actual.getSport());
		assertEquals(date, actual.getDate());
		assertEquals(duration, actual.getDuration());
		assertEquals(distance, actual.getDistance());
		assertEquals(averageHr, actual.getAverageHeartRate());
	}

	private Date date(String value) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(value);
	}
	
}
