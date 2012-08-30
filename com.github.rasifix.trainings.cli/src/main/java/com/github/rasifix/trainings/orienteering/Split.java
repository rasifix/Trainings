package com.github.rasifix.trainings.orienteering;

public class Split {
	
	private final String controlCode;
	private Time time;
	
	public Split(String controlCode, Time time) {
		this.controlCode = controlCode;
		this.time = time;
	}
	
	public String getControlCode() {
		return controlCode;
	}
	
	public Time getTime() {
		return time;
	}
	
	public String toString() {
		return "Split[control=" + controlCode + ",time=" + time.toString() + "]";
	}
	
}
