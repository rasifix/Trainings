package com.github.rasifix.solv;

import java.util.LinkedList;
import java.util.List;


public class Athlete {

	private Category category;
	
	private String name;
	
	private String siCardNumber;
	
	private int yearOfBirth;
	
	private String city;
	
	private String club;
	
	private Time startTime;
	
	private List<Split> splits = new LinkedList<Split>();

	private Time totalTime;

	public Athlete(String name) {
		this.name = name;
	}
	
	void setCategory(Category category) {
		this.category = category;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSiCardNumber() {
		return siCardNumber;
	}
	
	public void setSiCardNumber(String siCardNumber) {
		this.siCardNumber = siCardNumber;
	}
	
	public int getYearOfBirth() {
		return yearOfBirth;
	}
	
	public void setYearOfBirth(int yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getClub() {
		return club;
	}
	
	public void setClub(String club) {
		this.club = club;
	}
	
	public Time getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}
	
	public List<Split> getSplits() {
		return splits;
	}

	public Split getSplit(String controlCode) {
		for (Split split : splits) {
			if (split.getControlCode().equals(controlCode)) {
				return split;
			}
		}
		return null;
	}

	public Split getSplit(int splitIdx) {
		return splits.get(splitIdx);
	}
	
	public void addSplit(Split split) {
		splits.add(split);
	}

	public void setTotalTime(Time totalTime) {
		this.totalTime = totalTime;
	}
	
	public Time getTotalTime() {
		return totalTime;
	}
	
}
