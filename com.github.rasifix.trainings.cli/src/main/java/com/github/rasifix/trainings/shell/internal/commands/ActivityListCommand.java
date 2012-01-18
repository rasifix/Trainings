package com.github.rasifix.trainings.shell.internal.commands;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jline.Completor;
import jline.NullCompletor;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.ActivityRepository.ActivityOverview;
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
		try {
			List<ActivityOverview> activities = repository.findActivities(startDate, endDate);
			
			System.out.println(activities.size() + " activities found");
			for (ActivityOverview overview : activities) {
				System.out.println(
						format(overview.getDate()) 
						+ "\t" + overview.getSport() 
						+ "\t" + formatDistance(overview.getDistance()) 
						+ "\t" + formatTime((int) overview.getDuration())
						+ "\t(" + overview.getActivityId() + ")");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return context.getCurrent();
	}
	
	private String formatDistance(int distance) {
		int km = distance / 1000;
		int dkm = (distance / 100 % 10);
		return km + "." + dkm;
	}

	private String formatTime(int duration) {
		int hours = duration / 3600;
		int minutes = (duration - hours * 3600) / 60;
		int seconds = duration % 60;
		return pad(hours) + ":" + pad(minutes) + ":" + pad(seconds);
	}

	private String pad(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("negative time");
		} else if (value < 10) {
			return "0" + value;
		} else {
			return "" + value;
		}
	}

	private String format(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
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
