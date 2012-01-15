package com.github.rasifix.trainings.shell;

import jline.Completor;

public interface Command {
	
	Object execute(CommandContext context);

	String getName();

	Completor getCompletor();
	
}
