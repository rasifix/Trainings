package com.github.rasifix.trainings.shell.internal.commands.equipment;

import jline.Completor;
import jline.NullCompletor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.equipment.EquipmentRepository;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Equipment;

@Component
public class AssignEquipmentCommand implements Command {

	private static final String NAME = "equipment:assign";

	private EquipmentRepository repository;
	
	@Reference
	public void setRepository(EquipmentRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public String getUsage() {
		return NAME + " <equipment-id> - assign the specified equipment to the current activity";
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity activity = (Activity) context.getCurrent();
		
		if (context.getArguments().length != 1) {
			System.err.println(getUsage());
			return activity;
		}
		
		String equipmentId = context.getArgument(0);
		Equipment equipment = repository.getEquipment(equipmentId);
		if (equipment != null) {
			activity.addEquipment(equipment);
		}
		
		return context.getCurrent();
	}

}
