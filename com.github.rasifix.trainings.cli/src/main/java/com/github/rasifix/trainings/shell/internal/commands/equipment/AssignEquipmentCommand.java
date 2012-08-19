package com.github.rasifix.trainings.shell.internal.commands.equipment;

import jline.Completor;
import jline.NullCompletor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.equipment.EquipmentRepository;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Equipment;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

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
	public Completor getCompletor() {
		return new NullCompletor();
	}

	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity activity = (Activity) context.getCurrent();
		
		String equipmentId = context.getArgument(0);
		Equipment equipment = repository.getEquipment(equipmentId);
		if (equipment != null) {
			activity.addEquipment(equipment);
		}
		
		return context.getCurrent();
	}

}
