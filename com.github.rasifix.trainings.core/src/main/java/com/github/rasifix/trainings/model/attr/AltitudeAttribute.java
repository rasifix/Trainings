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
package com.github.rasifix.trainings.model.attr;

import com.github.rasifix.trainings.model.AttributeSummaryBuilder;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.TrackpointAttribute;
import com.github.rasifix.trainings.model.TrackpointSequence;


public class AltitudeAttribute implements TrackpointAttribute {
	
	private final double altitude;
	
	public AltitudeAttribute(double altitudeInMeters) {
		this.altitude = altitudeInMeters;
	}

	public double getAltitude() {
		return altitude;
	}
	
	@Override
	public Double getValue() {
		return altitude;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass().equals(obj.getClass())) {
			AltitudeAttribute other = (AltitudeAttribute) obj;
			return this.getValue().equals(other.getValue());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	public static AttributeSummaryBuilder<AltitudeSummary> getDefaultSummaryBuilder() {
		return new AttributeSummaryBuilder<AltitudeSummary>() {
			@Override
			public Class<? extends TrackpointAttribute> getAttributeType() {
				return AltitudeAttribute.class;
			}
			@Override
			public AltitudeSummary buildSummary(TrackpointSequence trackpoints) {
				if (trackpoints.isEmpty()) {
					return null;
				}
				
				int start = (int) Math.round(getAltitude(trackpoints.getFirst()));
				int end = (int) Math.round(getAltitude(trackpoints.getLast()));
				
				int min = Integer.MAX_VALUE;
				int max = Integer.MIN_VALUE;
				double sum = 0;
				int time = 0;
				int gain = 0;
				int loss = 0;
				
				Double last = null;
				for (Trackpoint trackpoint : trackpoints) {
					double altitude = getAltitude(trackpoint);
					if (last != null) {
						if (last < altitude) {
							gain += Math.round(altitude - last);
						} else if (altitude < last) {
							loss += Math.round(last - altitude);
						}
					}
					if (!trackpoints.isLast(trackpoint)) {
						int elapsed = (int) (trackpoint.getElapsedTime() - trackpoints.next(trackpoint).getElapsedTime());
						time += elapsed;
						sum += elapsed * altitude;
					}
					
					min = Math.min(min, (int) Math.round(altitude));
					max = Math.max(max, (int) Math.round(altitude));
					
					last = Double.valueOf(altitude);
				}
				
				int avg = (int) Math.round(1f * sum / time);
				
				return new AltitudeSummary(time, min, avg, max, gain, loss, start, end);
			}
			
			private double getAltitude(Trackpoint trackpoint) {
				return trackpoint.getAttribute(AltitudeAttribute.class).getAltitude();
			}
		};
	}
	
}
