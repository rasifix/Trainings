package com.github.rasifix.solv.internal.commands;

import jline.Completor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.solv.ResultService;

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
		return service.queryEvent(year, title);
	}

}
