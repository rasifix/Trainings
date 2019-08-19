package com.github.rasifix.trainings.strava;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.strava.dto.ActivityOverviewDTO;

public class ActivityParser {
	
	public StravaActivity parseActivity(InputStream is) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ActivityOverviewDTO overview = mapper.readValue(is, ActivityOverviewDTO.class);
		
		StravaActivity result = new StravaActivity(overview.getActivityId());
		result.setSport(overview.getSport());
		result.setStartTime(overview.getDate());
		result.setGearId(overview.getGearId());
		
		return result;
	}
	
}
