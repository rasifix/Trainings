package com.github.rasifix.trainings.shell.internal.commands.repository;

import java.util.Calendar;
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
public class RepoSyncCommand implements Command {

	private final Map<String, ServiceReference> repositories = new HashMap<String, ServiceReference>();
	private ComponentContext context;

	private ActivityRepository getRepository(String name) {
		return (ActivityRepository) context.getBundleContext().getService(repositories.get(name));
	}

	@Activate
	public void activate(ComponentContext context) {
		this.context = context;
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE, policy=ReferencePolicy.DYNAMIC, service=ActivityRepository.class, unbind="removeRepository")
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
		return "repo:sync";
	} 

	@Override
	public String getUsage() {
		return "repo:sync <source> <dest>";
	}

	@Override
	public Object execute(CommandContext ctx) throws Exception {
		String src = ctx.getArgument(0);
		String dst = ctx.getArgument(1);
		
		System.out.println("sync from " + src + " to " + dst);
		
		synchronized (repositories) {
			if (!repositories.containsKey(src)) {
				System.out.println(src + " repository not found");
				return ctx.getCurrent();
			}
			
			if (!repositories.containsKey(dst)) {
				System.out.println(dst + " repository not found");
				return ctx.getCurrent();
			}
			
			ActivityRepository source = getRepository(src);
			ActivityRepository destination = getRepository(dst);
			
			if (source != null && destination != null) {
				List<ActivityOverview> srcList = source.findActivities(null, null);
				List<ActivityOverview> dstList = destination.findActivities(null, null);
				
				System.out.println("found " + srcList.size() + " activities in source repository");
				System.out.println("found " + dstList.size() +  " activities in destination repository");
				
				for (ActivityOverview srcActivity : srcList) {
					ActivityOverview dstActivity = match(srcActivity, dstList);
					if (dstActivity == null) {
						System.out.println("sync missing: " + srcActivity.getActivityId() + " " + srcActivity.getSport() + " " + srcActivity.getDate() + " " + srcActivity.getDistance() + " " + formatDuration(srcActivity.getDuration()));
						destination.addActivity(source.getActivity(srcActivity.getActivityId()));
					}
				}
			}
		}
		
		return ctx.getCurrent();
	}

	private String formatDuration(long duration) {
		long seconds = duration % 60;
		long minutes = (duration / 60) % 60;
		long hours = duration / 3600;
		return pad(hours) + ":" + pad(minutes) + ":" + pad(seconds);
	}

	private String pad(long value) {
		return value < 10 ? "0" + value : "" + value;
	}

	private ActivityOverview match(ActivityOverview srcActivity, List<ActivityOverview> dstList) {
		for (ActivityOverview candidate : dstList) {
			if (Math.abs(candidate.getDate().getTime() - srcActivity.getDate().getTime()) < 60 * 1000) {
				return candidate;
			}
		}
		for (ActivityOverview candidate : dstList) {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(srcActivity.getDate());
			
			Calendar c2 = Calendar.getInstance();
			c2.setTime(candidate.getDate());
			
			if (Math.abs(candidate.getDistance() - srcActivity.getDistance()) < 250 && Math.abs(candidate.getDuration() - srcActivity.getDuration()) < 120) {
				if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH) && Math.abs(c1.get(Calendar.HOUR_OF_DAY) - c2.get(Calendar.HOUR_OF_DAY)) <= 3) {
					return candidate;
				}
			}
		}
		return null;
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

}
