package com.github.rasifix.trainings.shell.internal.commands.repository;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.model.Activity;

import jline.Completor;
import jline.NullCompletor;

@Component
public class DeleteCommand implements Command {

	private static final String NAME = "repo:delete";
	
	private ActivityRepository repository;

	@Reference(target = "(name=local)")
	public void setActivityRepository(ActivityRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME + " <activity-id>";
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws IOException {
		String activityId = context.getArgument(0);
		Activity activity = repository.getActivity(activityId);
		repository.removeActivity(activity.getId(), activity.getRevision());
		
		return context.getCurrent();
	}

}
