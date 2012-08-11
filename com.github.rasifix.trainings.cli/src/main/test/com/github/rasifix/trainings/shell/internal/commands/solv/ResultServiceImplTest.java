package com.github.rasifix.trainings.shell.internal.commands.solv;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

public class ResultServiceImplTest {
	
	@Test
	public void testParseRunners() throws Exception {
		List<Runner> runners = ResultServiceImpl.parseRunners(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("event-1.csv"))));
		assertEquals(52, runners.size());
		
		Runner runner = runners.get(13);
		assertEquals("Simon RŠss", runner.getName());
		assertEquals(12, runner.getSplits().size());
	}
	
}
