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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.AttributeSummary;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;

public class Track implements HasSummary {
	
//	private final List<Trackpoint> trackpoints = new LinkedList<Trackpoint>(); 
	private final TrackpointSequence trackpoints = new LinkedTrackpointSequence(); 
	
	private Activity activity;
	
	private Date startTime;
	
	private String sport;
	
	public Track(long startTime) {
		this(new Date(startTime));
	}
	
	public Track(Date startTime) {
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
	public Date getStartTime() {
		return startTime;
	}
	
	public String getSport() {
		return sport;
	}
	
	public void setSport(String sport) {
		this.sport = sport;
	}
	
	// --> start of HasSummary <--

	public int getDistance() {
		TrackpointSequence filtered = trackpoints.select(DistanceAttribute.class);
		if (filtered.size() >= 2) {
			Trackpoint first = filtered.getFirst();
			Trackpoint last = filtered.getLast();
			
			DistanceAttribute firstDistanceAttr = first.getAttribute(DistanceAttribute.class);
			Double firstDistance = firstDistanceAttr.getValue();

			DistanceAttribute lastDistanceAttr = last.getAttribute(DistanceAttribute.class);
			Double lastDistance = lastDistanceAttr.getValue();
			return (int) Math.round(lastDistance - firstDistance);
		}
		
		return Integer.valueOf(0);
	}
	
	@Override
	public int getDuration() {
		if (trackpoints.size() >= 2) {
			final Trackpoint first = trackpoints.get(0);
			final Trackpoint last = trackpoints.get(trackpoints.size() - 1);
			return (int) Math.round(delta(last.getTime(), first.getTime()));
		}
		return Integer.valueOf(0);
	}

	public Double getSpeed() {
		if (trackpoints.size() >= 2) {
			return 1.0 * getDistance() / getDuration();
		}
		return null;
	}
	
	public <T extends AttributeSummary<T>> T getSummary(AttributeSummaryBuilder<T> builder) {
		// power    - avg / median? / max
		// speed    - avg / median? / max
		TrackpointSequence filtered = trackpoints.select(builder.getAttributeType());
		return builder.buildSummary(filtered);
	}
	
	// --> end of HasSummary <--
	
	@Deprecated
	public double getTotalTimeInSeconds() {
		if (trackpoints.size() >= 2) {
			final Trackpoint first = trackpoints.get(0);
			final Trackpoint last = trackpoints.get(trackpoints.size() - 1);
			return delta(last.getTime(), first.getTime());
		}
		return 0.0;
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
		
		return hasHr ? result / getTotalTimeInSeconds() : null;
	}
	
	private static double delta(Date current, Date last) {
		return (current.getTime() - last.getTime()) / 1000.0;
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
				double altitude = attr.getAltitude();
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
				double altitude = attr.getAltitude();
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
