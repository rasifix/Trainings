package com.github.rasifix.trainings.shell.internal.commands.edit;

import java.util.LinkedList;
import java.util.List;

import jline.Completor;
import jline.NullCompletor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.PlaceNameLookup;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.PlaceNameAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;

@Component
public class AddLocationsCommand implements Command {

	private static final String NAME = "edit:locations";

	private final List<PlaceNameLookup> lookups = new LinkedList<PlaceNameLookup>();
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}
	
	@Reference(unbind="removeLookup", multiple=true)
	public void addLookup(PlaceNameLookup lookup) {
		synchronized (lookups) {
			lookups.add(lookup);
		}
	}
	
	public void removeLookup(PlaceNameLookup lookup) {
		synchronized (lookups) {
			lookups.remove(lookup);
		}
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity activity = (Activity) context.getCurrent();
		
		PlaceNameLookup[] cloned;
		synchronized (lookups) {
			cloned = lookups.toArray(new PlaceNameLookup[lookups.size()]);
		}
		
		for (Track track : activity.getTracks()) {
			for (Trackpoint trackpoint : track.getTrackpoints().select(PositionAttribute.class)) {
				PlaceNameLookup lookup = getLookupForTrackpoint(cloned, trackpoint);
				double latitude = trackpoint.getPosition().getLatitude();
				double longitude = trackpoint.getPosition().getLongitude();
				if (lookup != null && lookup.containsPosition(latitude, longitude))  {
					String location = lookup.locationForPosition(latitude, longitude);
					if (location != null) {
						trackpoint.addAttribute(new PlaceNameAttribute(location));
					}
				}
			}
		}
		
		return context.getCurrent();
	}

	private PlaceNameLookup getLookupForTrackpoint(PlaceNameLookup[] lookups, Trackpoint trackpoint) {
		for (PlaceNameLookup lookup : lookups) {
			if (lookup.containsPosition(trackpoint.getPosition().getLatitude(), trackpoint.getPosition().getLongitude())) {
				return lookup;
			}
		}
		return null;
	}

}
