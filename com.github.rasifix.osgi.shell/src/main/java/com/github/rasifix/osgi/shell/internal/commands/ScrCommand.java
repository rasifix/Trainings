package com.github.rasifix.osgi.shell.internal.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
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
	private ConfigurationAdmin configAdmin;
	
	@Reference
	public void setServiceComponentRuntime(ServiceComponentRuntime runtime) {
		this.runtime = runtime;
	}
	
	@Reference
	public void setConfigAdmin(ConfigurationAdmin configAdmin) {
		this.configAdmin = configAdmin;
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
		
		System.out.println(runtime.getClass().getName());
		
		Predicate<ComponentDescriptionDTO> filter = component -> true;
		if (context.getArguments().length == 1) {
			filter = component -> component.name.equals(context.getArgument(0));
		}
		
		components.stream().filter(filter).forEach(component -> {
			System.out.println("  component.name     = " + component.name);
			System.out.println("  bundle             = " + component.bundle.symbolicName);
			System.out.println("  configuration pids = " + Arrays.toString(component.configurationPid));
			if (component.references.length > 0) {
				System.out.println("  references         = " + Arrays.stream(component.references).map(r -> r.interfaceName).collect(Collectors.toList()));
			}
			System.out.println("  enabled            = " + runtime.isComponentEnabled(component));

			if (component.name.equals("com.github.rasifix.trainings.strava.StravaRepository")) {
				try {
					Hashtable<String, String> props = new Hashtable<>();
					props.put("token", "123");
					configAdmin.getConfiguration(component.configurationPid[0]).update(props);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			runtime.getComponentConfigurationDTOs(component).forEach(config -> {
				System.out.println("  state              = " + config.state);
				if (config.service != null) {
					System.out.println("  service = " + config.service.properties);
					System.out.println("  objectClass = " + Arrays.toString(((String[]) config.service.properties.get("objectClass"))));
				}
			});
			
			System.out.println();
		});
		
		return context.getCurrent();
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	
	
}
