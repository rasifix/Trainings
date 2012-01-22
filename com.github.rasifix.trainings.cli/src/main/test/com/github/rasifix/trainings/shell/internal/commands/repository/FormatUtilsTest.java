package com.github.rasifix.trainings.shell.internal.commands.repository;

import org.junit.Test;

public class FormatUtilsTest {
	
	@Test
	public void testFormatSpeedAsPace() throws Exception {
		System.out.println(FormatUtils.formatSpeedAsPace(10000 / 3600.0));
	}
	
}
