package com.github.rasifix.solv.service.internal;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import com.github.rasifix.solv.Time;
import com.github.rasifix.solv.service.internal.ResultListParser.ResultListEntry;

public class ResultListParserTest {
	
	@Test
	public void testParse() throws Exception {
		ResultListParser parser = new ResultListParser();
		List<ResultListEntry> result = parser.parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("full-result-list.csv"))));
		assertEquals(1460, result.size());
		
		ResultListEntry entry = result.get(0);
		assertEntry(entry, "Matthias Kyburz", "2:22", "8:29", "2:44", "2:28", "0:45", "0:49", "1:35", "7:40", "2:37", "1:25", "9:30", "1:53", "1:46", "2:07", "1:55", "1:53", "3:52", "1:51", "1:51", "0:55", "2:18", "2:06", "4:50", "2:51", "2:42", "2:57", "2:14", "13:23", "0:48", "6:34", "0:20");
		
		entry = result.get(result.size() - 1);
		assertEntry(entry, "Lauren Zweifel", "9:10", "12:10", "6:12", "8:41", "11:51", "14:02", "8:08", "7:24", "7:42", "1:41");
	}
	
	private static void assertEntry(ResultListEntry entry, String name, String... splits) {
		assertEquals(name, entry.name);
		assertEquals(splits.length, entry.splits.size());
		for (int i = 0; i < splits.length; i++) {
			assertEquals(Time.valueOf(splits[i], Time.TimeFormat.MINUTE_SECOND), entry.splits.get(i));
		}
	}
	
}
