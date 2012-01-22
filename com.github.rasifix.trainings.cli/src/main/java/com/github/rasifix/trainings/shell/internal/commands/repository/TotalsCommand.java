package com.github.rasifix.trainings.shell.internal.commands.repository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import jline.Completor;
import jline.NullCompletor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.ActivityRepository.ActivityOverview;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class TotalsCommand implements Command {

	private static final String NAME = "repo:totals";

	private ActivityRepository repository;
	
	@Reference
	public void setRepository(ActivityRepository repository) {
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
		if (context.getArguments().length < 2) {
			System.out.println("usage: repo:totals [start-date|last|this] [week|month|year]");
		}
		TimePeriod period = TimePeriod.valueOf(context.getArguments()[1].toUpperCase());
		
		String spec = context.getArguments()[0];
		Date startDate = null;
		if ("last".equals(spec)) {
			startDate = period.getNowMinusPeriod();
		} else if ("this".equals(spec)) {
			startDate = period.getStartOfThis();
		} else {
			startDate = parseDate(spec);
		}
		Date endDate = period.getEndDate(startDate);
		try {
			List<ActivityOverview> activities = repository.findActivities(startDate, endDate);
			System.out.println(activities.size() + " activities");
			
			Map<String, List<ActivityOverview>> groups = groupBySport(activities);
			for (Entry<String, List<ActivityOverview>> entry : groups.entrySet()) {
				System.out.println(entry.getKey() + " - " + entry.getValue().size() + " activities");
				System.out.println("  " + totalTime(entry.getValue()));
				System.out.println("  " + totalDistance(entry.getValue()));
				if (entry.getKey().equals("CYCLING")) {
					System.out.println("  " + averageSpeed(entry.getValue()));
				} else {
					System.out.println("  " + averagePace(entry.getValue()));
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return context.getCurrent();
	}

	private String averageSpeed(List<ActivityOverview> overviews) {
		int distance = 0;
		for (ActivityOverview overview : overviews) {
			distance += overview.getDistance();
		}
		
		int time = 0;
		for (ActivityOverview overview : overviews) {
			time += overview.getDuration();
		}
		
		return FormatUtils.formatSpeedInKmh(1.0 * distance / time);
	}

	private String averagePace(List<ActivityOverview> overviews) {
		int distance = 0;
		for (ActivityOverview overview : overviews) {
			distance += overview.getDistance();
		}
		
		int time = 0;
		for (ActivityOverview overview : overviews) {
			time += overview.getDuration();
		}
		
		return FormatUtils.formatSpeedAsPace(1.0 * distance / time);
	}

	private static String totalDistance(List<ActivityOverview> overviews) {
		int result = 0;
		for (ActivityOverview overview : overviews) {
			result += overview.getDistance();
		}
		return FormatUtils.formatDistance(result);
	}

	private static String totalTime(List<ActivityOverview> overviews) {
		int result = 0;
		for (ActivityOverview overview : overviews) {
			result += overview.getDuration();
		}
		return FormatUtils.formatTime(result);
	}

	private Map<String, List<ActivityOverview>> groupBySport(List<ActivityOverview> activities) {
		Map<String, List<ActivityOverview>> result = new TreeMap<String, List<ActivityOverview>>();
		for (ActivityOverview overview : activities) {
			List<ActivityOverview> group = result.get(overview.getSport());
			if (group == null) {
				group = new LinkedList<ActivityOverview>();
			}
			group.add(overview);
			result.put(overview.getSport(), group);
		}
		return result;
	}

	private Date parseDate(String spec) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(spec);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static enum TimePeriod {
		
		WEEK {
			@Override
			public Date getEndDate(Date startDate) {
				Calendar c = Calendar.getInstance();
				c.setTime(startDate);
				c.add(Calendar.DAY_OF_MONTH, 7);
				return c.getTime();
			} 
			
			@Override
			public Date getNowMinusPeriod() {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DAY_OF_MONTH, -7);
				return c.getTime();
			}
			
			@Override
			public Date getStartOfThis() {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.DAY_OF_WEEK, 1);
				return getStartOfDay(c.getTime());
			}
		},
		
		MONTH {
			 @Override
			public Date getEndDate(Date startDate) {
				Calendar c = Calendar.getInstance();
				c.setTime(startDate);
				c.add(Calendar.MONTH, 1);
				return c.getTime();
			}
			 
			@Override
			public Date getNowMinusPeriod() {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.MONTH, -1);
				return c.getTime();
			}
			 
			@Override
			public Date getStartOfThis() {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.DAY_OF_MONTH, 1);
				return getStartOfDay(c.getTime());
			}
		},
		
		YEAR {
			@Override
			public Date getEndDate(Date startDate) {
				Calendar c = Calendar.getInstance();
				c.setTime(startDate);
				c.add(Calendar.YEAR, 1);
				return c.getTime();
			}
			
			@Override
			public Date getNowMinusPeriod() {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.YEAR, -1);
				return c.getTime();
			}
			
			@Override
			public Date getStartOfThis() {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.MONTH, Calendar.JANUARY);
				return getStartOfDay(c.getTime());
			}
		};
		
		public abstract Date getEndDate(Date startDate);

		public abstract Date getStartOfThis();

		public abstract Date getNowMinusPeriod();
		
		protected Date getStartOfDay(Date day) {
			Calendar c = Calendar.getInstance();
			c.setTime(day);
			c.clear(Calendar.HOUR_OF_DAY);
			c.clear(Calendar.MINUTE);
			c.clear(Calendar.SECOND);
			c.clear(Calendar.MILLISECOND);
			return c.getTime();
		}
		
	}

}
