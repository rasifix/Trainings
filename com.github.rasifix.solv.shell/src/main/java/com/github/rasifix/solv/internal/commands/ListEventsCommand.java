package com.github.rasifix.solv.internal.commands;

import java.util.List;

import jline.Completor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.solv.EventOverview;
import com.github.rasifix.solv.ResultService;

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
	public String getUsage() {
		return NAME + " [<year>]";
	}

	@Override
	public Completor getCompletor() {
		return null;
	}

	@Override
	public Object execute(final CommandContext context) throws Exception {
		int year = context.getArguments().length > 0 ? Integer.parseInt(context.getArgument(0)) : 2012;
		List<EventOverview> events = service.listEvents(year);
		for (int i = 0; i < events.size(); i++) {
			EventOverview event = events.get(i);
			System.out.println(String.format("%5s  %s", i + 1, event.getTitle()));			
		}
		
		context.put("solv:events", events);
		
		return context.getCurrent();
	}

}
