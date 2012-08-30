package com.github.rasifix.trainings.orienteering.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.cyberneko.html.parsers.SAXParser;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXHandler;
import org.xml.sax.InputSource;

import com.github.rasifix.trainings.orienteering.Time;
import com.github.rasifix.trainings.orienteering.Time.TimeFormat;

class StartListParser {
	
	List<StartListEntry> parse(BufferedReader reader) throws IOException {
		List<StartListEntry> result = new LinkedList<StartListEntry>();
		
		SAXParser parser = new SAXParser();
		SAXHandler handler = new SAXHandler();
		parser.setContentHandler(handler);
		try {
			parser.parse(new InputSource(reader));
			Document doc = handler.getDocument();
			Element root = doc.getRootElement();
			Element body = root.getChild("BODY");
			List<Element> pres = findDescendants("PRE", body);
			for (Element pre : pres) {
				int index = pre.getParentElement().getChildren().indexOf(pre);
				Element b = (Element) pre.getParentElement().getChildren().get(index - 1);
				String cat = b.getTextNormalize();
				
				String[] preLines = pre.getText().split("[\r\n]+");
				for (String preLine : preLines) {
					preLine = ParseHelper.decode(preLine);
					StartListEntry entry = new StartListEntry();
					entry.category = cat;
					entry.siCardNumber = parseField(preLine, 0, 7);
					entry.athlete = parseField(preLine, 8, 31);
					entry.yearOfBirth = Integer.parseInt(parseField(preLine, 31, 33));
					entry.place = parseField(preLine, 34, 53);
					entry.club = parseField(preLine, 53, 73);
					entry.startTime = Time.valueOf(parseField(preLine, 73, 78), TimeFormat.HOUR_MINUTE);
					result.add(entry);
				}
			}
			
			return result;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String parseField(String line, int start, int end) {
		return line.substring(start, end).trim();
	}

	protected static List<Element> findDescendants(String name, Element current) {
		List<Element> result = new LinkedList<Element>();
		findDescendants(result, name, current);
		return result;
	}

	private static void findDescendants(List<Element> result, String name, Element current) {
		if (name.equals(current.getName())) {
			result.add(current);
			return;
		}
		
		@SuppressWarnings("unchecked")
		List<Element> children = (List<Element>) current.getChildren();
		for (Element child : children) {
			findDescendants(result, name, child);
		}
	}

	class StartListEntry {
		
		String category;
		
		String siCardNumber;
		
		String athlete;
		
		int yearOfBirth;
		
		String place;
		
		String club;
		
		Time startTime;
		
	}
	
}
