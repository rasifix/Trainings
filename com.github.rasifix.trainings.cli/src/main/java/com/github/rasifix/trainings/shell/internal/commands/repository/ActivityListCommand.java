package com.github.rasifix.trainings.shell.internal.commands.repository;

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
				double speed = 1.0 * overview.getDistance() / overview.getDuration();
				System.out.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM  %2$15s  %3$7s  %4$8s  %5$d  %6$14s  (%7$s)\n", 
						overview.getDate(), 
						overview.getSport(),
						FormatUtils.formatDistance(overview.getDistance()), 
						FormatUtils.formatTime((int) overview.getDuration()),
						overview.getAverageHeartRate(),
						"CYCLING".equals(overview.getSport())
								? FormatUtils.formatSpeedInKmh(speed)
								: FormatUtils.formatSpeedAsPace(speed),
						overview.getActivityId());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
