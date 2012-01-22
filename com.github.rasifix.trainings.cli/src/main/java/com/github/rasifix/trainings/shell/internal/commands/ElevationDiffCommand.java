package com.github.rasifix.trainings.shell.internal.commands;

import jline.Completor;
import jline.NullCompletor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.ElevationModel;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Position;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class ElevationDiffCommand implements Command {

	private static final String NAME = "elevationdiff";
	
	private ElevationModel model;

	@Override
	public String getName() {
		return NAME;
	}

	@Reference
	public void setElevationModel(ElevationModel model) {
		this.model = model;
	}
	
	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity activity = (Activity) context.getCurrent();
		if (activity == null) {
			return null;
		}
		
		Double lastReal = null;
		Double realAscent = Double.valueOf(0);
		Double realDescent = Double.valueOf(0);
		
		Double lastMeasured = null;
		Double measuredAscent = Double.valueOf(0);
		Double measuredDescent = Double.valueOf(0);
		
		for (Track track : activity.getTracks()) {
			for (Trackpoint trackpoint : track.getTrackpoints()) {
				if (trackpoint.hasAttribute(PositionAttribute.class) && trackpoint.hasAttribute(AltitudeAttribute.class)) {
					Position position = trackpoint.getPosition();
					double measured = trackpoint.getAttribute(AltitudeAttribute.class).getAltitude();
					double real = model.elevationForPosition(position.getLatitude(), position.getLongitude());
					
					if (lastReal != null && lastReal < real) {
						realAscent += real - lastReal;
					} else if (lastReal != null && lastReal > real) {
						realDescent += lastReal - real;
					}
					lastReal = real;
					
					if (lastMeasured != null && lastMeasured < measured) {
						measuredAscent += measured - lastMeasured;
					} else if (lastMeasured != null && lastMeasured > measured) {
						measuredDescent += lastMeasured - measured;
					}
					lastMeasured = measured;
				}
			}
		}
		
		System.out.format("measured +%.1f/-%.1f\n", measuredAscent, measuredDescent);
		System.out.format("real     +%.1f/-%.1f\n", realAscent, realDescent);
		
		return context.getCurrent();
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

}
