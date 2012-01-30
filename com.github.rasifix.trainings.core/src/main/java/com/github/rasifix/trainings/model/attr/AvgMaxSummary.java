package com.github.rasifix.trainings.model.attr;

public class AvgMaxSummary implements AttributeSummary<AvgMaxSummary> {

	private final int duration;
	private final int avg;
	private final int max;

	public AvgMaxSummary(int duration, int avg, int max) {
		this.duration = duration;
		this.avg = avg;
		this.max = max;
	}
	
	public int getAvg() {
		return avg;
	}
	
	public int getMax() {
		return max;
	}
	
	@Override
	public AvgMaxSummary merge(AvgMaxSummary summary) {
		int newAvg = (avg * duration + summary.avg * summary.duration) / (duration + summary.duration);
		return new AvgMaxSummary(duration + summary.duration, newAvg, Math.max(this.max, summary.max));
	}

}
