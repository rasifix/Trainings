package com.github.rasifix.trainings.shell.internal.commands.solv;

import java.util.List;

import jline.Completor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;

@Component
public class ListEventsCommand implements Command {

	private static final String NAME = "solv:events";
	
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
	public Object execute(final CommandContext context) throws Exception {
		int year = context.getArguments().length > 0 ? Integer.parseInt(context.getArgument(0)) : 2012;
		List<Event> events = service.listEvents(year);
		for (int i = 0; i < events.size(); i++) {
			Event event = events.get(i);
			System.out.println(String.format("%5s  %s", i + 1, event.getName()));			
		}
		
		context.put("solv:events", events);
		
		return context.getCurrent();
	}

}
