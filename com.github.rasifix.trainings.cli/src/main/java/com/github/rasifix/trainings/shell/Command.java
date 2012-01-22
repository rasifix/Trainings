package com.github.rasifix.trainings.shell;

import jline.Completor;

public interface Command {
	
	Object execute(CommandContext context) throws Exception;

	String getName();

	Completor getCompletor();
	
}
