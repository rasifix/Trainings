package com.github.rasifix.osgi.shell.internal.commands;

import java.util.Arrays;
import java.util.function.Predicate;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;

import jline.Completor;
import jline.NullCompletor;

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
	public String getUsage() {
		return NAME + " <service-class>";
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		String serviceClass = context.getArguments().length == 0 ? null : context.getArguments()[0];
		ServiceReference<?>[] references = this.context.getBundleContext().getAllServiceReferences(serviceClass, null);
		if (references == null) {
			return context.getCurrent();
		}
		
		Predicate<ServiceReference<?>> predicate = ref -> true;
		if (context.getArguments().length == 1) {
			String objectClass = context.getArgument(0);
			predicate = ref -> Arrays.stream((String[]) ref.getProperty("objectClass")).anyMatch(prop -> objectClass.equals(prop));
		}
		
		for (ServiceReference<?> reference : references) {
			if (!predicate.test(reference)) {
				continue;
			}
			
			Long serviceId = (Long) reference.getProperty("service.id");
			String[] objectClass = (String[]) reference.getProperty("objectClass");
			
			System.out.println("Bundle-SymbolicName: " + reference.getBundle().getSymbolicName());
			System.out.println("service.id         = " + serviceId);
			for (String intf : objectClass) {
				System.out.println("objectClass        = " + intf);
			}
			
			for (String property : reference.getPropertyKeys()) {
				if (!"service.id".equals(property) && !"objectClass".equals(property)) {
					System.out.format("%-19s= %s\n", property, reference.getProperty(property));
				}
			}
			
			if (reference.getUsingBundles() != null) {
				for (Bundle bundle : reference.getUsingBundles()) {
					System.out.println("used-by            : " + bundle.getSymbolicName());
				}
			}
			
			System.out.println();
		}
		return context.getCurrent();
	}

}
