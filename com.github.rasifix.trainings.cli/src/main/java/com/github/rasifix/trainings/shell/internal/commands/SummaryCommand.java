package com.github.rasifix.trainings.shell.internal.commands;

import static java.util.stream.Collectors.joining;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.osgi.service.component.annotations.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;

import jline.Completor;
import jline.NullCompletor;

@Component
public class SummaryCommand implements Command {

	private static final String NAME = "summary";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME;
	}

	@Override
	public Object execute(CommandContext context) {
		Object current = context.getCurrent();
		if (current instanceof Activity) {
			Activity activity = (Activity) current;
			System.out.println(format(activity.getStartTime()) + " " + activity.getSport());
			System.out.println("total time     = " + activity.getDuration());
			System.out.println("total distance = " + activity.getDistance());
			System.out.println("equipments     = " + activity.getEquipments().stream().map(e -> e.getName()).collect(joining(",")));
			System.out.println();
			System.out.println(pluralize(activity.getTrackCount(), "track", "tracks"));
			for (Track track : activity.getTracks()) {
				System.out.println(format(track.getStartTime()) + " " + track.getSport());
				System.out.println("total time     = " + track.getDuration());
				System.out.println("total distance = " + track.getDistance());
			}
		}
		return current;
	}

	private static String pluralize(int number, String singular, String plural) {
		return number + " " + (number == 1 ? singular : plural);
	}

	private String format(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(date);
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

}
