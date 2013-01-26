package com.github.rasifix.solv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Category {

	private String name;
	
	private int distance;
	
	private int ascent;
	
	private List<String> controls = new ArrayList<String>();
	
	private List<Athlete> ranking = new ArrayList<Athlete>(); 

	public Category(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public int getDistance() {
		return distance;
	}
	
	public int getAscent() {
		return ascent;
	}

	public List<String> getControls() {
		return controls;
	}
	
	public void addAthlete(Athlete athlete) {
		ranking.add(athlete);
		athlete.setCategory(this);
	}

	public Collection<Athlete> getAthletes() {
		return ranking;
	}

	public void setControls(List<String> controls) {
		if (!this.controls.isEmpty() && !this.controls.equals(controls)) {
			System.err.println("CONTROLCODEMISMATCH " + name);
			System.err.println(this.controls);
			System.err.println(controls);
			return;
		}
		this.controls = controls;
	}

	public boolean hasControl(String controlCode) {
		return controls.contains(controlCode);
	}

	public int getControlIndex(String controlCode) {
		return controls.indexOf(controlCode);
	}

}
