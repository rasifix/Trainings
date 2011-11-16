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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.measure.unit.SI;


import org.joda.time.DateTime;

import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;

public class Track {
	
	private final List<Trackpoint> trackpoints = new LinkedList<Trackpoint>(); 
	
	private Activity activity;
	
	private DateTime startTime;
	
	private String sport;
	
	public Track(DateTime startTime) {
		if (startTime == null) {
			throw new IllegalArgumentException("startTime cannot be null");
		}
		this.startTime = startTime;
	}

	/**
	 * Gets the {@link Activity} containing this {@link Track}.
	 * 
	 * @return the {@link Activity} containing this {@link Track}
	 */
	public Activity getActivity() {
		return activity;
	}
	
	void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	/**
	 * Gets the start time of this track.
	 * 
	 * @return the start time of the track
	 */
	public DateTime getStartTime() {
		return startTime;
	}
	
	public String getSport() {
		return sport;
	}
	
	public void setSport(String sport) {
		this.sport = sport;
	}

	public Double getDistance() {
		if (trackpoints.size() >= 2) {
			final Trackpoint first = trackpoints.get(0);
			final Trackpoint last = trackpoints.get(trackpoints.size() - 1);
			
			final DistanceAttribute firstDistanceAttr = first.getAttribute(DistanceAttribute.class);
			final Double firstDistance = firstDistanceAttr.getValue();
			
			final DistanceAttribute lastDistanceAttribute = last.getAttribute(DistanceAttribute.class);
			final Double lastDistance = lastDistanceAttribute.getValue();
			
			return lastDistance - firstDistance;
		}
		return null;
	}
	
	public Double getTotalTime() {
		if (trackpoints.size() >= 2) {
			final Trackpoint first = trackpoints.get(0);
			final Trackpoint last = trackpoints.get(trackpoints.size() - 1);
			return delta(last.getTime(), first.getTime());
		}
		return null;
	}

	public Double getSpeed() {
		if (trackpoints.size() >= 2) {
			return getDistance() / getTotalTime();
		}
		return null;
	}
	
	public Double getAverageHeartRate() {
		if (trackpoints.size() <= 1) {
			return null;
		}
		
		boolean hasHr = false;
		double result = 0;
		Trackpoint last = null;
		
		for (Trackpoint trackpoint : trackpoints) {
			if (trackpoint.hasAttribute(HeartRateAttribute.class)) {
				double delta = delta(trackpoint.getTime(), last != null ? last.getTime() : startTime); 
				result += trackpoint.getAttribute(HeartRateAttribute.class).getValue() * delta;
				last = trackpoint;
				hasHr = true;
			}
		} 
		
		return hasHr ? result / getTotalTime() : null;
	}
	
	private static double delta(DateTime current, DateTime last) {
		return (current.toDate().getTime() - last.toDate().getTime()) / 1000.0;
	}
	
	/**
	 * Gets the {@link TrackpointAttribute}s used by {@link Trackpoint}s in this
	 * track.
	 * 
	 * @return the {@link TrackpointAttribute}s of the {@link Trackpoint}s 
	 */
	public Collection<Class<? extends TrackpointAttribute>> getAttributes() {
		Set<Class<? extends TrackpointAttribute>> result = new HashSet<Class<? extends TrackpointAttribute>>();
		for (Trackpoint trackpoint : trackpoints) {
			result.addAll(trackpoint.getAttributes());
		}
		return result;
	}
	
	public void addTrackpoint(Trackpoint trackpoint) {
		if (trackpoint.getTrack() != null) {
			trackpoint.getTrack().removeTrackpoint(trackpoint);
		}
		trackpoints.add(trackpoint);
		trackpoint.setTrack(this);
	}
	
	public void removeTrackpoint(Trackpoint trackpoint) {
		if (trackpoint.getTrack() != this) {
			throw new IllegalArgumentException("event not owned by this track");
		}
		trackpoints.remove(trackpoint);
		trackpoint.setTrack(this);
	}
	
	public Trackpoint getTrackpoint(int trackpointIdx) {
		return trackpoints.get(trackpointIdx);
	}
	
	public int getTrackpointCount() {
		return trackpoints.size();
	}
	
	public List<Trackpoint> getTrackpoints() {
		return trackpoints;
	}

	public Double getAltitudeGain() {
		double result = 0;
		Double lastValue = null;
		for (Trackpoint trackpoint : trackpoints) {
			AltitudeAttribute attr = trackpoint.getAttribute(AltitudeAttribute.class);
			if (attr != null) {
				double altitude = attr.getAltitude().doubleValue(SI.METER);
				if (lastValue != null && Math.abs(lastValue - altitude) >= 3) {
					if (altitude - lastValue > 0) {
						result += altitude - lastValue;
					}
					lastValue = altitude;
				} else if (lastValue == null) {
					lastValue = altitude;
				}
			}
		}
		return result;
	}

	public Double getAltitudeLoss() {
		double result = 0;
		Double lastValue = null;
		for (Trackpoint trackpoint : trackpoints) {
			AltitudeAttribute attr = trackpoint.getAttribute(AltitudeAttribute.class);
			if (attr != null) {
				double altitude = attr.getAltitude().doubleValue(SI.METER);
				if (lastValue != null && Math.abs(lastValue - altitude) >= 3) {
					if (altitude - lastValue < 0) {
						result += lastValue - altitude;
					}
					lastValue = altitude;
				} else if (lastValue == null) {
					lastValue = altitude;
				}
			}
		}
		return result;
	}
	
}
