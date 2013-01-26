package com.github.rasifix.solv.service.internal;

public class ParseHelper {
	
	public static String decode(String in) {
		in = in.replaceAll("&ouml;", "ö");
		in = in.replaceAll("&auml;", "ä");
		in = in.replaceAll("&uuml;", "ü");
		in = in.replaceAll("&eacute;", "é");
		in = in.replaceAll("&egrave;", "è");
		in = in.replaceAll("&ecirc;", "ê");
		in = in.replaceAll("&acirc;", "â");
		in = in.replaceAll("&ccedil;", "ç");
		return in;

	}
	
}
