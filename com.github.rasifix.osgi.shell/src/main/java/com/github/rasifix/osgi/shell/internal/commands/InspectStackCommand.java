package com.github.rasifix.osgi.shell.internal.commands;

import java.util.Iterator;

import jline.Completor;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;

public class InspectStackCommand implements Command {

	private static final String NAME = "show:stack";

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME;
	}

	@Override
	public Completor getCompletor() {
		return null;
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		int i = 0;
		
		Iterator<Object> it = context.stackIterator();
		while (it.hasNext()) {
			Object next = it.next();
			System.out.println(String.format("%5d - %s", i, next));
		}
		
		return context.getCurrent();
	}

}
