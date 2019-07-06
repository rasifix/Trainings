package com.github.rasifix.trainings.shell.internal.commands.edit;

import java.util.ListIterator;

import org.osgi.service.component.annotations.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;

import jline.Completor;

@Component
public class ShortenCommand implements Command {
	
	private static final String NAME = "edit:shorten";

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME + " <position:end|start> <time:[s]>";
	}
	
	@Override
	public Completor getCompletor() {
		return null;
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity activity = (Activity) context.getCurrent();
		
		if (context.getArguments().length != 2) {
			System.err.println(getUsage());
			return activity;
		}
		
		String position = context.getArgument(0);
		long millis = parseTime(context.getArgument(1)) * 1000L;
		
		int count = 0;
		if ("end".equals(position)) {
			Track track = activity.getTrack(activity.getTrackCount() - 1);
			ListIterator<Trackpoint> it = track.getTrackpoints().listIterator(track.getTrackpoints().size());
			while (it.hasPrevious()) {
				Trackpoint next = it.previous();
				if (next.getElapsedTime() <= millis) {
					break;
				}
				count += 1;
				it.remove();
			}
			
		} else if ("start".equals(position)) {
			Track track = activity.getTrack(0);
			ListIterator<Trackpoint> it = track.getTrackpoints().listIterator();
			while (it.hasNext()) {
				Trackpoint next = it.next();
				if (next.getElapsedTime() >= millis) {
					break;
				}
				count += 1;
				it.remove();
			}
		}
		
		System.out.println("removed " + count + " trackpoints from " + position);
		
		return activity;
	}

	private int parseTime(String arg) {
		String[] split = arg.split("[:]");
		return Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
	}

}
