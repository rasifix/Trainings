/*
 * Copyright 2011 Simon Raess
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rasifix.trainings.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.github.rasifix.trainings.model.attr.AttributeSummary;


public class ActivityImpl implements Activity {

	private final List<Track> tracks = new LinkedList<Track>();
	
	private Date startTime;

	private String activityId;

	private String revision;

	private final Collection<Equipment> equipments = new HashSet<Equipment>();
	
	public ActivityImpl(final long startTime) {
		this(new Date(startTime));
	}
	
	public ActivityImpl(final Date startTime) {
		this.startTime = startTime;
	}

	public String getId() {
		return activityId;
	}
	
	public void setId(String id) {
		this.activityId = id;
	}

	public String getRevision() {
		return revision;
	}
	
	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getEndTime() {
		return new Date(startTime.getTime() + (long) (getDuration() * 1000));
	}
	
	// --> start of HasSummary <--
	
	@Override
	public int getDistance() {
		int distance = 0;
		for (Track track : tracks) {
			distance += track.getDistance();
		}
		return distance;
	}
	
	@Override
	public int getDuration() {
		int duration = 0;
		for (Track track : tracks) {
			duration += track.getDuration();
		}
		return duration;
	}
	
	@Override
	public Double getSpeed() {
		double resultSum = 0;
		for (Track track : tracks) {
			resultSum += track.getSpeed() * track.getDuration();
		}
		return resultSum / getDuration();
	}
	
	@Override
	public String getSport() {
		String sport = null;
		for (Track track : tracks) {
			if (sport == null) {
				sport = track.getSport();
			} else if (!sport.equals(track.getSport())) {
				sport = "Multisport";
			}
		}
		return sport;
	}
	
	@Override
	public <T extends AttributeSummary<T>> T getSummary(AttributeSummaryBuilder<T> builder) {
		T result = null;
		for (Track track : tracks) {
			T trackSummary = track.getSummary(builder);
			if (trackSummary == null) {
				System.out.println(builder + " produced null summary for track: " + track + "(" + track.getTrackpointCount() + ")");
				continue;
			}
			if (result == null) {
				result = trackSummary;
			} else {
				result = result.merge(trackSummary);
			}
		}
		return result;
	}
	
	@Override
	public List<String> getPlaces() {
		LinkedList<String> result = new LinkedList<String>();
		for (Track track : tracks) {
			List<String> places = track.getPlaces();
			if (places.isEmpty()) {
				// skip
			} else if (result.isEmpty()) {
				result.addAll(places);
			} else if (result.getLast().equals(places.get(0))) {
				result.addAll(places.subList(1, places.size()));
			} else {
				result.addAll(places);
			}
		}
		return result;
	}
	
	// --> end of HasSummary <--
	
	public void addTrack(Track track) {
		if (track.getActivity() != null) {
			((ActivityImpl) track.getActivity()).removeTrack(track);
		}
		tracks.add(track);
		track.setActivity(this);
	}
	
	public void removeTrack(Track track) {
		if (track.getActivity() != this) {
			throw new IllegalArgumentException("track not owned by this activity");
		}
		tracks.remove(track);
		track.setActivity(this);
	}

	public List<Track> getTracks() {
		return Collections.unmodifiableList(tracks);
	}

	public int getTrackCount() {
		return tracks.size();
	}

	public Track getTrack(int idx) {
		return tracks.get(idx);
	}

	public void addEquipment(Equipment equipment) {
		equipments.add(equipment);
	}
	
	public void removeEquipment(Equipment equipment) {
		equipments.remove(equipment);
	}

	public Collection<Equipment> getEquipments() {
		return equipments;
	}
	
}
