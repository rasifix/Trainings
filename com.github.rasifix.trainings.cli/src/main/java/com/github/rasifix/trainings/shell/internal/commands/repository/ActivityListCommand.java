package com.github.rasifix.trainings.shell.internal.commands.repository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.ActivityRepository.ActivityOverview;

import jline.Completor;
import jline.NullCompletor;

@Component
public class ActivityListCommand implements Command {

	private static final String NAME = "repo:list";

	private final Map<String, ServiceReference> repositories = new HashMap<String, ServiceReference>();
	private ComponentContext context;

	private ActivityRepository getRepository(String name) {
		return (ActivityRepository) context.getBundleContext().getService(repositories.get(name));
	}

	@Activate
	public void activate(ComponentContext context) {
		this.context = context;
	}
	
	@Reference(policy=ReferencePolicy.DYNAMIC, cardinality=ReferenceCardinality.AT_LEAST_ONE, service=ActivityRepository.class, unbind="removeRepository")
	public void addRepository(ServiceReference ref) {
		String name = (String) ref.getProperty("name");
		synchronized (repositories) {
			repositories.put(name, ref);
		} 
	}
	
	public void removeRepository(ServiceReference ref) {
		synchronized (repositories) {
			repositories.remove(ref.getProperty("name"));
		}
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME;
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws IOException {
		String name = context.getArguments().length  > 0 ? context.getArgument(0) : "local";
		
		String start = context.getArguments().length > 1 ? context.getArgument(1) : "1980-01-01";
		String end = context.getArguments().length > 2 ? context.getArgument(2) : "now";
		
		Date startDate = parseDate(start);
		Date endDate = parseDate(end);

		System.out.println(repositories.keySet());
		List<ActivityOverview> activities = getRepository(name).findActivities(startDate, endDate);
			
		System.out.println(activities.size() + " activities found");
		for (ActivityOverview overview : activities) {
			double speed = 1.0 * overview.getDistance() / overview.getDuration();
			System.out.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM  %2$-15s  %3$7s  %4$8s  %5$3s  %6$14s  (%7$s)\n", 
					overview.getDate(), 
					overview.getSport(), 
					FormatUtils.formatDistance(overview.getDistance()), 
					FormatUtils.formatTime((int) overview.getDuration()),
					overview.getAverageHeartRate() == null ? "-" : overview.getAverageHeartRate(),
					"CYCLING".equals(overview.getSport())
							? FormatUtils.formatSpeedInKmh(speed)
							: FormatUtils.formatSpeedAsPace(speed),
					overview.getActivityId());
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
