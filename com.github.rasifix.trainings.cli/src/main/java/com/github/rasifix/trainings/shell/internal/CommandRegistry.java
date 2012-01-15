package com.github.rasifix.trainings.shell.internal;

import com.github.rasifix.trainings.shell.Command;

public interface CommandRegistry {
	
	String[] getCommandNames();
	
	Command getCommand(String name);
	
}
