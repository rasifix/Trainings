package com.github.rasifix.trainings.model.attr;

import com.github.rasifix.trainings.model.TrackpointAttribute;

public class PlaceNameAttribute implements TrackpointAttribute {

	private final String placeName;

	public PlaceNameAttribute(String placeName) {
		this.placeName = placeName;
	}
	
	public String getPlaceName() {
		return placeName;
	}
	
	@Override
	public String getValue() {
		return placeName;
	}
	
	@Override
	public String toString() {
		return "PlaceNameAttribute[value=" + placeName + "]";
	}

}
