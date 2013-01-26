package com.github.rasifix.solv.service.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import com.github.rasifix.solv.Time;

class ResultListParser {
	
	List<ResultListEntry> parse(Reader reader) throws IOException {
		List<ResultListEntry> result = new LinkedList<ResultListEntry>();

		parseRunners(result, new BufferedReader(reader));
		
		return result;
	}

	protected static void parseRunners(List<ResultListEntry> entries, BufferedReader reader) throws IOException {
		// first line is header - we don't care as it is obviously wrong...
		reader.readLine();
		
		String line = reader.readLine();
		while (line != null) {
			// unescape html escape sequences (don't ask why they are in a CSV file...)
			line = ParseHelper.decode(line);

			// ignore empty line at end
			if (line.trim().length() == 0) {
				break;
			}
			
			// poor mans CSV parser (seems to work ok)
			String[] split = line.split(";");
			
			// get the name of the poor ole runner
			ResultListEntry entry = new ResultListEntry();
			entry.name = split[1];
			entry.yearOfBirth = split[2].length() > 0 ? Integer.parseInt(split[2]) : -1;
			try {
				entry.totalTime = Time.valueOf(split[5].substring(0, split[5].length() - "</b>".length()), Time.TimeFormat.HOUR_MINUTE_SECOND);
			} catch (NumberFormatException e) {
				// ignored
			}
			if (entry.totalTime == null) {
				System.out.println(Arrays.toString(split));
			}
			entries.add(entry);
			
			// absolute times (don't care, can be reconstructed)
			line = reader.readLine();

			while (line != null && line.length() > 0 && line.charAt(0) == ' ') {
				// split times
				line = reader.readLine();

				// parse split times
				String control = parseField(line, 3, 6);
				String field = parseField(line, 6, 13);
				if (field != null) {
					entry.splits.add(new Split(control, parseTime(field)));
				}
				
				control = parseField(line, 18, 21);
				field = parseField(line, 21, 28);
				if (field != null) {
					entry.splits.add(new Split(control, parseTime(field)));
				}
				
				control = parseField(line, 33, 36);
				field = parseField(line, 36, 43);
				if (field != null) {
					entry.splits.add(new Split(control, parseTime(field)));
				}
								
				control = parseField(line, 48, 51);
				field = parseField(line, 51, 58);
				if (field != null) {
					entry.splits.add(new Split(control, parseTime(field)));
				}
				
				control = parseField(line, 63, 66);
				field = parseField(line, 66, 73);
				if (field != null) {
					entry.splits.add(new Split(control, parseTime(field)));
				}

				// split lost time
				line = reader.readLine();

				// next line: absolute times or next runner
				line = reader.readLine();
			}
		}
	}

	private static Time parseTime(String field) {
		if (field == null || field.equals("--")) {
			return null;
		}
		return Time.valueOf(field, Time.TimeFormat.MINUTE_SECOND);
	}

	protected static String parseField(String line, int start, int end) {
		if (line.length() < start) {
			return null;
		} else if (line.length() < end) {
			end = line.length();
		}
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
	
	static class ResultListEntry {
		
		public Time totalTime;

		String name;
		
		int yearOfBirth;
		
		List<Split> splits = new LinkedList<Split>();
		
	}
	
	static class Split {
		
		String control;
		
		Time time;
		
		public Split(String control, Time time) {
			this.control = control;
			this.time = time;
		}
		
	}
	
}
