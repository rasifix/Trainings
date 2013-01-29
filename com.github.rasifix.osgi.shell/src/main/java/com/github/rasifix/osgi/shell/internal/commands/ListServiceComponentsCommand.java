package com.github.rasifix.osgi.shell.internal.commands;

import org.osgi.service.component.ComponentContext;

import jline.Completor;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;

@Component
public class ListServiceComponentsCommand implements Command {

	private ComponentContext context;

	@Activate
	public void doActivate(ComponentContext context) {
		System.out.println("ACTIVATE");
		this.context = context;
	}
	
	@Override
	public Object execute(CommandContext context) throws Exception {
		return null;
	}

	@Override
	public String getName() {
		return "osgi:components";
	}

	@Override
	public Completor getCompletor() {
		return null;
	}

}
