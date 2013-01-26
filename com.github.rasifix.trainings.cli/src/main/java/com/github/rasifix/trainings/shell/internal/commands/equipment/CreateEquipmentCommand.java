package com.github.rasifix.trainings.shell.internal.commands.equipment;

import java.text.SimpleDateFormat;
import java.util.Date;

import jline.Completor;
import jline.NullCompletor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.equipment.EquipmentRepository;
import com.github.rasifix.trainings.model.Equipment;

@Component
public class CreateEquipmentCommand implements Command {

	private static final String NAME = "equipment:create";
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
	public Object execute(CommandContext context) throws Exception {
		String name = context.getArgument(0);
		String brand = context.getArgument(1);
		Date dateOfPurchase = new SimpleDateFormat("yyyy-MM-dd").parse(context.getArgument(2));
		
		Equipment equipment = new Equipment();
		equipment.setName(name);
		equipment.setBrand(brand);
		equipment.setDateOfPurchase(dateOfPurchase);
		
		repository.addEquipment(equipment);
		
		return equipment;
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

}
