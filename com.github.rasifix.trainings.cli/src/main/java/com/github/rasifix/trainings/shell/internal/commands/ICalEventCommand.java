package com.github.rasifix.trainings.shell.internal.commands;

import java.io.IOException;
import java.net.URL;

import jline.Completor;
import jline.NullCompletor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.ActivityExporter;
import com.github.rasifix.trainings.model.Activity;

@Component
public class ICalEventCommand implements Command {

	private static final String NAME = "ical:create";

	private volatile ActivityExporter exporter;
	
	@Reference(target="(exporter=ical)", unbind="removeExporter", dynamic=true)
	public void addExporter(ActivityExporter icalExporter) {
		this.exporter = icalExporter;
	}
	
	public void removeExporter(ActivityExporter exporter) {
		this.exporter = null;
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
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws IOException {
		Object current = context.getCurrent();
		if (current instanceof Activity) {
			URL result = exporter.exportActivity((Activity) current);
			System.out.println(result);
		}
		return current;
	}

}
