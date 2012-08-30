package com.github.rasifix.trainings.orienteering.internal.commands;

import jline.Completor;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.orienteering.ResultService;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class ImportEvent implements Command {

	private static final String NAME = "solv:import";

	private ResultService service;
	
	@Reference
	public void setService(ResultService service) {
		this.service = service;
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Completor getCompletor() {
		return null;
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		int year = Integer.parseInt(context.getArgument(0));
		String title = context.getArgument(1);
		return service.getEvent(year, title);
	}

}