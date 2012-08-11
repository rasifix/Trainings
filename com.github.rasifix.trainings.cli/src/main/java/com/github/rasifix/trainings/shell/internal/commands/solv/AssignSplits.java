package com.github.rasifix.trainings.shell.internal.commands.solv;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import jline.Completor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.LapPoint;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.TrackpointAttribute;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class AssignSplits implements Command {

	private ResultService service;

	@Reference
	public void setService(ResultService service) {
		this.service = service;
	}
	
	@Override
	public String getName() {
		return "solv:assign-splits";
	}

	@Override
	public Completor getCompletor() {
		return null;
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity activity = (Activity) context.getCurrent();
		
		if (activity.getTrackCount() > 1) {
			System.err.println("only single track activities are supported");
			return activity;
		}
		
		Track track = activity.getTrack(0);
		
		int year = Integer.parseInt(context.getArgument(0));
		int eventIdx = Integer.parseInt(context.getArgument(1)) - 1;
		int categoryIdx = Integer.parseInt(context.getArgument(2)) - 1;
		String name = context.getArgument(3);
		
		List<Event> events = service.listEvents(year);
		Event event = events.get(eventIdx);
		
		List<Category> categories = service.listCategories(event);
		Category category = categories.get(categoryIdx);
		
		List<Runner> runners = service.listRunners(category);
		for (Runner runner : runners) {
			if (runner.getName().equals(name)) {
				System.out.println(runner.getSplits());
				Iterator<Long> splits = parseSplits(runner.getSplits()).iterator();
				System.out.println(splits);
				long currentSplit = splits.next();
				System.out.println("CURRENT SPLIT = " + format(currentSplit));
				ListIterator<Trackpoint> trackpoints = track.getTrackpoints().listIterator();
				while (trackpoints.hasNext()) {
					Trackpoint trackpoint = trackpoints.next();
					long elapsedTime = trackpoint.getElapsedTime();
					if (elapsedTime >= currentSplit) {
						LapPoint point = new LapPoint(currentSplit, trackpoint);
						trackpoints.set(point);
						if (splits.hasNext()) {
							currentSplit += splits.next();
							System.out.println("CURRENT SPLIT = " + format(currentSplit));
						} else {
							return context.getCurrent();
						}
					}
				}
			}
		}
		
		return context.getCurrent();
	}
	
	private String format(long elapsed) {
		elapsed /= 1000;
		return elapsed / 60 + "." + pad(elapsed % 60);
	}
	
	private String pad(long value) {
		return value < 10 ? "0" + value : "" + value;
	}

	private List<Long> parseSplits(List<String> splits) {
		List<Long> result = new LinkedList<Long>();
		for (String split : splits) {
			if ("--".equals(split)) {
				// ignore
			} else {
				String[] time = split.split("[.]");
				result.add(1000L * (Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1])));
			}
		}
		return result;
	}

}
