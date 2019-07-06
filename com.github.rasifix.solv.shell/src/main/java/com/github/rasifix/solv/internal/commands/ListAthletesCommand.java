package com.github.rasifix.solv.internal.commands;

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.solv.Athlete;
import com.github.rasifix.solv.Category;
import com.github.rasifix.solv.Event;

import jline.Completor;

@Component
public class ListAthletesCommand implements Command {

	private static final String NAME = "solv:athletes";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME + " <category>";
	}

	@Override
	public Completor getCompletor() {
		return null;
	}

	@Override
	public Object execute(final CommandContext context) throws Exception {
		Event event = (Event) context.getCurrent();

		String categoryName = context.getArgument(0);
		Category category = event.getCategory(categoryName);
		
		List<Athlete> athletes = category.getAthletes();
		for (int i = 0; i < athletes.size(); i++) {
			Athlete athlete = athletes.get(i);
			System.out.println(String.format("%3d  %5s  %s", i + 1, athlete.getName(), athlete.getSplits()));
		}
		
		return context.getCurrent();
	}

}
