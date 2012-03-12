package com.github.rasifix.trainings.ui.client;

import com.github.rasifix.trainings.ui.client.activities.ActivityOverview;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;

public class ViewRowCell extends AbstractCell<ViewRow<ActivityOverview>> {

	interface Templates extends SafeHtmlTemplates {
		// 
		// <b>Run Around Lake Tahoo</b>
	    // <div><span style="text-baseline: center; color:#888; font-size:10px"> 2012-01-01 15:28</span></div>
	    // <div style="font-size:11px; line-height:16px; vertical-align:middle">
		//    <img src="stopwatch.png" width="16" height="16" />00:45:13
		//    <img src="distance.png" width="16" height="16" />10.3km
		//    <img src="heart.png" />153
		//  </div>

		@SafeHtmlTemplates.Template("<table style=\"width:100%\"><tr>"
				+ "<td style=\"width:48px\"><img src=\"{0}\" width=\"48\" height=\"48\"/></td>"
				+ "<td>"
				+ "<b>{1}</b><div>"
				+ "<span style=\"text-baseline:center; color:#888;font-size:10px\"> {2}</span></div>"
				+ "<div style=\"font-size:11px; line-height:16px; vertical-align:middle\">"
				+ "<img src=\"{3}\" width=\"16\" height=\"16\"/> {4}&nbsp;"
				+ "<img src=\"{5}\" width=\"16\" height=\"16\"/> {6}&nbsp;"
				+ "<img src=\"{7}\" width=\"16\" height=\"16\"/> {8}&nbsp;"
				+ "<img src=\"{9}\" width=\"16\" height=\"16\"/> {10}</div>"
				+ "</td>" 
				+ "</tr></table>")
		SafeHtml cell(SafeUri imageUrl, SafeHtml title, SafeHtml date, 
				SafeUri watchUrl, String time, 
				SafeUri distanceUrl, String distance,
				SafeUri speedUrl, String speed,
				SafeUri heartUrl, String hr);
	}
	
	interface Images extends ClientBundle {
		
		@Source("running.png")
		ImageResource running();
		
		@Source("orienteering.png")
		ImageResource orienteering();
		
		@Source("cycling.png")
		ImageResource cycling();
		
		@Source("mtb.png")
		ImageResource mtb();
		
		@Source("heart.png")
		ImageResource heartRate();
		
		@Source("distance.png")
		ImageResource distance();
		
		@Source("speed.png")
		ImageResource speed();
		
		@Source("stopwatch.png")
		ImageResource stopWatch();
		
	}

	private static Templates templates = GWT.create(Templates.class);
	private static Images images = GWT.create(Images.class);
	
	@Override
	public void render(Context context, ViewRow<ActivityOverview> value, SafeHtmlBuilder sb) {
		if (value == null) {
			return;
		}
		
		ActivityOverview overview = value.getValue();
		
		SafeHtml safeValue = SafeHtmlUtils.fromString(value.getKey());
		
		ImageResource image = null;
		if ("RUNNING".equals(overview.getSport())) {
			image = images.running();
		} else if ("CYCLING".equals(overview.getSport())) {
			image = images.cycling();
		} else if ("MTB".equals(overview.getSport())) {
			image = images.mtb();
		} else if ("ORIENTEERING".equals(overview.getSport())) {
			image = images.orienteering();
		}
		
		String title = overview.getFirstPlace();
		if (!overview.getFirstPlace().equals(overview.getLastPlace())) {
			title += "-" + overview.getLastPlace();
		}
		
		SafeHtml rendered = templates.cell(
				image.getSafeUri(), SafeHtmlUtils.fromString(title), safeValue,
				images.stopWatch().getSafeUri(), formatTime(overview.getTime()),
				images.distance().getSafeUri(), formatDistance(overview.getDistance()),
				images.speed().getSafeUri(), formatSpeed(overview),
				images.heartRate().getSafeUri(), "" + overview.getHr());
		sb.append(rendered);
	}

	private String formatSpeed(ActivityOverview overview) {
		if (overview.getSport().equals("RUNNING") || overview.getSport().equals("ORIENTEERING")) {
			//   1 / overview.getSpeed()   ==> s / m  
			//  60 / overview.getSpeed() / 1000
			double pace = (100.0 / overview.getSpeed() / 6);
			int minutes = (int) pace;
			int seconds = (int) ((pace - minutes) * 60);
			return pad(minutes) + ":" + pad(seconds) + " min/km";
		}
		
		String speed = "" + Math.round(overview.getSpeed() * 3.6 * 10);
		return speed.substring(0, speed.length() - 1) + "." + speed.substring(speed.length() - 1) + "km/h";
	}

	private String formatTime(int time) {
		int hours = time / 3600;
		int minutes = (time - hours * 60) / 60;
		int seconds = time % 60;
		return pad(hours) + ":" + pad(minutes) + ":" + pad(seconds);
	}
	
	private String pad(int value) {
		if (value < 10) {
			return "0" + value;
		}
		return "" + value;
	}

	private String formatDistance(int distance) {
		int km = distance / 1000;
		int dkm = (distance / 100) % 10;
		return km + "." + dkm + "km";
	}

}
