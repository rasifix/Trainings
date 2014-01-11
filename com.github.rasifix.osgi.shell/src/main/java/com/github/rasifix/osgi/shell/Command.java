package com.github.rasifix.osgi.shell;

import jline.Completor;

public interface Command {

	String getName();
	
	String getUsage();
	
	Object execute(CommandContext context) throws Exception;

	Completor getCompletor();
	
}
