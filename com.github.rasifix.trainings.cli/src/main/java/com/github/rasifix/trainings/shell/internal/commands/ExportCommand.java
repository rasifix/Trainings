package com.github.rasifix.trainings.shell.internal.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.FileNameCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.format.ActivityWriter;
import com.github.rasifix.trainings.format.Format;
import com.github.rasifix.trainings.model.Activity;

@Component
public class ExportCommand implements Command {

	private static final String NAME = "export";
	
	private final Map<String, ServiceReference> formats = new HashMap<String, ServiceReference>();
	
	private ComponentContext context;
	
	@Activate
	public void activate(ComponentContext context) {
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
		return NAME + " <format> <outfile>";
	}

	@Override
	public Completor getCompletor() {
		return new ArgumentCompletor(new Completor[] {
			new SimpleCompletor(getFormatIds()),
			new FileNameCompletor(),
			new NullCompletor()
		});
	}
	
	private String[] getFormatIds() {
		synchronized (formats) {
			return formats.keySet().toArray(new String[0]);
		}
	}

	@Override
	public Object execute(CommandContext context) throws IOException {
		Object current = context.getCurrent();
		if (current instanceof Activity) {
			Activity activity = (Activity) current;
			String id = context.getArguments()[0];
			String fileName = context.getArguments()[1];
			
			if (fileName.startsWith("~/")) {
				fileName = System.getProperty("user.home") + fileName.substring(1);
			}
			File file = new File(fileName);

			ServiceReference reference = formats.get(id);
			if (reference != null) {
				Format format = (Format) this.context.getBundleContext().getService(reference);
				if (format != null) {
					ActivityWriter writer = format.createWriter();
					writer.writeActivity(activity, new FileOutputStream(file));
				}
			}
		}
		return current;
	}

}
