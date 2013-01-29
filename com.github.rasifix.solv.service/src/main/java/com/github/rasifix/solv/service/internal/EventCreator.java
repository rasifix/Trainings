package com.github.rasifix.solv.service.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.rasifix.solv.Athlete;
import com.github.rasifix.solv.Category;
import com.github.rasifix.solv.Event;
import com.github.rasifix.solv.service.internal.ResultListParser.ResultListEntry;
import com.github.rasifix.solv.service.internal.ResultListParser.Split;
import com.github.rasifix.solv.service.internal.StartListParser.StartListEntry;

class EventCreator {
	
	Event createEvent(int year, String name, List<StartListEntry> startList, List<ResultListEntry> resultList) {
		Event event = new Event(year, name);
		Map<String, StartListEntry> startListIndex = index(startList);
		
		for (ResultListEntry entry : resultList) {
			StartListEntry startListEntry = startListIndex.get(key(entry.name, entry.yearOfBirth));
			if (startListEntry == null) {
				System.err.println("skipping " + entry.name + " because no start list entry found");
				continue;
			}
			Category category = event.getCategory(startListEntry.category);
			if (category == null) {
				System.out.println("creating category " + startListEntry.category);
				category = new Category(event, startListEntry.category);
				event.addCategory(category);
			}
			
			Athlete athlete = new Athlete(entry.name);
			athlete.setCity(startListEntry.place);
			athlete.setClub(startListEntry.club);
			athlete.setSiCardNumber(startListEntry.siCardNumber);
			athlete.setStartTime(startListEntry.startTime);
			athlete.setYearOfBirth(startListEntry.yearOfBirth);
			athlete.setTotalTime(entry.totalTime);
			
			List<String> controls = new LinkedList<String>();
			for (Split split : entry.splits) {
				controls.add(split.control);
				athlete.addSplit(new com.github.rasifix.solv.Split(split.control, split.time));
			}
			category.setControls(controls);
			
			category.addAthlete(athlete);
		}
		
		return event;
	}

	private Map<String, StartListEntry> index(List<StartListEntry> startList) {
		Map<String, StartListEntry> result = new HashMap<String, StartListParser.StartListEntry>();
		for (StartListEntry entry : startList) {
			if (result.put(key(entry.athlete, entry.yearOfBirth), entry) != null) {
				System.err.println("overrode existing entry for " + entry.athlete);
			}
		}
		return result;
	}

	private String key(String athlete, int yearOfBirth) {
		if (athlete.length() > "Stefan Aschwanden-Lich".length()) {
			athlete = athlete.substring(0, "Stefan Aschwanden-Lich".length());
		}
		return athlete + "---" + yearOfBirth;
	}
	
}
