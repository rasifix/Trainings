package com.github.rasifix.trainings.shell.internal.commands.repository;

public final class FormatUtils {

	private FormatUtils() {
		// hidden constructor
	}
	
	public static String formatSpeedAsPace(double speedInMetersPerSecond) {
		if (speedInMetersPerSecond == 0) {
			return "undefined";
		}
		double secondsPerKm = 1000 / speedInMetersPerSecond;
		int minutes = (int) secondsPerKm / 60;
		int seconds = (int) (secondsPerKm - minutes * 60);
		return pad(minutes) + ":" + pad(seconds) + " min:s/km";
	}
	
	public static String formatSpeedInKmh(double speedInMetersPerSecond) {
		double speedInKmh = speedInMetersPerSecond * 3.6;
		int kmh = (int) speedInKmh;
		int dkmh = (int) Math.round((speedInKmh - kmh) * 10);
		return kmh + "." + dkmh + " km/h";
	}

	public static String formatDistance(int distanceInMeters) {
		int km = distanceInMeters / 1000;
		int dkm = distanceInMeters / 100 % 10;
		return km + "." + dkm + " km";
	}

	public static String formatTime(int timeInSeconds) {
		int hours = timeInSeconds / 3600;
		int minutes = (timeInSeconds - hours * 3600) / 60;
		int seconds = timeInSeconds % 60;
		return pad(hours) + ":" + pad(minutes) + ":" + pad(seconds);
	}
	
	private static String pad(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("negative time");
		} else if (value < 10) {
			return "0" + value;
		} else {
			return "" + value;
		}
	}
	
}
