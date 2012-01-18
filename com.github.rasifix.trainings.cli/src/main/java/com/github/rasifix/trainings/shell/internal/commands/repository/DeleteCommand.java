package com.github.rasifix.trainings.shell.internal.commands.repository;

import java.io.IOException;
import java.util.List;

import jline.Completor;
import jline.NullCompletor;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

import com.github.rasifix.trainings.ActivityRepository.ActivityOverview;

@Component
public class DeleteCommand implements Command {

	private static final String NAME = "repo:delete";
	
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
//		int index = Integer.parseInt(context.getArguments()[0]);
//		if (context.containsKey("com.github.rasifix.trainings.repository.activity-overview")) {
//			List<ActivityOverview> activities = (List<ActivityOverview>) context.get("com.github.rasifix.trainings.repository.activity-overview");
//			ActivityOverview overview = activities.get(index);
//			//repository.removeActivity();
//		}
		String activityId = context.getArguments()[0];
		try {
			Activity activity = repository.getActivity(activityId);
			repository.removeActivity(activity.getId(), activity.getRevision());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return context.getCurrent();
	}

}
