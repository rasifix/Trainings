package com.github.rasifix.trainings.model.attr;

public class AltitudeSummary implements AttributeSummary<AltitudeSummary> {

	private final int duration;
	
	private final int min;
	
	private final int avg;
	
	private final int max;
	
	private final int altGain;
	
	private final int altLoss;
	
	private final int startAltitude;
	
	private final int endAltitude;
	
	public AltitudeSummary(int duration, int min, int avg, int max, int altGain, int altLoss, int startAltitude, int endAltitude) {
		this.duration = duration;
		this.min = min;
		this.avg = avg;
		this.max = max;
		this.altGain = altGain;
		this.altLoss = altLoss;
		this.startAltitude = startAltitude;
		this.endAltitude = endAltitude;
	}
	
	public int getMin() {
		return min;
	}
	
	public int getAvg() {
		return avg;
	}
	
	public int getMax() {
		return max;
	}
	
	public int getAltGain() {
		return altGain;
	}
	
	public int getAltLoss() {
		return altLoss;
	}
	
	public int getStartAltitude() {
		return startAltitude;
	}
	
	public int getEndAltitude() {
		return endAltitude;
	}
	
	@Override
	public AltitudeSummary merge(AltitudeSummary summary) {
		int newGain = altGain + summary.altGain;
		int newLoss = altLoss + summary.altLoss;
		int newMin = Math.min(min, summary.min);
		int newMax = Math.max(max, summary.max);
		int newAvg = (duration * avg + summary.duration * avg) / (duration + summary.duration);
		return new AltitudeSummary(duration + summary.duration, newMin, newAvg, newMax, newGain, newLoss, startAltitude, summary.endAltitude);
	}

}
