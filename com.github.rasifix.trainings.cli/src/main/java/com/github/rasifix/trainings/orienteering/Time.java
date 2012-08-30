package com.github.rasifix.trainings.orienteering;


public class Time implements Comparable<Time> {
	
	public static enum TimeFormat {
		HOUR_MINUTE, HOUR_MINUTE_SECOND, MINUTE_SECOND
	}
	
	private final int seconds;
	
	public Time(int seconds) {
		this.seconds = seconds;
	}
	
	public static final Time valueOf(String value, TimeFormat format) {
		String[] parts = value.split("[:.]");
		if (format == TimeFormat.HOUR_MINUTE) {
			return new Time(Integer.parseInt(parts[0]) * 3600 + Integer.parseInt(parts[1]) * 60);
		} else if (format == TimeFormat.MINUTE_SECOND) {
			return new Time(Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]));
		} else if (format == TimeFormat.HOUR_MINUTE_SECOND) {
			if (parts.length == 2) {
				return new Time(Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]));
			} else {
				return new Time(Integer.parseInt(parts[0]) * 3600 + Integer.parseInt(parts[1]) * 60 + Integer.parseInt(parts[2]));
			}
		}
		
		throw new IllegalArgumentException(value + " is not a valid time");
	}
	
	public int getHours() {
		return seconds / 3600;
	}
	
	public int getMinutes() {
		return seconds / 60;
	}
	
	public int getSeconds() {
		return seconds;
	}

	public Time add(Time time) {
		return new Time(this.seconds + time.seconds);
	}
	
	@Override
	public String toString() {
		return "Time[seconds=" + seconds + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj.getClass() == getClass()) {
			Time other = (Time) obj;
			return this.seconds == other.seconds;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return seconds;
	}

	@Override
	public int compareTo(Time time) {
		return this.seconds - time.seconds;
	}

	public String format(TimeFormat format) {
		if (format == TimeFormat.HOUR_MINUTE_SECOND) {
			return seconds / 3600 + ":" + pad((seconds / 60) % 60) + ":" + pad(seconds % 60);
		} else if (format == TimeFormat.HOUR_MINUTE) {
			return seconds / 3600 + ":" + pad((seconds / 60) % 60);
		} else {
			return seconds / 60 + ":" + pad(seconds % 60);
		}
	}

	private String pad(int value) {
		return value < 10 ? "0" + value : "" + value;
	}
	
}
