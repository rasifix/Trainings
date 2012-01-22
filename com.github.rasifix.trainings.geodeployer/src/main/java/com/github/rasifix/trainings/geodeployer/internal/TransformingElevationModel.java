package com.github.rasifix.trainings.geodeployer.internal;

import com.github.rasifix.trainings.ElevationModel;

public class TransformingElevationModel implements ElevationModel {

	private ElevationModel target;

	public TransformingElevationModel(ElevationModel target) {
		this.target = target;
	}
	
	@Override
	public boolean containsPosition(double latitude, double longitude) {
		double[] converted = ApproxSwissProj.WGS84toLV03(latitude, longitude, 0);
		return target.containsPosition(converted[0], converted[1]);
	}

	@Override
	public double elevationForPosition(double latitude, double longitude) {
		double[] converted = ApproxSwissProj.WGS84toLV03(latitude, longitude, 0);
		return target.elevationForPosition(converted[0], converted[1]);
	}

}
