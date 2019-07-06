package com.github.rasifix.trainings.shell.internal.commands.edit;

import org.osgi.service.component.annotations.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.NullCompletor;
import jline.SimpleCompletor;

@Component
public class EditCommand implements Command {

	private static final String NAME = "edit:sport";

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME + " <sport>";
	}

	@Override
	public Object execute(CommandContext context) {
		Object current = context.getCurrent();
		if (current instanceof Activity) {
			Activity activity = (Activity) current;
			String[] arguments = context.getArguments();
			if (arguments.length == 0) {
				System.err.println(getUsage());
				return current;
			}
			
			for (Track track : activity.getTracks()) {
				track.setSport(arguments[0]);
			}
		}
		return current;
	}

	@Override
	public Completor getCompletor() {
		return new ArgumentCompletor(new Completor[] {
			new SimpleCompletor(new String[] { "RUNNING", "CYCLING", "ORIENTEERING" }),
			new NullCompletor()
		});
	}

}
