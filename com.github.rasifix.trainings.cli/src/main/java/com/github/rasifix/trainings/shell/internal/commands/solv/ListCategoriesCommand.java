package com.github.rasifix.trainings.shell.internal.commands.solv;

import java.util.List;

import jline.Completor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;

@Component
public class ListCategoriesCommand implements Command {

	private static final String NAME = "solv:categories";
	
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
		@SuppressWarnings("unchecked")
		final List<Event> events = (List<Event>) context.get("solv:events");
		
		if (events == null) {
			return context.getCurrent();
		}
		
		int eventIdx = Integer.parseInt(context.getArgument(0));
		if (eventIdx > events.size()) {
			return context.getCurrent();
		}
		
		final Event event = events.get(eventIdx - 1);
		List<Category> categories = service.listCategories(event);
		for (int i = 0; i < categories.size(); i++) {
			Category cat = categories.get(i);
			System.out.println(String.format("%5s  %s", i + 1, cat.getName()));
		}
		
		context.put("solv:categories", categories);
		
		return context.getCurrent();
	}
	
}
