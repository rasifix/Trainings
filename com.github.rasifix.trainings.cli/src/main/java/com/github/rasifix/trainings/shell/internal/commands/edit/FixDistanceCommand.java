package com.github.rasifix.trainings.shell.internal.commands.edit;

import jline.Completor;
import aQute.bnd.annotation.component.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;

@Component
public class FixDistanceCommand implements Command {

	private static final String NAME = "edit:tmp:fix-time";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Completor getCompletor() {
		return null;
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity current = (Activity) context.getCurrent();
		
		int trackIdx = Integer.parseInt(context.getArgument(0)); 
		int totalTime = Integer.parseInt(context.getArgument(1));
		
		Track track = current.getTrack(trackIdx);
		double factor = 1.0 * totalTime / track.getDistance();
		for (int i = 0; i < track.getTrackpointCount(); i++) {
			Trackpoint trackpoint = track.getTrackpoint(i);
			double distance = trackpoint.getAttribute(DistanceAttribute.class).getValue();
			long elapsedTime = (long) (distance * factor * 1000);
			trackpoint.setElapsedTime(elapsedTime);
		}
		
		return current;
	}

}
