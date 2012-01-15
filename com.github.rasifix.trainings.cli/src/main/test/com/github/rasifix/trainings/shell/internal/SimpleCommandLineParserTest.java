package com.github.rasifix.trainings.shell.internal;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class SimpleCommandLineParserTest {

	@Test
	public void testZeroArguments() throws Exception {
		String[] arguments = parse("");
		assertEquals(0, arguments.length);
	}
	
	@Test
	public void testOneArgument() throws Exception {
		String[] arguments = parse("argument");
		assertEquals(1, arguments.length);
		assertEquals("argument", arguments[0]);
	}
	
	@Test
	public void testOneArgumentSpecialChars() throws Exception {
		String[] arguments = parse("ar/g:u_m-ent");
		assertEquals(1, arguments.length);
		assertEquals("ar/g:u_m-ent", arguments[0]);
	}
	
	@Test
	public void testTwoArguments() throws Exception {
		String[] arguments = parse("argument foo");
		assertEquals(2, arguments.length);
		assertEquals("argument", arguments[0]);
		assertEquals("foo", arguments[1]);
	}
	
	@Test
	public void testQuotedArgument() throws Exception {
		String[] arguments = parse("\"argu ment\"");
		assertEquals(1, arguments.length);
		assertEquals("argu ment", arguments[0]);
	}
	
	@Test
	public void testQuotedArgumentWithEscapedQuote() throws Exception {
		String[] arguments = parse("\"argu\\\"bla\\\"ment\"");
		assertEquals(1, arguments.length);
		assertEquals("argu\"bla\"ment", arguments[0]);
	}
	
	@Test
	public void testMultiArguments() throws Exception {
		String[] arguments = parse("\"arg\" u \"ment foo\" 123");
		System.out.println(Arrays.asList(arguments));
		assertEquals(4, arguments.length);
		assertEquals("arg", arguments[0]);
		assertEquals("u", arguments[1]);
		assertEquals("ment foo", arguments[2]);
		assertEquals("123", arguments[3]);
	}

	private String[] parse(String line) {
		return new SimpleCommandLineParser().split(line);
	}
	
}
