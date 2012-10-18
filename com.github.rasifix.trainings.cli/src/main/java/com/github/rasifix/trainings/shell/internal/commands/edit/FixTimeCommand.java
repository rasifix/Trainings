package com.github.rasifix.trainings.shell.internal.commands.edit;

import java.util.ListIterator;

import jline.Completor;

import aQute.bnd.annotation.component.Component;

import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class FixTimeCommand implements Command {

	private static final String NAME = "edit:tmp:fix-distance";

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
		int trackpointIdx = Integer.parseInt(context.getArgument(1));
		double offset = Double.parseDouble(context.getArgument(2));
		
		ListIterator<Trackpoint> it = current.getTrack(trackIdx).getTrackpoints().listIterator(trackpointIdx);
		while (it.hasNext()) {
			Trackpoint next = it.next();
			DistanceAttribute attribute = next.getAttribute(DistanceAttribute.class);
			if (attribute != null) {
				Double value = attribute.getValue();
				next.addAttribute(new DistanceAttribute(value + offset));
			}
		}
		
		return current;
	}

}
