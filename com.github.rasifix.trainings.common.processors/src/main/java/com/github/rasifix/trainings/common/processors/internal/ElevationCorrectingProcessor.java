package com.github.rasifix.trainings.common.processors.internal;


import org.osgi.service.component.ComponentContext;

import com.github.rasifix.trainings.ActivityProcessor;
import com.github.rasifix.trainings.ElevationModel;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Position;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;

public class ElevationCorrectingProcessor implements ActivityProcessor {

	private ElevationModel elevationModel;
	
	public ElevationCorrectingProcessor() {
		System.out.println("gotcha");
	}
	
	public void activate(ComponentContext context) {
		System.out.println("got activated");
	}
	
	public void deactivate(ComponentContext context) {
		System.out.println("got deactivated");
	}
	
	public void setElevationModel(ElevationModel elevationModel) {
		System.out.println("set elevation model");
		this.elevationModel = elevationModel;
	}
	
	public void unsetElevationModel(ElevationModel elevationModel) {
		System.out.println("unset elevation model");
		this.elevationModel = null;
	}
	
	@Override
	public Activity processActivity(Activity activity) {
		for (Track track : activity.getTracks()) {
			for (Trackpoint trackpoint : track.getTrackpoints()) {
				if (trackpoint.hasAttribute(PositionAttribute.class)) {
					Position position = trackpoint.getPosition();
					if (elevationModel.containsPosition(position.getLatitude(), position.getLongitude())) {
						double elevation = elevationModel.elevationForPosition(position.getLatitude(), position.getLongitude());
						trackpoint.addAttribute(new AltitudeAttribute(elevation));
					}
				}
			}
		}
		return activity;
	}

}
