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

public class Position {
	
	private final double latitude;
	
	private final double longitude;
	
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	@Override
	public String toString() {
		return "Position[latitude=" + latitude + ",longitude=" + longitude + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass().equals(obj.getClass())) {
			Position other = (Position) obj;
			return this.latitude == other.latitude
			    && this.longitude == other.longitude;
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		int hash = 13;
		hash = hash * 17 + Double.valueOf(latitude).hashCode();
		hash = hash * 17 + Double.valueOf(longitude).hashCode();
		return hash;
	}
	
}
