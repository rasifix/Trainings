package com.github.rasifix.osgi.shell.internal;

import com.github.rasifix.osgi.shell.Command;

public interface CommandRegistry {
	
	String[] getCommandNames();
	
	Command getCommand(String name);
	
}
