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

import java.util.LinkedList;
import java.util.List;

public class TcxActivity {
	
	private final String id;
	
	private final String sport;
	
	private final List<TcxLap> laps = new LinkedList<TcxLap>();
	
	private TcxCreator creator;
	
	public TcxActivity(String id, String sport) {
		this.id = id;
		this.sport = sport;
	}
	
	public String getId() {
		return id;
	}
	
	public String getSport() {
		return sport;
	}

	public TcxCreator getCreator() {
		return creator;
	}
	
	public void setCreator(TcxCreator creator) {
		this.creator = creator;
	}
	
	public void addLap(TcxLap lap) {
		this.laps.add(lap);
	}
	
	public void removeLap(int idx) {
		this.laps.remove(idx);
	}

	public TcxLap getLap(int idx) {
		return laps.get(idx);
	}
	
	public List<TcxLap> getLaps() {
		return laps;
	}
	
	@Override
	public String toString() {
		return "Activity[id='" + id + "',sport='" + sport + "',laps=" + laps.size() + "]";
	}
	
}
