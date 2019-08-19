package com.github.rasifix.trainings.strava;

import java.util.Collection;
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
	private String gearId;
	private Collection<Equipment> equipments = new LinkedList<>();

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
		return tracks.get(0).getDistance();
	}

	@Override
	public int getDuration() {
		return tracks.get(0).getDuration();
	}

	@Override
	public Double getSpeed() {
		return tracks.get(0).getSpeed();
	}

	@Override
	public <T extends AttributeSummary<T>> T getSummary(AttributeSummaryBuilder<T> builder) {
		return tracks.get(0).getSummary(builder);
	}

	@Override
	public List<String> getPlaces() {
		return tracks.get(0).getPlaces();
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
		return new Date(getStartTime().getTime() + getDuration());
	}

	@Override
	public Collection<Equipment> getEquipments() {
		return equipments;
	}

	@Override
	public void addEquipment(Equipment equipment) {
		this.equipments.add(equipment);
	}

	public void setGearId(String gearId) {
		this.gearId = gearId;
	}
	
	public String getGearId() {
		return gearId;
	}

}
