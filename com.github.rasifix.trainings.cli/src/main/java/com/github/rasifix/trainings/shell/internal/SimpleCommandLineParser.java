package com.github.rasifix.trainings.shell.internal;

import java.util.LinkedList;
import java.util.List;

public class SimpleCommandLineParser implements CommandLineParser {

	private int pos;
	private String line;

	@Override
	public String[] split(String line) {
		this.pos = 0;
		this.line = line.trim();
		
		List<String> parts = new LinkedList<String>();
		while (pos < this.line.length()) {
			parts.add(part());
			consume(' ');
		}
		
		return parts.toArray(new String[parts.size()]);
	}

	private void consume(char consume) {
		char c = next();
		while (c == consume) {
			c = next();
		}
		if (c != 0) {
			pos -= 1;
		}
	}

	private String part() {
		StringBuilder part = new StringBuilder();
		
		char c = next();
		if (c == '"') {
			String result = quoted(part);
			expect('"');
			return result;
		} else {
			pos -= 1;
			return unquoted(part);
		}
	}

	private void expect(char c) {
		pos -= 1;
		if (next() != '"') {
			throw new IllegalArgumentException("expecting closing quote");
		}
	}

	private String unquoted(StringBuilder part) {
		char c = next();
		while (isTokenChar(c)) {
			part.append(c);
			c = next();
		}
		return part.toString();
	}

	private char next() {
		if (pos == line.length()) {
			return 0;
		}
		return line.charAt(pos++);
	}

	private boolean isTokenChar(char c) {
		if (Character.isLetterOrDigit(c)) {
			return true;
		}
		
		switch (c) {
		case '-':
		case '_':
		case '/':
		case ':':
		case ';':
		case '.':
		case '!':
		case '(':
		case ')':
		case '[':
		case '~':
		case '*':
		case ']':
			return true;
		}
		
		return false;
	}

	private String quoted(StringBuilder part) {
		char c = next();
		while (!isQuoteChar(c)) {
			if (c == '\\') {
				c = next();
				if (c != '"') {
					throw new IllegalArgumentException("invalid escape sequence \\" + c);
				}
			}
			part.append(c);
			c = next();
		}
		return part.toString();
	}

	private boolean isQuoteChar(char c) {
		return c == '"';
	}

}
