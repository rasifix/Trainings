package com.github.rasifix.solv.internal.commands;

import java.util.List;

import jline.Completor;
import aQute.bnd.annotation.component.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.solv.Category;
import com.github.rasifix.solv.Event;

@Component
public class ListCategoriesCommand implements Command {

	private static final String NAME = "solv:categories";

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
		Event event = (Event) context.getCurrent();
		
		List<Category> categories = event.getCategories();
		for (int i = 0; i < categories.size(); i++) {
			Category cat = categories.get(i);
			System.out.println(String.format("%5s  %s", i + 1, cat.getName()));
		}
		
		context.put("solv:categories", categories);
		
		return event;
	}
	
}
