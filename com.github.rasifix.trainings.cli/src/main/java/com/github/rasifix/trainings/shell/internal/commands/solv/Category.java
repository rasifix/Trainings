package com.github.rasifix.trainings.shell.internal.commands.solv;

public class Category {
	
	private final String name;
	private final Event event;

	public Category(Event event, String name) {
		this.event = event;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public Event getEvent() {
		return event;
	}

}
