package com.github.rasifix.trainings.strava.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GearDTO {

	private String id;
	
	private String name;
	
	@JsonProperty("brand_name")
	private String brand;
	
	@JsonProperty("model_name")
	private String model;
	
	private float distance;
	
	private String description;

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getBrand() {
		return brand;
	}
	
	public String getModel() {
		return model;
	}
	
	public String getDescription() {
		return description;
	}
	
	public float getDistance() {
		return distance;
	}

}
