package com.github.rasifix.trainings.shell.internal.commands.repository;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.ActivityRepository.ActivityOverview;
import com.github.rasifix.trainings.model.Activity;

import jline.Completor;
import jline.NullCompletor;

@Component
public class MigrateCommand implements Command {
	
	private static final String NAME = "repo:migrate";

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
	public String getUsage() {
		return NAME + " - migrate all activities by loading and storing them back to the repository";
	}
	
	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		List<ActivityOverview> activities = repository.findActivities(null, null);
		for (ActivityOverview overview : activities) {
			System.out.println("... migrating " + overview.getActivityId());
			Activity activity = repository.getActivity(overview.getActivityId());
			repository.addActivity(activity);
		}
		return context.getCurrent();
	}

}
