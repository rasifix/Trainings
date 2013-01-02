package com.github.rasifix.trainings.shell.internal.commands;

import java.io.File;
import java.text.SimpleDateFormat;

import jline.Completor;

import aQute.bnd.annotation.component.Component;

import com.github.rasifix.trainings.integration.resource.FileResource;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class MoveCommand implements Command {

	private static final String NAME = "move";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Completor getCompletor() {
		return null;
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity activity = (Activity) context.getCurrent();
		
		FileResource resource = (FileResource) context.get("resource");
		File file = resource.getFile();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
		
		File target = new File("/Users/sir/Dropbox/garmin/" + format.format(activity.getStartTime()));
		target.mkdirs();
		
		target = new File (target, file.getName());
		file.renameTo(target);
		
		return context.getCurrent();
	}

}
