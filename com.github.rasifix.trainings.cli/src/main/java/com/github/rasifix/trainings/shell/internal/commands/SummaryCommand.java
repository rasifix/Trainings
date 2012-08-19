package com.github.rasifix.trainings.shell.internal.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import jline.Completor;
import jline.NullCompletor;
import aQute.bnd.annotation.component.Component;

import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.ActivityImpl;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.SpeedAttribute;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class SummaryCommand implements Command {

	private static final String NAME = "summary";
	
	@Override
	public String getName() {
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
			System.out.println();
			System.out.println(pluralize(activity.getTrackCount(), "track", "tracks"));
			for (Track track : activity.getTracks()) {
				System.out.println(format(track.getStartTime()) + " " + track.getSport());
				System.out.println("total time     = " + track.getDuration());
				System.out.println("total distance = " + track.getDistance());
				int tpWithoutSpeed = 0;
				for (Trackpoint trackpoint : track.getTrackpoints()) {
					if (trackpoint.hasAttribute(SpeedAttribute.class)) {
						SpeedAttribute speed = trackpoint.getAttribute(SpeedAttribute.class);
						if (speed.getValue() < 0.1) {
							tpWithoutSpeed += 1;
//							System.out.println(trackpoint.getAttribute(DistanceAttribute.class).getValue());
						}
					}
				}
//				System.out.println("without speed = " + tpWithoutSpeed);
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
