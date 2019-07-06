package com.github.rasifix.trainings.shell.internal.commands;

import org.osgi.service.component.annotations.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;

import jline.Completor;
import jline.NullCompletor;

@Component
public class OpenActivityCommand implements Command {

	private static final String NAME = "open";

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
	public Object execute(CommandContext context) throws Exception {
		String id = context.getArgument(0);
		String url = "http://localhost:5984/trainings/_design/app/ng/index.html#/activity/" + id;
		ProcessBuilder builder = new ProcessBuilder("open", url);
		builder.start();
		return context.getCurrent();
	}

}
