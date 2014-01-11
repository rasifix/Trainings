package com.github.rasifix.trainings.shell.internal.commands.repository;

import java.io.IOException;

import jline.Completor;
import jline.NullCompletor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.ActivityRepository;

@Component
public class LoadCommand implements Command {

	private static final String NAME = "repo:load";

	private volatile ActivityRepository repository;
	
	@Reference(dynamic=true, unbind="removeRepository")
	public void addRepository(ActivityRepository repository) {
		this.repository = repository;
	}
	
	public void removeRepository(ActivityRepository repository) {
		this.repository = null;
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
		String activityId = context.getArguments()[0];
		return repository.getActivity(activityId);
	}

}
