package com.github.rasifix.solv;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.rasifix.solv.Time;
import com.github.rasifix.solv.Time.TimeFormat;

public class TimeTest {

	@Test
	public void testFormat() throws Exception {
		Time time = new Time(3665);
		
		assertEquals("1:01:05", time.format(TimeFormat.HOUR_MINUTE_SECOND));
		assertEquals("61:05", time.format(TimeFormat.MINUTE_SECOND));
		assertEquals("1:01", time.format(TimeFormat.HOUR_MINUTE));
	}
	
	@Test
	public void testValueOf() throws Exception {
		assertEquals(3665, Time.valueOf("1:01:05", TimeFormat.HOUR_MINUTE_SECOND).getSeconds());
		assertEquals(3665, Time.valueOf("61:05", TimeFormat.MINUTE_SECOND).getSeconds());
		assertEquals(3660, Time.valueOf("1:01", TimeFormat.HOUR_MINUTE).getSeconds());
	}
	
}
