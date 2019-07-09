package com.github.rasifix.osgi.shell.internal.commands;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;

import jline.Completor;
import jline.NullCompletor;

@Component
public class ScrCommand implements Command {
	
	private static final String NAME = "osgi:scr";
	private ServiceComponentRuntime runtime;
	private BundleContext context;
	
	@Reference
	public void setServiceComponentRuntime(ServiceComponentRuntime runtime) {
		this.runtime = runtime;
	}
	
	@Activate
	public void activate(BundleContext context) {
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
	public Object execute(CommandContext context) throws Exception {
		Collection<ComponentDescriptionDTO> components = runtime.getComponentDescriptionDTOs(this.context.getBundles());
		components.forEach(component -> {
			System.out.println(component.name);
			if ("com.github.rasifix.trainings.strava.StravaRepository".equals(component.name)) {
				runtime.getComponentConfigurationDTOs(component).forEach(config -> {
					System.out.println("  " + config.state);
				});
			}
		});
		
		return context.getCurrent();
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	
	
}
