package com.github.rasifix.trainings.shell.internal.commands.solv;

import java.util.Calendar;
import java.util.List;

import jline.Completor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class ListRunnersCommand implements Command {

	private static final String NAME = "solv:runners";

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
		Category category = null;
		if (context.getArguments().length == 2) {
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			
			int eventIdx = Integer.parseInt(context.getArgument(0));
			String categoryName = context.getArgument(1);
			
			List<Event> events = service.listEvents(year);
			Event event = events.get(eventIdx - 1);
			List<Category> categories = service.listCategories(event);
			for (Category next : categories) {
				if (next.getName().equals(categoryName)) {
					category = next;
					break;
				}
			}
			
		} else {
			@SuppressWarnings("unchecked")
			List<Category> categories = (List<Category>) context.get("solv:categories");
			category = categories.get(Integer.parseInt(context.getArgument(0)) - 1);
		}
		
		List<Runner> runners = service.listRunners(category);
		for (int i = 0; i < runners.size(); i++) {
			Runner runner = runners.get(i);
			System.out.println(String.format("%5s  %s", runner.getName(), runner.getSplits()));
		}
		
		return context.getCurrent();
	}

}
