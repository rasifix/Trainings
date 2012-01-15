package com.github.rasifix.trainings.shell.internal.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import jline.Completor;
import jline.NullCompletor;

import aQute.bnd.annotation.component.Component;

import com.github.rasifix.trainings.model.Activity;
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
			System.out.println("total time     = " + activity.getTotalTime());
			System.out.println("total distance = " + activity.getTotalDistance());
		}
		return current;
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
