package com.github.rasifix.solv.service.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import com.github.rasifix.solv.Athlete;
import com.github.rasifix.solv.Event;
import com.github.rasifix.solv.Time;
import com.github.rasifix.solv.service.internal.ResultListParser.ResultListEntry;
import com.github.rasifix.solv.service.internal.StartListParser.StartListEntry;

public class EventCreatorTest {
	
	@Test
	public void testCreateEvent() throws Exception {
		List<StartListEntry> startList = new StartListParser().parse(getResource("full-start-list.html"));
		List<ResultListEntry> resultList = new ResultListParser().parse(getResource("full-result-list.csv"));
		
		EventCreator creator = new EventCreator();
		Event event = creator.createEvent(2012, "LOM", startList, resultList);
		
		assertEquals(2012, event.getYear());
		assertEquals("LOM", event.getTitle());
		
		assertNotNull("has category HAL", event.getCategory("HAL"));
		
		System.err.flush();
		List<Athlete> athletesAtControl = event.getAthletesAtControl("47");
		Collections.sort(athletesAtControl, timeComparator("47"));
		for (Athlete athlete : athletesAtControl) {
			Time time = athlete.getStartTime().add(athlete.getSplit("47").getTime());
			System.out.println(time.format(Time.TimeFormat.HOUR_MINUTE_SECOND) + " - " + athlete.getName());
		}
	}

	private Comparator<? super Athlete> timeComparator(final String controlCode) {
		return new Comparator<Athlete>() {
			@Override
			public int compare(Athlete o1, Athlete o2) {
				Time t1 = o1.getStartTime().add(o1.getSplit(controlCode).getTime());
				Time t2 = o2.getStartTime().add(o2.getSplit(controlCode).getTime());
				return t1.compareTo(t2);
			}
		};
	}

	private BufferedReader getResource(String name) {
		return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(name)));
	}
	
}
