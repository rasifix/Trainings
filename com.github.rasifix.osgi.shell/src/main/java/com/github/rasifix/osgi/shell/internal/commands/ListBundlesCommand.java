package com.github.rasifix.osgi.shell.internal.commands;

import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;

import jline.Completor;
import jline.NullCompletor;

@Component
public class ListBundlesCommand implements Command {

	private static final String NAME = "osgi:bundles";
	
	private ComponentContext context;

	public void activate(ComponentContext context) {
		this.context = context;
	}
	
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
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		for (Bundle bundle : this.context.getBundleContext().getBundles()) {
			System.out.println(bundle.getBundleId() + ": " + bundle.getSymbolicName() + " " + bundle.getState());
		}
		return context.getCurrent();
	}

}
