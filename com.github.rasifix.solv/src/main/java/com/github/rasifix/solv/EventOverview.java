package com.github.rasifix.solv;

public class EventOverview {
	
	private final int year;
	
	private final String title;

	public EventOverview(int year, String title) {
		this.year = year;
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getYear() {
		return year;
	}
	
	@Override
	public String toString() {
		return "EventOverview[year=" + year + ",title=" + title + "]";
	}
	
}
