package com.github.rasifix.trainings;

public interface PlaceNameLookup {
	
	boolean containsPosition(double latitude, double longitude);
	
	String locationForPosition(double latitude, double longitude);
	
}
