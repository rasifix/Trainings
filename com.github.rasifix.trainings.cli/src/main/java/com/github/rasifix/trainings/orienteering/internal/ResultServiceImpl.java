package com.github.rasifix.trainings.orienteering.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;

import com.github.rasifix.trainings.orienteering.Event;
import com.github.rasifix.trainings.orienteering.ResultService;
import com.github.rasifix.trainings.orienteering.internal.ResultListParser.ResultListEntry;
import com.github.rasifix.trainings.orienteering.internal.StartListParser.StartListEntry;

@Component(properties={ "url=http://www.o-l.ch/cgi-bin/results" })
public class ResultServiceImpl implements ResultService {

	private String serviceUrl;
	
	void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	@Activate
	public void activate(ComponentContext context) throws MalformedURLException {
		this.serviceUrl = (String) context.getProperties().get("url");
	}

	@Override
	public Event getEvent(int year, String title) throws IOException {
		List<StartListEntry> startList = parseStartList(year, title);
		List<ResultListEntry> resultList = parseResultList(year, title);
		
		return createEvent(year, title, startList, resultList);
	}

	private Event createEvent(int year, String title, List<StartListEntry> startList, List<ResultListEntry> resultList) {
		EventCreator creator = new EventCreator();
		return creator.createEvent(year, title, startList, resultList);
	}

	private List<StartListEntry> parseStartList(int year, String title) throws IOException {
		HttpClient client = new DefaultHttpClient();
		String query = query(
			nameValuePair("year", Integer.toString(year)), 
			nameValuePair("event", title),
			nameValuePair("type", "start"),
			nameValuePair("kind", "all")
		);
		HttpGet request = new HttpGet(uri(serviceUrl, query));
		return client.execute(request, new ResponseHandler<List<StartListEntry>>() {
			@Override
			public List<StartListEntry> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				StartListParser parser = new StartListParser();
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					return parser.parse(new BufferedReader(new StringReader(EntityUtils.toString(entity))));
				}
				throw new IllegalStateException("invalid response");
			}
		});
	}
	
	private List<ResultListEntry> parseResultList(int year, String title) throws IOException {
		HttpClient client = new DefaultHttpClient();
		String query = query(
			nameValuePair("year", Integer.toString(year)), 
			nameValuePair("event", title),
			nameValuePair("type", "rang"),
			nameValuePair("kind", "all"),
			nameValuePair("zwizt", "1"),
			nameValuePair("csv", "1")
		);
		HttpGet request = new HttpGet(uri(serviceUrl, query));
		return client.execute(request, new ResponseHandler<List<ResultListEntry>>() {
			@Override
			public List<ResultListEntry> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				ResultListParser parser = new ResultListParser();
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					return parser.parse(new BufferedReader(new StringReader(EntityUtils.toString(entity))));
				}
				throw new IllegalStateException("invalid response");
			}
		});
	}
	
	protected static URI uri(String serviceUrl, String query) {
		try {
			return new URI(serviceUrl + "?" + query);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected static String query(NameValuePair... pairs) {
		return URLEncodedUtils.format(Arrays.asList(pairs), "UTF-8");
	}
	
	protected static NameValuePair nameValuePair(String name, Object value) {
		return new BasicNameValuePair(name, value.toString());
	}

}
