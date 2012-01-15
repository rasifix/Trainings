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

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Activity {

	private final List<Track> tracks = new LinkedList<Track>();
	
	private Date startTime;
	
	public Activity(final long startTime) {
		this(new Date(startTime));
	}
	
	public Activity(final Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public Date getEndTime() {
		double result = 0;
		for (Track track : tracks) {
			result += track.getTotalTimeInSeconds();
		}
		return new Date(startTime.getTime() + (long) (result * 1000));
	}

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

	public long getTotalTime() {
		int result = 0;
		for (Track track : tracks) {
			result += track.getTotalTimeInSeconds();
		}
		return result;
	}

	public int getTotalDistance() {
		int result = 0;
		for (Track track : tracks) {
			result += track.getDistance();
		}
		return result;
	}
	
	public void addTrack(Track track) {
		if (track.getActivity() != null) {
			track.getActivity().removeTrack(track);
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
	
}
