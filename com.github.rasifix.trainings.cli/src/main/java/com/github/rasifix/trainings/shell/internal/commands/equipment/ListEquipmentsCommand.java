package com.github.rasifix.trainings.shell.internal.commands.equipment;

import java.text.SimpleDateFormat;
import java.util.Date;

import jline.Completor;
import jline.NullCompletor;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.equipment.EquipmentRepository;
import com.github.rasifix.trainings.model.Equipment;
import com.github.rasifix.trainings.shell.Command;
import com.github.rasifix.trainings.shell.CommandContext;

@Component
public class ListEquipmentsCommand implements Command {
	
	private static final String NAME = "equipment:list";

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
		int idx = 0;
		for (Equipment equipment : repository.getAllEquipments()) {
			System.out.format("%3d  %20s  %15s  %10s  (%s)\n", 
					++idx, equipment.getName(), equipment.getBrand(), 
					format(equipment.getDateOfPurchase()), equipment.getId());
		}
		return context.getCurrent();
	}

	private String format(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

}
