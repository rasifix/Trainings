package com.github.rasifix.solv.service.internal;

public class ParseHelper {
	
	public static String decode(String in) {
		in = in.replaceAll("&ouml;", "�");
		in = in.replaceAll("&auml;", "�");
		in = in.replaceAll("&uuml;", "�");
		in = in.replaceAll("&eacute;", "�");
		in = in.replaceAll("&egrave;", "�");
		in = in.replaceAll("&ecirc;", "�");
		in = in.replaceAll("&acirc;", "�");
		in = in.replaceAll("&ccedil;", "�");
		return in;

	}
	
}
