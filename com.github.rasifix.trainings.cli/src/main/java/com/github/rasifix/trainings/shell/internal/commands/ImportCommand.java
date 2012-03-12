package com.github.rasifix.trainings.shell.internal.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.FileNameCompletor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.ActivityImporter;
import com.github.rasifix.trainings.integration.resource.FileResource;
import com.github.rasifix.trainings.integration.resource.Resource;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class ImportCommand implements Command {

	private static final String NAME = "import";
	
	private ActivityImporter importer;
	
	@Reference(dynamic=true)
	public void setImporter(ActivityImporter importer) {
		this.importer = importer;
	}
	
	@Override
	public String getName() {
		return NAME;
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
				List<Activity> imported = importer.importActivities(resource);
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
		List<Activity> activities = importer.importActivities(resource);
		if (activities.size() == 1) {
			return activities.get(0);
		}
		return activities;
	}

	private List<String> parseSpec(CommandContext context, String specFile) throws IOException {
		List<File> files = context.resolveFiles(specFile);
		if (files.isEmpty()) {
			return Collections.emptyList();
		} else if (files.size() > 1) {
			throw new IllegalArgumentException("expecting exactly one spec file");
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(files.get(0)));
		String line = null;
		
		List<String> lines = new LinkedList<String>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		
		return lines;
	}

}
