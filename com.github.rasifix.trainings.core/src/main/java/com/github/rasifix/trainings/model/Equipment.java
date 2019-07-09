package com.github.rasifix.trainings.model;

import java.util.Date;

public class Equipment {
	
	private String id;
	private String revision;
	private Date dateOfPurchase;
	private String name;
	private String brand;
	private boolean active;
	private String defaultSport;

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRevision() {
		return revision;
	}
	
	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	public Date getDateOfPurchase() {
		return dateOfPurchase;
	}

	public void setDateOfPurchase(Date dateOfPurchase) {
		this.dateOfPurchase = dateOfPurchase;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setDefaultSport(String defaultSport) {
		this.defaultSport = defaultSport;
	}
	
	public String getDefaultSport() {
		return defaultSport;
	}
	
}
