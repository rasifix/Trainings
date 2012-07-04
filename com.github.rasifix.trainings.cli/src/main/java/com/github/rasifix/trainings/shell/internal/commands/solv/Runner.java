package com.github.rasifix.trainings.shell.internal.commands.solv;

import java.util.LinkedList;
import java.util.List;

public class Runner {
	
	private final List<String> splits = new LinkedList<String>();

	private final String name;

	public Runner(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void addSplit(String split) {
		this.splits.add(split);
	}
	
	public List<String> getSplits() {
		return splits;
	}
	
	@Override
	public String toString() {
		return name + splits;
	}

}
