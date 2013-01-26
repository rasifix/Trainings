package com.github.rasifix.solv.internal.commands;

import jline.Completor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.solv.Event;
import com.github.rasifix.solv.EventKey;
import com.github.rasifix.solv.EventRepository;

@Component
public class StoreEventCommand implements Command {

	private static final String NAME = "repo:event:store";

	private EventRepository repository;
	
	@Reference
	public void setRepository(EventRepository repository) {
		this.repository = repository;
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
		Event event = (Event) context.getCurrent();
		
		EventKey key = repository.addEvent(event);
		System.out.println(key.toURL());
		
		return event;
	}

}
