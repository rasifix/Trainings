package com.github.rasifix.trainings.shell.internal.commands.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import com.github.rasifix.trainings.ActivityKey;
import com.github.rasifix.trainings.ActivityRepository;
import com.github.rasifix.trainings.model.Activity;

import jline.Completor;
import jline.NullCompletor;

@Component
public class StoreCommand implements Command {
	
	private static final String NAME = "repo:store";

	private final Map<String, ServiceReference> repositories = new HashMap<String, ServiceReference>();
	private ComponentContext context;

	private ActivityRepository getRepository(String name) {
		return (ActivityRepository) context.getBundleContext().getService(repositories.get(name));
	}

	@Activate
	public void activate(ComponentContext context) {
		this.context = context;
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE, policy=ReferencePolicy.DYNAMIC, service=ActivityRepository.class, unbind="removeRepository")
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
		return NAME + " - stores the current activity in the repository";
	}
	
	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws IOException {
		Object current = context.getCurrent();
		ActivityRepository repository = getRepository(context.getArguments().length > 0 ? context.getArgument(0) : "local");
		if (current instanceof Activity) {
			ActivityKey key = repository.addActivity((Activity) current);
			System.out.println(key.toURL());
		} else if (current instanceof List<?>) {
			for (Object next : (List<?>) current) {
				if (next instanceof Activity) {
					repository.addActivity((Activity) next);
				}
			}
		}
		return current;
	}

}
