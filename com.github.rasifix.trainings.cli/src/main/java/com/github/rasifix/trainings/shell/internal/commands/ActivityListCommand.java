package com.github.rasifix.trainings.shell.internal.commands;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jline.Completor;
import jline.NullCompletor;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class ActivityListCommand implements Command {

	private static final String NAME = "repo:list";
	
	private ActivityRepository repository;
	
	@Reference
	public void setActivityRepository(ActivityRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) {
		Date startDate = parseDate(context.getArguments()[0]);
		Date endDate = parseDate(context.getArguments()[1]);
		repository.findActivities(startDate, endDate);
		return context.getCurrent();
	}

	private Date parseDate(String dateString) {
		if ("now".equals(dateString)) {
			return new Date();
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return format.parse(dateString);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
