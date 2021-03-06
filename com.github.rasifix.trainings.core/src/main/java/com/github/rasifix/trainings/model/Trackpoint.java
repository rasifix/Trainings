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
import java.util.HashMap;
import java.util.Map;

import com.github.rasifix.trainings.model.attr.PositionAttribute;


public class Trackpoint {
	
	private final Map<Class<? extends TrackpointAttribute>, TrackpointAttribute> attributes = new HashMap<Class<? extends TrackpointAttribute>, TrackpointAttribute>();
	
	private Track track;
	
	private long elapsedTime;

	public Trackpoint(final long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	public Track getTrack() {
		return track;
	}
	
	void setTrack(Track track) {
		this.track = track;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	public Date getTime() {
		return new Date(track.getStartTime().getTime() + elapsedTime);
	}

	public Position getPosition() {
		PositionAttribute attribute = getAttribute(PositionAttribute.class);
		return attribute != null
		     ? attribute.getPosition()
		     : null;
	}
	
	public int getAttributeCount() {
		return attributes.size();
	}
	
	public Collection<Class<? extends TrackpointAttribute>> getAttributes() {
		return Collections.unmodifiableCollection(attributes.keySet());
	}

	public <T extends TrackpointAttribute> boolean hasAttribute(Class<T> attribute) {
		return attributes.containsKey(attribute);
	}
	
	public <T extends TrackpointAttribute> void addAttribute(T attribute) {
		attributes.put(attribute.getClass(), attribute);
	}

	public <T extends TrackpointAttribute> T getAttribute(Class<T> type) {
		return type.cast(attributes.get(type));
	}
	
	@Override
	public String toString() {
		return "Trackpoint[elapsedTime=" + getElapsedTime() + "]";
	}
	
}
