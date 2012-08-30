package com.github.rasifix.trainings.orienteering;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Event {
	
	private int year;
	
	private String title;
	
	private Map<String, Category> categories = new HashMap<String, Category>();
	
	public Event(int year, String title) {
		this.year = year;
		this.title = title;
	}

	public int getYear() {
		return year;
	}

	public String getTitle() {
		return title;
	}
	
	public void addCategory(Category category) {
		categories.put(category.getName(), category);
	}
	
	public Category getCategory(String name) {
		return categories.get(name);
	}

	public List<Category> getCategories() {
		return new LinkedList<Category>(categories.values());
	}
	
	public List<Athlete> getAthletesAtControl(String controlCode) {
		List<Athlete> result = new LinkedList<Athlete>();
		for (Category category : getCategoriesWithControl(controlCode)) {
			for (Athlete athlete : category.getAthletes()) {
				if (athlete.getSplit(controlCode) != null && athlete.getSplit(controlCode).getTime() != null) {
					result.add(athlete);
				}
			}
		}
		return result;
	}

	private Collection<Category> getCategoriesWithControl(String controlCode) {
		Set<Category> result = new HashSet<Category>();
		for (Category category : categories.values()) {
			if (category.hasControl(controlCode)) {
				result.add(category);
			}
		}
		return result;
	}
	
}
