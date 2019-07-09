package com.github.rasifix.trainings.shell.internal.commands.equipment;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.equipment.EquipmentRepository;
import com.github.rasifix.trainings.model.Equipment;

import jline.Completor;
import jline.NullCompletor;

@Component
public class DeactivateEquipmentCommand implements Command {

	private static final String NAME = "equipment:deactivate";
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
		return NAME + " <id>";
	}
	
	@Override
	public Object execute(CommandContext context) throws Exception {
		if (context.getArguments().length != 1) {
			System.err.println(getUsage());
			return context.getCurrent();
		}
		
		String id = context.getArgument(0);
		
		Equipment equipment = repository.getEquipment(id);
		equipment.setActive(false);
		
		repository.addEquipment(equipment);
				
		return equipment;
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

}
