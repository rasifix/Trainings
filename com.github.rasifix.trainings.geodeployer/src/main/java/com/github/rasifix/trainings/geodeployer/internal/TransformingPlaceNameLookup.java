package com.github.rasifix.trainings.geodeployer.internal;

import com.github.rasifix.trainings.PlaceNameLookup;

public class TransformingPlaceNameLookup implements PlaceNameLookup {

	private final PlaceNameLookup target;

	public TransformingPlaceNameLookup(PlaceNameLookup target) {
		this.target = target;
	}

	@Override
	public boolean containsPosition(double latitude, double longitude) {
		double[] converted = ApproxSwissProj.WGS84toLV03(latitude, longitude, 0);
		return target.containsPosition(converted[0], converted[1]);
	}

	@Override
	public String locationForPosition(double latitude, double longitude) {
		double[] converted = ApproxSwissProj.WGS84toLV03(latitude, longitude, 0);
		return target.locationForPosition(converted[0], converted[1]);
	}

}
