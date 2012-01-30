package com.github.rasifix.trainings.equipment;

import java.io.IOException;
import java.util.List;

import com.github.rasifix.trainings.model.Equipment;


public interface EquipmentRepository {
	
	String addEquipment(Equipment equipment) throws IOException;
	
	Equipment getEquipment(String id) throws IOException;
	
	List<Equipment> getAllEquipments() throws IOException;
	
}
