package com.github.rasifix.trainings.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface Activity extends HasSummary {

	List<Track> getTracks();

	int getTrackCount();
	
	Track getTrack(int trackIdx);

	String getId();

	String getRevision();

	Date getStartTime();
	
	Date getEndTime();

	Collection<Equipment> getEquipments();

	void addEquipment(Equipment equipment);
	
}
