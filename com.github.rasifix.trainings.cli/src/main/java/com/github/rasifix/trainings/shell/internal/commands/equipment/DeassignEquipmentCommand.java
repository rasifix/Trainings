package com.github.rasifix.trainings.shell.internal.commands.equipment;

import org.osgi.service.component.annotations.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.model.Activity;

import jline.Completor;
import jline.NullCompletor;

@Component
public class DeassignEquipmentCommand implements Command {

	private static final String NAME = "equipment:deassign";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME + " - deassign all equipments";
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity activity = (Activity) context.getCurrent();
		
		activity.getEquipments().clear();
		
		return context.getCurrent();
	}

}
