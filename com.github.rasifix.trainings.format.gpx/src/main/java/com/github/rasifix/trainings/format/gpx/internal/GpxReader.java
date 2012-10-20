package com.github.rasifix.trainings.format.gpx.internal;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticMeasurement;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.gavaghan.geodesy.GlobalPosition;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import aQute.bnd.annotation.component.Reference;

import com.github.rasifix.trainings.ElevationModel;
import com.github.rasifix.trainings.format.ActivityReader;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.ActivityImpl;
import com.github.rasifix.trainings.model.Position;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.attr.AltitudeAttribute;
import com.github.rasifix.trainings.model.attr.DistanceAttribute;
import com.github.rasifix.trainings.model.attr.PositionAttribute;

public class GpxReader implements ActivityReader {

	private static final Namespace NS_GPX_1_1 = Namespace.getNamespace("http://www.topografix.com/GPX/1/1");
	
	private ElevationModel elevationModel;
	
	@Reference(optional=true)
	public void setElevationModel(ElevationModel elevationModel) {
		this.elevationModel = elevationModel;
	}
	
	@Override
	public List<Activity> readActivities(InputStream inputStream) throws IOException {
		List<Activity> result = new LinkedList<Activity>();
		
		Document document = parseDocument(inputStream);
		if (document == null) {
			return result;
		}
		
		Element root = document.getRootElement();
		if ("gpx".equals(root.getName())) {
			Element rte = root.getChild("rte", NS_GPX_1_1);
			if (rte != null) {
				result.add(importRoute(rte));
			}
			
			Element trk = root.getChild("trk", NS_GPX_1_1);
			if (trk != null) {
				result.add(importTrack(trk));
			}
		}
		
		return result;
	}

	private Activity importRoute(Element rte) {
		Date startTime = new Date();
		ActivityImpl activity = new ActivityImpl(startTime);
		
		Position lastPosition = null;
		double distance = 0;
		double lastElevation = 0;
		
		int index = 0;
		Track track = new Track(startTime);
		@SuppressWarnings("unchecked")
		List<Element> children = rte.getChildren("rtept", NS_GPX_1_1);
		for (Element child : children) {
			Trackpoint trackpoint = new Trackpoint(index);
			double latitude = Double.parseDouble(child.getAttributeValue("lat"));
			double longitude = Double.parseDouble(child.getAttributeValue("lon"));
			double elevation = getElevationForPosition(latitude, longitude);
			
			Position position = new Position(latitude, longitude);
			trackpoint.addAttribute(new PositionAttribute(position));
			trackpoint.addAttribute(new AltitudeAttribute(elevation));
			
			if (lastPosition == null) {
				trackpoint.addAttribute(new DistanceAttribute(0));
			} else {
				GeodeticCalculator calc = new GeodeticCalculator();
				GlobalPosition start = new GlobalPosition(new GlobalCoordinates(lastPosition.getLatitude(), lastPosition.getLongitude()), lastElevation);
				GlobalPosition end = new GlobalPosition(new GlobalCoordinates(latitude, longitude), elevation);
				GeodeticMeasurement measurement = calc.calculateGeodeticMeasurement(Ellipsoid.WGS84, start, end);
				distance += measurement.getPointToPointDistance();
				trackpoint.addAttribute(new DistanceAttribute(distance));
			}
			
			track.addTrackpoint(trackpoint);
			
			// get ready for next iteration
			lastElevation = elevation;
			lastPosition = position;
			index += 1;
		}
		activity.addTrack(track);
		return activity;
	}

	private Activity importTrack(Element trk) {
		Date startTime = parseTime(trk);
		ActivityImpl activity = new ActivityImpl(startTime);
		
		Position lastPosition = null;
		double distance = 0;
		double lastElevation = 0;
		
		Track track = new Track(startTime);
		
		Element trkseg = trk.getChild("trkseg", NS_GPX_1_1);
		@SuppressWarnings("unchecked")
		List<Element> trkpts = trkseg.getChildren("trkpt", NS_GPX_1_1);
		for (Element trkpt : trkpts) {			
			// <trkpt lat="47.015954000" lon="7.455408000"><ele>563.0</ele><time>2012-01-01T15:45:47Z</time></trkpt>
			double latitude = Double.parseDouble(trkpt.getAttributeValue("lat"));
			double longitude = Double.parseDouble(trkpt.getAttributeValue("lon"));
			Position position = new Position(latitude, longitude);
			double elevation = Double.parseDouble(trkpt.getChildTextTrim("ele", NS_GPX_1_1));
			Date time = parseTime(trkpt);
			
			
			long elapsed = time.getTime() - startTime.getTime();
			Trackpoint trackpoint = new Trackpoint(elapsed);
			trackpoint.addAttribute(new PositionAttribute(position));
			trackpoint.addAttribute(new AltitudeAttribute(elevation));
			
			if (lastPosition == null) {
				trackpoint.addAttribute(new DistanceAttribute(0));
				
			} else {
				GeodeticCalculator calc = new GeodeticCalculator();
				GlobalPosition start = new GlobalPosition(new GlobalCoordinates(lastPosition.getLatitude(), lastPosition.getLongitude()), lastElevation);
				GlobalPosition end = new GlobalPosition(new GlobalCoordinates(latitude, longitude), elevation);
				GeodeticMeasurement measurement = calc.calculateGeodeticMeasurement(Ellipsoid.WGS84, start, end);
				distance += measurement.getPointToPointDistance();
				trackpoint.addAttribute(new DistanceAttribute(distance));
			}

			track.addTrackpoint(trackpoint);
			
			// get ready for next iteration
			lastElevation = trackpoint.getAttribute(AltitudeAttribute.class).getAltitude();
			lastPosition = trackpoint.getPosition();
		}
		activity.addTrack(track);
		return activity;
	}

	private Date parseTime(Element trk) {
		String timeText = trk.getChildTextTrim("time", NS_GPX_1_1);
		if (timeText != null && timeText.length() > 0) {
			return parseTime(timeText);
		}
		return new Date();
	}

	private Date parseTime(String timeText) {
		try {
			// 2012-01-01T15:45:47Z
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(timeText);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private double getElevationForPosition(double latitude, double longitude) {
		if (elevationModel != null && elevationModel.containsPosition(latitude, longitude)) {
			return elevationModel.elevationForPosition(latitude, longitude);
		}
		return 0;
	}

	private Document parseDocument(InputStream inputStream) throws IOException {
		try {
			SAXBuilder builder = new SAXBuilder();
			return builder.build(inputStream);
		} catch (JDOMException e) {
			return null;
		}
	}

}