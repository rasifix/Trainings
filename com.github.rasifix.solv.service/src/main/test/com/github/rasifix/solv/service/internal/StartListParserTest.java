package com.github.rasifix.solv.service.internal;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import com.github.rasifix.solv.Time;
import com.github.rasifix.solv.service.internal.StartListParser.StartListEntry;

public class StartListParserTest {
	
	@Test
	public void testParse() throws Exception {
		StartListParser parser = new StartListParser();
		List<StartListEntry> result = parser.parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("full-start-list.html"))));
		assertEquals(1523, result.size());
		
		assertEntry(result.get(0), "59364", "Thomas Hofer", 83, "Huttwil", "OLG Huttwil", "10:18");
	}

	private void assertEntry(
			StartListEntry entry, String siCardNumber, String name, int yearOfBirth, String city, String club, String time) {
		
		assertEquals(siCardNumber, entry.siCardNumber);
		assertEquals(name, entry.athlete);
		assertEquals(yearOfBirth, entry.yearOfBirth);
		assertEquals(city, entry.place);
		assertEquals(club, entry.club);
		assertEquals(Time.valueOf(time, Time.TimeFormat.HOUR_MINUTE), entry.startTime);
	}
	
}
