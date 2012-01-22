package com.github.rasifix.trainings.shell.internal.commands.edit;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.NullCompletor;
import jline.SimpleCompletor;
import aQute.bnd.annotation.component.Component;

import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class EditCommand implements Command {

	private static final String NAME = "edit:sport";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Object execute(CommandContext context) {
		Object current = context.getCurrent();
		if (current instanceof Activity) {
			Activity activity = (Activity) current;
			String[] arguments = context.getArguments();
			if (arguments.length == 0) {
				System.err.println("missing argument <sport>");
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