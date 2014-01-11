package com.github.rasifix.trainings.shell.internal.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.FileNameCompletor;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.format.Format;
import com.github.rasifix.trainings.integration.resource.FileResource;
import com.github.rasifix.trainings.integration.resource.Resource;
import com.github.rasifix.trainings.model.Activity;

@Component
public class ImportCommand implements Command {

	private static final String NAME = "import";
	
	private final Map<String, ServiceReference> formats = new HashMap<String, ServiceReference>();
	
	private ComponentContext context;
	
	@Activate
	public void doActivate(ComponentContext context) {
		this.context = context;
	}
	
	@Reference(service=Format.class, unbind="removeFormat", dynamic=true, multiple=true)
	public void addFormat(ServiceReference format) {
		String id = (String) format.getProperty("com.github.rasifix.trainings.format");
		synchronized (formats) {
			formats.put(id, format);
		}
	}
	
	public void removeFormat(ServiceReference format) {
		String id = (String) format.getProperty("com.github.rasifix.trainings.format");
		synchronized (formats) {
			formats.remove(id);
		}
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		StringBuilder builder = new StringBuilder();
		builder.append(NAME).append(" <specfile> <files>+\n");
		builder.append(NAME).append(" <file>");
		return builder.toString();
	}

	@Override
	public Completor getCompletor() {
		return new ArgumentCompletor(new Completor[] { new FileNameCompletor() });
	}

	@Override
	public Object execute(CommandContext context) throws IOException {
		if (context.getArguments().length == 0) {
			return context.getCurrent();
		}
		
		if (context.getArgument(0).endsWith(".spec")) {
			String spec = context.getArgument(0);
			List<String> commands = parseSpec(context, spec);
			
			List<File> files = new LinkedList<File>();
			for (int i = 1; i < context.getArguments().length; i++) {
				System.out.println("argument " + i + " = " + context.getArgument(i));
				files.addAll(context.resolveFiles(context.getArgument(i)));
			}
			
			System.out.println("... about to import " + files.size() + " files");
			for (File file : files) {
				System.out.println("... importing " + file.getName());
				
				FileResource resource = new FileResource(file);
				context.put("resource", resource);
				
				List<Activity> imported = importActivities(resource);
				if (imported.size() > 1 || imported.isEmpty()) {
					System.out.println("... skipping");
					continue;
				}
				context.setCurrent(imported.get(0));
				
				for (String command : commands) {
					System.out.println("... > " + command);
					context.execute(command);
				}
			}
			
			return null;
		}
		
		String arg = context.getArgument(0);
		System.out.println(arg);
		if (arg.startsWith("~/")) {
			arg = System.getProperty("user.home") + arg.substring(1);
		}
		File file = new File(arg);
		if (!file.exists()) {
			System.err.println(file.getAbsolutePath() + " does not exist");
		}
		Resource resource = new FileResource(file);
		context.put("resource", resource);
		
		List<Activity> activities = importActivities(resource);
		if (activities.size() == 1) {
			return activities.get(0);
		}
		return activities;
	}

	private List<Activity> importActivities(Resource resource) throws IOException {
		Format format = findFormat(resource);
		if (format == null) {
			System.err.println("... no matching format found, skipping");
			return new LinkedList<Activity>();
		}
		
		return format.createReader().readActivities(resource.openInputStream());
	}

	private Format findFormat(Resource resource) throws IOException {
		synchronized (formats) {
			for (ServiceReference reference : formats.values()) {
				Format format = (Format) context.getBundleContext().getService(reference);
				if (format.canRead(resource)) {
					return format;
				}
			}
		}
		return null;
	}

	private List<String> parseSpec(CommandContext context, String specFile) throws IOException {
		List<File> files = context.resolveFiles(specFile);
		if (files.isEmpty()) {
			return Collections.emptyList();
		} else if (files.size() > 1) {
			throw new IllegalArgumentException("expecting exactly one spec file");
		}
		
		List<String> lines = new LinkedList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(files.get(0)));
		try {
			String line = null;			
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} finally {
			reader.close();
		}
		
		return lines;
	}

}
