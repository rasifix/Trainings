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
import java.util.LinkedList;
import java.util.List;

public class TcxLap {
	
	private final List<TcxTrack> tracks = new LinkedList<TcxTrack>();
	
	private final Date startTime;
	
	/**
	 * Total time of the lap in seconds.
	 */
	private double totalTime;
	
	/**
	 * Total distance of the lap in meters.
	 */
	private double distance;
	
	/**
	 * Average speed in this lap.
	 */
	private Double averageSpeed;
	
	/**
	 * Maximum speed in this lap.
	 */
	private Double maximumSpeed;
	
	/**
	 * Average heart rate in this lap in beats per minute.
	 */
	private Integer averageHeartRate;
	
	/**
	 * Maximum heart rate in this lap in beats per minute.
	 */
	private Integer maximumHeartRate;
	
	/**
	 * The calories burned in this lap.
	 */
	private Short calories;
	
	/**
	 * The average bike cadence.
	 */
	private Short averageBikeCadence;
	
	/**
	 * The maximum bike cadence.
	 */
	private Short maximumBikeCadence;
	
	/**
	 * The average run cadence.
	 */
	private Short averageRunCadence;
	
	/**
	 * The maximum run cadence.
	 */
	private Short maximumRunCadence;
	
	/**
	 * The number of steps in this lap.
	 */
	private Integer steps;
	
	/**
	 * The trigger method of the lap.
	 */
	private String triggerMethod;

	/**
	 * The intensity of the lap.
	 */
	private Intensity intensity;
	
	public TcxLap(Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public void addTrack(TcxTrack track) {
		this.tracks.add(track);
	}
	
	public void removeTrack(int idx) {
		this.tracks.remove(idx);
	}

	public TcxTrack getTrack(int idx) {
		return this.tracks.get(idx);
	}

	public int getTrackCount() {
		return tracks.size();
	}
	
	public List<TcxTrack> getTracks() {
		return tracks;
	}
	
	public TcxTrack getFirstTrack() {
		return getTrack(0);
	}
	
	public TcxTrack getLastTrack() {
		return getTrack(getTrackCount() - 1);
	}
	
	public TcxTrackpoint getFirstTrackpoint() {
		return getFirstTrack().getFirstTrackpoint();
	}
	
	public TcxTrackpoint getLastTrackpoint() {
		return getLastTrack().getLastTrackpoint();
	}

	public double getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Double getAverageSpeed() {
		return averageSpeed;
	}
	
	public void setAverageSpeed(Double averageSpeed) {
		this.averageSpeed = averageSpeed;
	}
	
	public Double getMaximumSpeed() {
		return maximumSpeed;
	}

	public void setMaximumSpeed(Double maximumSpeed) {
		this.maximumSpeed = maximumSpeed;
	}

	public Integer getAverageHeartRate() {
		return averageHeartRate;
	}

	public void setAverageHeartRate(Integer averageHeartRate) {
		this.averageHeartRate = averageHeartRate;
	}

	public Integer getMaximumHeartRate() {
		return maximumHeartRate;
	}

	public void setMaximumHeartRate(Integer maximumHeartRate) {
		this.maximumHeartRate = maximumHeartRate;
	}
	
	public Short getCalories() {
		return calories;
	}
	
	public void setCalories(Short calories) {
		this.calories = calories;
	}

	public Short getAverageBikeCadence() {
		return averageBikeCadence;
	}
	
	public void setAverageBikeCadence(Short averageBikeCadence) {
		this.averageBikeCadence = averageBikeCadence;
	}
	
	public Short getAverageRunCadence() {
		return averageRunCadence;
	}
	
	public Short getMaximumBikeCadence() {
		return maximumBikeCadence;
	}
	
	public void setMaximumBikeCadence(Short maxBikeCadence) {
		this.maximumBikeCadence = maxBikeCadence;
	}
	
	public void setAverageRunCadence(Short averageRunCadence) {
		this.averageRunCadence = averageRunCadence;
	}
	
	public Short getMaximumRunCadence() {
		return maximumRunCadence;
	}
	
	public void setMaximumRunCadence(Short maxRunCadence) {
		this.maximumRunCadence = maxRunCadence;
	}
	
	public String getTriggerMethod() {
		return triggerMethod;
	}

	public void setTriggerMethod(String triggerMethod) {
		this.triggerMethod = triggerMethod;
	}
	
	public Integer getSteps() {
		return steps;
	}
	
	public void setSteps(Integer steps) {
		this.steps = steps;
	}
	
	public Intensity getIntensity() {
		return intensity;
	}

	public void setIntensity(Intensity intensity) {
		this.intensity = intensity;
	}
	
}
