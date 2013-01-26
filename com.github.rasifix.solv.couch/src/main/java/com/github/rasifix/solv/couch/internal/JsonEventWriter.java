package com.github.rasifix.solv.couch.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import com.github.rasifix.saj.JsonOutputHandler;
import com.github.rasifix.saj.JsonWriter;
import com.github.rasifix.saj.dom.JsonModelBuilder;
import com.github.rasifix.solv.Athlete;
import com.github.rasifix.solv.Category;
import com.github.rasifix.solv.Event;
import com.github.rasifix.solv.EventWriter;
import com.github.rasifix.solv.Split;
import com.github.rasifix.solv.Time;

public class JsonEventWriter implements EventWriter {

	@Override
	public void writeEvent(Event event, OutputStream stream) throws IOException {
		OutputStreamWriter streamWriter = new OutputStreamWriter(stream, "UTF-8");
		JsonWriter writer = new JsonWriter(streamWriter);
		
		writeEvent(event, writer);
		
		writer.close();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> writeEvent(Event event) {
		JsonModelBuilder builder = new JsonModelBuilder();
		writeEvent(event, builder);
		return (Map<String, Object>) builder.getResult();
	}

	private void writeEvent(Event event, JsonOutputHandler writer) {
		writer.startObject();
		writer.member("year", event.getYear());
		writer.member("title", event.getTitle());
		
		writer.startMember("categories");
		writerCategories(event, writer);
		writer.endMember();
		
		writer.endObject();
	}

	private void writerCategories(Event event, JsonOutputHandler writer) {
		writer.startObject();
		for (Category category : event.getCategories()) {
			writer.startMember(category.getName());
			writer.startObject();
//			writer.member("distance", category.getDistance());
//			writer.member("ascent", category.getAscent());
			
			writer.startMember("controls");
			writer.startArray();
			for (String control : category.getControls()) {
				writer.value(control);
			}
			writer.endArray();
			writer.endMember();
			
			writer.startMember("athletes");
			writeAthletes(category, writer);
			writer.endMember();
			
			writer.endObject();
			writer.endMember();
		}
		writer.endObject();
	}

	private void writeAthletes(Category category, JsonOutputHandler writer) {
		writer.startArray();
		for (Athlete athlete : category.getAthletes()) {
			writerAthlete(athlete, writer);
		}
		writer.endArray();
	}

	private void writerAthlete(Athlete athlete, JsonOutputHandler writer) {
		writer.startObject();
		writer.member("name", athlete.getName());
		writer.member("siCardNumber", athlete.getSiCardNumber());
		writer.member("year", athlete.getYearOfBirth());
		writer.member("city", athlete.getCity());
		writer.member("club", athlete.getClub());
		writer.member("startTime", athlete.getStartTime().format(Time.TimeFormat.HOUR_MINUTE));
		if (athlete.getTotalTime() != null) {
			writer.member("totalTime", athlete.getTotalTime().format(Time.TimeFormat.HOUR_MINUTE_SECOND));
		} else {
			writer.startMember("totalTime");
			writer.nullValue();
			writer.endMember();
		}
		
		writer.startMember("splits");
		writerSplits(athlete, writer);
		writer.endMember();
		
		writer.endObject();
	}

	private void writerSplits(Athlete athlete, JsonOutputHandler writer) {
		writer.startArray();
		for (Split split : athlete.getSplits()) {
			if (split.getTime() != null) {
				writer.value(split.getTime().format(Time.TimeFormat.MINUTE_SECOND));
			} else {
				writer.nullValue();
			}
		}
		writer.endArray();
	}

}
