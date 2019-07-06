package com.github.rasifix.trainings.strava.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.rasifix.trainings.ActivityRepository;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ActivityOverviewDTO implements ActivityRepository.ActivityOverview {

	private final String id;
	
	@JsonProperty("start_date")
	private Date date;

	@JsonProperty("average_heartrate")
	private Integer averageHr;
	
	private int distance;

	@JsonProperty("elapsed_time")
	private long duration;

	@JsonProperty("type")
	private String sport;
	
	@JsonProperty("gear_id")
	private String gearId;

	@JsonCreator
	ActivityOverviewDTO(@JsonProperty("id") String id) {
		this.id = id;
	}
	
	@Override
	public Date getDate() {
		return date;
	}
	
	void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String getSport() {
		return sport;
	}
	
	void setSport(String sport) {
		if ("Ride".equals(sport)) {
			this.sport = "CYCLING";
		} else if ("Run".equals(sport)) {
			this.sport = "RUNNING";
		} else {
			this.sport = sport.toUpperCase();
		}
	}

	@Override
	public long getDuration() {
		return duration;
	}
	
	void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public int getDistance() {
		return distance;
	}
	
	void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public String getActivityId() {
		return id;
	}

	@Override
	public Integer getAverageHeartRate() {
		return averageHr;
	}
	
	public String getGearId() {
		return gearId;
	}
	
	public void setGearId(String gearId) {
		this.gearId = gearId;
	}
	
}
