package com.github.rasifix.trainings.shell.internal.commands.edit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.ElevationModel;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Position;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;

import jline.Completor;
import jline.NullCompletor;

@Component
public class ElevationCorrectionCommand implements Command {

	private static final String NAME = "edit:elevation";
	
	private ElevationModel elevationModel;

	@Reference
	public void setElevationModel(ElevationModel elevationModel) {
		this.elevationModel = elevationModel;
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME + " - corrects the elevation of the current activity";
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity activity = (Activity) context.getCurrent();
		
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
		
		return context.getCurrent();
	}

}
