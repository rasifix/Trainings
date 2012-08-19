package com.github.rasifix.trainings.model;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.model.attr.AttributeSummary;

public class TracklessActivity implements Activity {

	private final Collection<Equipment> equipments = new LinkedList<Equipment>();
	private final Date startTime;
	private String sport = "unknown";
	private int distance;
	private int duration;
	private int avghr;
	private String id;

	public TracklessActivity(Date startTime) {
		this.startTime = startTime;
	}
	
	@Override
	public Date getStartTime() {
		return startTime;
	}
	
	@Override
	public Date getEndTime() {
		return new Date(startTime.getTime() + duration * 1000L);
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
		return distance;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public Double getSpeed() {
		return duration != 0 ? 1.0 * distance / duration : null;
	}

	@Override
	public <T extends AttributeSummary<T>> T getSummary(AttributeSummaryBuilder<T> builder) {
		return null;
	}

	@Override
	public List<String> getPlaces() {
		return new LinkedList<String>();
	}

	@Override
	public List<Track> getTracks() {
		return Collections.emptyList();
	}
	
	@Override
	public int getTrackCount() {
		return 0;
	}
	
	@Override
	public Track getTrack(int trackIdx) {
		throw new IndexOutOfBoundsException("index " + trackIdx + " is invalid");
	}

	public void setId(String id) {
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
	public Collection<Equipment> getEquipments() {
		return Collections.unmodifiableCollection(equipments);
	}
	
	@Override
	public void addEquipment(Equipment equipment) {
		equipments.add(equipment);
	}

	public void setAvgHr(int avghr) {
		this.avghr = avghr;
	}
	
	public int getAvgHr() {
		return avghr;
	}
	
}
