package com.github.rasifix.solv.service.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
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
import org.cyberneko.html.parsers.SAXParser;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXHandler;
import org.osgi.service.component.ComponentContext;
import org.xml.sax.InputSource;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;

import com.github.rasifix.solv.Event;
import com.github.rasifix.solv.EventOverview;
import com.github.rasifix.solv.ResultService;
import com.github.rasifix.solv.service.internal.ResultListParser.ResultListEntry;
import com.github.rasifix.solv.service.internal.StartListParser.StartListEntry;

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
	public List<EventOverview> listEvents(final int year) throws IOException {
		HttpClient client = new DefaultHttpClient();
		String query = query(
			nameValuePair("year", year),
			nameValuePair("event", "Auswahl")
		);
		HttpGet request = new HttpGet(uri(serviceUrl, query));
		return client.execute(request, new ResponseHandler<List<EventOverview>>() {
			@Override
			public List<EventOverview> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					SAXParser parser = new SAXParser();
					SAXHandler handler = new SAXHandler();
					parser.setContentHandler(handler);
					try {
						parser.parse(new InputSource(entity.getContent()));
						Document doc = handler.getDocument();
						Element root = doc.getRootElement();
						Element body = root.getChild("BODY");
						Element pre = findPre(body);
						List<Element> inputs = (List<Element>) pre.getChildren("INPUT");
						
						List<EventOverview> result = new LinkedList<EventOverview>();
						for (Element input : inputs) {
							if (input.getAttributeValue("type").equals("radio") && input.getAttributeValue("name").equals("event")) {
								String name = input.getAttributeValue("value");
								if (!"Auswahl".equals(name)) {
									result.add(new EventOverview(year, name));
								}
							}			
						}
						return result;
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				return null;
			}
		});
	}
	
	@Override
	public Event queryEvent(int year, String title) throws IOException {
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

	protected static Element findPre(Element current) {
		if ("PRE".equals(current.getName())) {
			return current;
		}
		
		@SuppressWarnings("unchecked")
		List<Element> children = (List<Element>) current.getChildren();
		for (Element child : children) {
			Element findPre = findPre(child);
			if (findPre != null) {
				return findPre;
			}
		}
		return null;
	}

}
