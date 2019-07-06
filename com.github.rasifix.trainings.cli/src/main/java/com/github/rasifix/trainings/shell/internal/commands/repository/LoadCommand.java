package com.github.rasifix.trainings.shell.internal.commands.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.ActivityRepository;

import jline.Completor;
import jline.NullCompletor;

@Component
public class LoadCommand implements Command {
	
	private static final String NAME = "repo:load";
	
	private final Map<String, ServiceReference> repositories = new HashMap<String, ServiceReference>();
	private ComponentContext context;

	private ActivityRepository getRepository(String name) {
		return (ActivityRepository) context.getBundleContext().getService(repositories.get(name));
	}

	@Activate
	public void activate(ComponentContext context) {
		this.context = context;
	}
	
	@Reference(policy=ReferencePolicy.DYNAMIC, cardinality=ReferenceCardinality.MULTIPLE, service=ActivityRepository.class, unbind="removeRepository")
	public void addRepository(ServiceReference ref) {
		String name = (String) ref.getProperty("name");
		synchronized (repositories) {
			repositories.put(name, ref);
		} 
	}
	
	public void removeRepository(ServiceReference ref) {
		synchronized (repositories) {
			repositories.remove(ref.getProperty("name"));
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME + " <activity-id>";
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws IOException {
		String repository = "local";
		String activityId = context.getArgument(0);
		if (context.getArguments().length > 1) {
			repository = context.getArgument(1);
		}
		return getRepository(repository).getActivity(activityId);
	}

}
