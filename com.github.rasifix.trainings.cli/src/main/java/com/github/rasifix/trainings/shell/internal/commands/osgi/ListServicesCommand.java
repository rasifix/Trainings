package com.github.rasifix.trainings.shell.internal.commands.osgi;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import jline.Completor;
import jline.NullCompletor;

import aQute.bnd.annotation.component.Component;

import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class ListServicesCommand implements Command {

	private static final String NAME = "osgi:services";
	
	private ComponentContext context;

	public void activate(ComponentContext context) {
		this.context = context;
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
	public Object execute(CommandContext context) throws Exception {
		String serviceClass = context.getArguments()[0];
		ServiceReference[] references = this.context.getBundleContext().getAllServiceReferences(serviceClass, null);
		for (ServiceReference reference : references) {
			System.out.println(reference.getBundle().getSymbolicName() + " - " + this.context.getBundleContext().getService(reference));
		}
		return context.getCurrent();
	}

}
