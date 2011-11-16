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

import java.util.Date;

public class TcxTrackpoint {
	
	/**
	 * The time at which the trackpoint was created.
	 */
	private Date time;
	
	/**
	 * The position of the trackpoint.
	 */
	private TcxPosition position;
	
	/**
	 * Altitude in meters.
	 */
	private Double altitude;
	
	/**
	 * Distance from start in meters.
	 */
	private Double distance;
	
	/**
	 * Heart rate in beats per minute.
	 */
	private Integer heartRate;
	
	/**
	 * The current bike cadence.
	 */
	private Short bikeCadence;
	
	/**
	 * The current run cadence.
	 */
	private Short runCadence;
	
	/**
	 * The current speed.
	 */
	private Double speed;

	/**
	 * The sensor state, either Absent or Present (could be an enum...).
	 */
	private SensorState sensorState;

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public TcxPosition getPosition() {
		return position;
	}

	public void setPosition(TcxPosition position) {
		this.position = position;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Integer getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(Integer heartRate) {
		this.heartRate = heartRate;
	}
	
	public Short getRunCadence() {
		return runCadence;
	}
	
	public void setRunCadence(Short runCadence) {
		this.runCadence = runCadence;
	}
	
	public Short getBikeCadence() {
		return bikeCadence;
	}
	
	public void setBikeCadence(Short bikeCadence) {
		this.bikeCadence = bikeCadence;
	}
	
	public Double getSpeed() {
		return speed;
	}
	
	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public void setSensorState(SensorState sensorState) {
		this.sensorState = sensorState;
	}
	
	public SensorState getSensorState() {
		return sensorState;
	}
	
}
