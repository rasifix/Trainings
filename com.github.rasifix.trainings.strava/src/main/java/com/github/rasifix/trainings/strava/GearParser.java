package com.github.rasifix.trainings.strava;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rasifix.trainings.model.Equipment;
import com.github.rasifix.trainings.strava.dto.GearDTO;

public class GearParser {

	public Equipment parseGear(InputStream is) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		GearDTO dto = mapper.readValue(is, GearDTO.class);
		
		Equipment result = new Equipment();
		result.setId(dto.getId());
		result.setBrand(dto.getBrand());
		result.setName(dto.getModel());
		
		return result;
	}

}
