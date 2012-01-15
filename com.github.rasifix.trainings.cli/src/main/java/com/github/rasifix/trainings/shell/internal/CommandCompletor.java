package com.github.rasifix.trainings.shell.internal;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

import com.github.rasifix.trainings.shell.Command;

public class CommandCompletor implements Completor {
	
	private final CommandRegistry registry;

	public CommandCompletor(CommandRegistry registry) {
		this.registry = registry;
	}
	
	@Override
	public int complete(String buf, int cursor, List candidates) {
		buf = buf == null ? "" : buf;
		
		int firstDelimiter = buf.indexOf(' ');
		if (firstDelimiter == -1) {
			SimpleCompletor completor = new SimpleCompletor(getCommandNames());
			return completor.complete(buf, cursor, candidates);
		}
		
		int start = firstDelimiter;
		while (start < buf.length() && buf.charAt(start++) == ' ') {
			// skip whitespace
		}
		
		// we do not have to modify the start if we reached the end of the buffer
		if (buf.charAt(start - 1) != ' ') {
			start -= 1;
		}
		
		// get the Completor of the command (if any)
		String command = buf.substring(0, start).trim();
		Completor completor = getCompletorForCommand(command);
		if (completor == null) {
			return -1;
		}
		
		int argCursor = cursor - start;
		String argBuffer = buf.substring(firstDelimiter + 1);
		return start + completor.complete(argBuffer, argCursor, candidates);
    }

	private Completor getCompletorForCommand(String commandName) {
		Command command = registry.getCommand(commandName);
		return command != null ? command.getCompletor() : null;
	}

	private String[] getCommandNames() {
		return registry.getCommandNames();
	}
}