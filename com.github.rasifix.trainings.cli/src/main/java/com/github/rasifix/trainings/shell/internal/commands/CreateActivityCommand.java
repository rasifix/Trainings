package com.github.rasifix.trainings.shell.internal.commands;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jline.Completor;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.model.ActivityImpl;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;

import aQute.bnd.annotation.component.Component;

@Component
public class CreateActivityCommand implements Command {

	private static final String NAME = "create:activity";
	
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
		Date date = parseDate(context.getArgument(0));
		ActivityImpl activity = new ActivityImpl(date);
		
		String sport = context.getArgument(1);
		
		long distance = Long.parseLong(context.getArgument(2));
		long duration = parseTime(context.getArgument(3));
		
		Track track = new Track(date);
		track.setSport(sport);
		activity.addTrack(track);
		
		Trackpoint start = new Trackpoint(0);
		start.addAttribute(new DistanceAttribute(0));
		track.addTrackpoint(start);
		
		Trackpoint end = new Trackpoint(duration);
		end.addAttribute(new DistanceAttribute(distance));
		track.addTrackpoint(end);
		
		return activity;
	}

	private long parseTime(String value) {
		String[] split = value.split("[:]");
		int hours = Integer.parseInt(split[0]);
		int minutes = Integer.parseInt(split[1]);
		return (hours * 3600 + minutes * 60) * 1000L;
	}

	private Date parseDate(String value) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(value);
	}

}
