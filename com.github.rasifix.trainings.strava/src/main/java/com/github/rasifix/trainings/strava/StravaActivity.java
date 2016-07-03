package com.github.rasifix.trainings.strava;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.AttributeSummaryBuilder;
import com.github.rasifix.trainings.model.Equipment;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.attr.AttributeSummary;

public class StravaActivity implements Activity {

	private final String id;
	private String sport;
	private List<Track> tracks = new LinkedList<>();
	private Date startTime;

	public StravaActivity(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getRevision() {
		return null;
	}
	
	@Override
	public String getSport() {
		return sport;
	}
	
	public void setSport(String sport) {
		this.sport = sport;
	}

	@Override
	public int getDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Double getSpeed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends AttributeSummary<T>> T getSummary(AttributeSummaryBuilder<T> builder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getPlaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Track> getTracks() {
		return tracks ;
	}

	@Override
	public int getTrackCount() {
		return tracks.size();
	}

	@Override
	public Track getTrack(int trackIdx) {
		return tracks.get(trackIdx);
	}

	@Override
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date dateTime) {
		this.startTime = dateTime;
	}

	@Override
	public Date getEndTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Equipment> getEquipments() {
		return Collections.emptyList();
	}

	@Override
	public void addEquipment(Equipment equipment) {
		throw new UnsupportedOperationException();
	}

}
