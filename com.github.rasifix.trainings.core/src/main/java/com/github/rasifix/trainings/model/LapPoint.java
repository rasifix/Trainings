package com.github.rasifix.trainings.model;

public class LapPoint extends Trackpoint {

	public LapPoint(long elapsedTime) {
		super(elapsedTime);
	}

	public LapPoint(long elapsedTime, Trackpoint trackpoint) {
		super(elapsedTime);
		for (Class<? extends TrackpointAttribute> attributeType : trackpoint.getAttributes()) {
			TrackpointAttribute attribute = trackpoint.getAttribute(attributeType);
			addAttribute(attribute);
		}
	}

}
