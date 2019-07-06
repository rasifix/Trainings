package com.github.rasifix.trainings.shell.internal.commands.edit;

import java.util.ListIterator;

import org.osgi.service.component.annotations.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.PositionAttribute;

import jline.Completor;

@Component
public class CutEndCommand implements Command {

	public static final String NAME = "edit:cut-end";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME + " - removes trackpoints at the end that do not have a position";
	}

	@Override
	public Completor getCompletor() {
		return null;
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity activity = (Activity) context.getCurrent();
		Track track = activity.getTrack(activity.getTrackCount() - 1);
		ListIterator<Trackpoint> it = track.getTrackpoints().listIterator(track.getTrackpoints().size());
		while (it.hasPrevious()) {
			Trackpoint next = it.previous();
			if (next.hasAttribute(PositionAttribute.class)) {
				break;
			}
			System.out.println("REMOVE TRACKPOINT " + next.getElapsedTime());
			it.remove();
		}
		return context.getCurrent();
	}

}
