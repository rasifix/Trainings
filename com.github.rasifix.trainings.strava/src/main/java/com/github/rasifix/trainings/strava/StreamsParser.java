package com.github.rasifix.trainings.strava;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rasifix.trainings.model.Position;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.CadenceAttribute;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.HeartRateAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;

public class StreamsParser {

	public List<Track> parseStreams(Date startTime, InputStream in) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.reader().readTree(in);

		Map<Type, JsonNode> streams = new HashMap<>();
		
		for (JsonNode streamNode : root) {
			Type type = Type.valueOf(streamNode.get("type").asText());
			streams.put(type, streamNode.get("data"));
		}
		
		Track track = new Track(startTime);
		int count = streams.get(Type.time).size();
		for (int i = 0; i < count; i++) {
			long elapsedTime = streams.get(Type.time).get(i).asLong() * 1000;
			Trackpoint trackpoint = new Trackpoint(elapsedTime);
			
			if (streams.containsKey(Type.distance)) {
				double distance = streams.get(Type.distance).get(i).asDouble();
				trackpoint.addAttribute(new DistanceAttribute(distance));
			}
			
			if (streams.containsKey(Type.altitude)) {
				double altitude = streams.get(Type.altitude).get(i).asDouble();
				trackpoint.addAttribute(new AltitudeAttribute(altitude));
			}
			
			if (streams.containsKey(Type.latlng)) {
				JsonNode latlng = streams.get(Type.latlng).get(i);
				Position position = new Position(latlng.get(0).asDouble(), latlng.get(1).asDouble());
				trackpoint.addAttribute(new PositionAttribute(position));
			}
			
			if (streams.containsKey(Type.heartrate)) {
				int hr = streams.get(Type.heartrate).get(i).asInt();
				trackpoint.addAttribute(new HeartRateAttribute(hr));
			}
			
			if (streams.containsKey(Type.cadence)) {
				int cadence = streams.get(Type.cadence).get(i).asInt();
				trackpoint.addAttribute(new CadenceAttribute(cadence));
			}
			
			track.addTrackpoint(trackpoint);
		}
		
		return Collections.singletonList(track);
	}
	
	protected enum Type {
		latlng, time, distance, altitude, heartrate, cadence
	}

}
