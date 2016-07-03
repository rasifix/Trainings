package com.github.rasifix.trainings.strava;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rasifix.trainings.ActivityRepository.ActivityOverview;
import com.github.rasifix.trainings.strava.dto.ActivityOverviewDTO;

public class ActivitiesParser {
	
	public List<ActivityOverview> parseActivities(InputStream content) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(content, new TypeReference<List<ActivityOverviewDTO>>(){});
	}

}
