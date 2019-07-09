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
public class SetDefaultSportCommand implements Command {

	private static final String NAME = "equipment:default-sport";
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
		return NAME + " <id> <sport>";
	}
	
	@Override
	public Object execute(CommandContext context) throws Exception {
		if (context.getArguments().length != 2) {
			System.err.println(getUsage());
			return context.getCurrent();
		}
		
		String id = context.getArgument(0);
		String sport = context.getArgument(1);
		
		Equipment equipment = repository.getEquipment(id);
		equipment.setDefaultSport(sport);
		
		repository.addEquipment(equipment);
				
		return equipment;
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

}
