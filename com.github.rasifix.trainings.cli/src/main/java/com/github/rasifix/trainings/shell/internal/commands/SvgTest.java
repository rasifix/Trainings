package com.github.rasifix.trainings.shell.internal.commands;

import java.util.ListIterator;

import org.osgi.service.component.annotations.Component;

import com.github.rasifix.osgi.shell.Command;
import com.github.rasifix.osgi.shell.CommandContext;
import com.github.rasifix.trainings.model.Activity;
import com.github.rasifix.trainings.model.Position;
import com.github.rasifix.trainings.model.Track;
import com.github.rasifix.trainings.model.Trackpoint;
import com.github.rasifix.trainings.model.TrackpointSequence;
import com.github.rasifix.trainings.model.attr.PositionAttribute;

import jline.Completor;
import jline.NullCompletor;

@Component
public class SvgTest implements Command {

	@Override
	public Object execute(CommandContext context) throws Exception {
		Activity activity = (Activity) context.getCurrent();
		
		double minx = Double.MAX_VALUE;
		double maxx = Double.MIN_VALUE;
		double miny = Double.MAX_VALUE;
		double maxy = Double.MIN_VALUE;
		
		for (Track track : activity.getTracks()) {
			for (Trackpoint trackpoint : track.getTrackpoints().select(PositionAttribute.class)) {
				Position pos = trackpoint.getPosition();
				minx = Math.min(minx, pos.getLongitude());
				maxx = Math.max(maxx, pos.getLongitude());
				miny = Math.min(miny, pos.getLatitude());
				maxy = Math.max(maxy, pos.getLatitude());
			}
		}

		double width = maxx - minx;
		double height = maxy - miny;
		double widthFactor = 1, heightFactor = 1;
		double border;
		
		if (width > height) {
			heightFactor = height / width; 
			border = width * 0.025;
		} else {
			widthFactor = width / height;
			border = height * 0.025;
		}
		
		width += 2 * border;
		height += 2 * border;
		minx -= border;
		miny -= border;
		maxx += border;
		maxy += border;
		
		double canvasWidth = widthFactor * 600;
		double canvasHeight = heightFactor * 600;
		
		StringBuilder b = new StringBuilder();
		b.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		b.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
		b.append("<svg xmlns=\"http://www.w3.org/2000/svg\"\n");
		b.append("     xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:ev=\"http://www.w3.org/2001/xml-events\"\n");
		b.append("     version=\"1.1\" baseProfile=\"full\"\n");
		b.append("     width=\"").append(canvasWidth);
		b.append("\" height=\"").append(canvasHeight);
		b.append("\" viewBox=\"");
		b.append(minx).append(" ").append(miny).append(" ").append(width).append(" ").append(height);
		b.append("\">\n");
		b.append("<!-- ").append(minx).append(" ").append(maxx).append(" ").append(" ").append(miny).append(" ").append(maxy).append("-->\n");
		b.append("<g transform=\"translate(0,").append(maxy).append(") scale(1,-1) translate(0,-").append(miny).append(")\">\n");
		
		for (Track track : activity.getTracks()) {
			b.append("  <path stroke-width=\"0.0001\" fill=\"none\" stroke=\"black\" d=\"");
			TrackpointSequence trackpoints = track.getTrackpoints().select(PositionAttribute.class);
			Position first = trackpoints.getFirst().getPosition();
			b.append("M").append(first.getLongitude()).append(",").append(first.getLatitude());
			b.append(" ");
			
			ListIterator<Trackpoint> it = trackpoints.listIterator(1);
			while (it.hasNext()) {
				Position pos = it.next().getPosition();
				b.append("L").append(pos.getLongitude()).append(",").append(pos.getLatitude());
				b.append(" ");
			}
			
			b.append("\" />\n");
		}
		
		b.append("</g>\n");
		b.append("</svg>\n");
		
		System.out.println(b);
		
		return context.getCurrent();
	}

	@Override
	public String getName() {
		return "svg";
	}
	
	@Override
	public String getUsage() {
		return "svg";
	}

	@Override
	public Completor getCompletor() {
		return new NullCompletor();
	}

}
