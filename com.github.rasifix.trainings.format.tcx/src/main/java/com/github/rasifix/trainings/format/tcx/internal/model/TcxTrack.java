/* 
 * Copyright 2010 Simon Raess
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
package com.github.rasifix.trainings.format.tcx.internal.model;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TcxTrack {
	
	private final List<TcxTrackpoint> trackpoints = new LinkedList<TcxTrackpoint>();
	
	public void addTrackpoint(TcxTrackpoint trackpoint) {
		this.trackpoints.add(trackpoint);
	}

	public void addTrackpoint(int idx, TcxTrackpoint trackpoint) {
		trackpoints.add(idx, trackpoint);
	}
	
	public void removeTrackpoint(int idx) {
		this.trackpoints.remove(idx);
	}
	
	public void addTrackpoints(Collection<? extends TcxTrackpoint> trackpoints) {
		this.trackpoints.addAll(trackpoints);
	}
	
	public TcxTrackpoint getTrackpoint(int idx) {
		return this.trackpoints.get(idx);
	}
	
	public int getTrackpointCount() {
		return trackpoints.size();
	}
	
	public TcxTrackpoint getFirstTrackpoint() {
		return trackpoints.get(0);
	}

	public TcxTrackpoint getLastTrackpoint() {
		return trackpoints.get(getTrackpointCount() - 1);
	}
	
	public List<TcxTrackpoint> getTrackpoints() {
		return trackpoints;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Track[trackpoints=").append(getTrackpointCount());
		if (getTrackpointCount() >= 2) {
			builder.append(",start='").append(format(getFirstTrackpoint().getTime())).append("'");
			builder.append(",last='").append(format(getLastTrackpoint().getTime())).append("'");
		}
		builder.append("]");
		
		return builder.toString();
	}

	private String format(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
	
}
