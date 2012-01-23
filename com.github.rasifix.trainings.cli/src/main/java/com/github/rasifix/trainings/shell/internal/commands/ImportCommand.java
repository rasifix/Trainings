package com.github.rasifix.trainings.shell.internal.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
		return new FileNameCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws IOException {
		for (String arg : context.getArguments()) {
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
		return null;
	}

}
