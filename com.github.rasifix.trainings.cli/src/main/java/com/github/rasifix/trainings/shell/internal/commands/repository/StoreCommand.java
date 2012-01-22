package com.github.rasifix.trainings.shell.internal.commands.repository;

import java.io.IOException;
import java.util.List;

import jline.Completor;
import jline.NullCompletor;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.ActivityKey;
import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class StoreCommand implements Command {
	
	private static final String NAME = "repo:store";

	private ActivityRepository repository;
	
	@Reference(dynamic=true)
	public void setActivityRepository(ActivityRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) {
		Object current = context.getCurrent();
		try {
			if (current instanceof Activity) {
				ActivityKey key = repository.addActivity((Activity) current);
				System.out.println(key.toURL());
			} else if (current instanceof List<?>) {
				for (Object next : (List<?>) current) {
					if (next instanceof Activity) {
						repository.addActivity((Activity) next);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return current;
	}

}
