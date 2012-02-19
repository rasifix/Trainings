package com.github.rasifix.trainings.model;

import java.util.List;

import com.github.rasifix.trainings.model.attr.AttributeSummary;

public interface HasSummary {
	
	String getSport();
	
	/**
	 * Gets the distance in meters. Returns null if there is no distance
	 * measured or not enough information available
	 * 
	 * @return the distance in meters
	 */
	int getDistance();
	
	/**
	 * Gets the duration in seconds.
	 * 
	 * @return the duration in seconds
	 */
	int getDuration();

	/**
	 * Gets the speed in meters per second. The speed is calculated by
	 * {@link #getDistance()} / {@link #getDuration()}.
	 * 
	 * @return the speed in meters per second or null if the calculation is
	 * not possible, e.g. because duration is 0.
	 */
	Double getSpeed();
	
	<T extends AttributeSummary<T>> T getSummary(AttributeSummaryBuilder<T> builder);

	/**
	 * Gets a list of place names that this activity visited.
	 * 
	 * @return the visited places
	 */
	List<String> getPlaces();
	
}
