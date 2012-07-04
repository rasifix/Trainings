package com.github.rasifix.trainings.shell.internal.commands.solv;

public class Event {
	
	private final int year;
	
	private final String name;

	public Event(int year, String name) {
		this.year = year;
		this.name = name;
	}
	
	public int getYear() {
		return year;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "Event[year=" + year + ", name=" + name + "]";
	}
	
}
