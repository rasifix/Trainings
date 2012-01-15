package com.github.rasifix.trainings.shell.internal.commands;

import jline.Completor;
import jline.NullCompletor;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class LoadCommand implements Command {

	private static final String NAME = "repo:load";

	private ActivityRepository repository;
	
	@Reference(dynamic=true)
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
		String activityId = context.getArguments()[0];
		Activity activity = repository.getActivity(activityId);
		return activity;
	}

}
