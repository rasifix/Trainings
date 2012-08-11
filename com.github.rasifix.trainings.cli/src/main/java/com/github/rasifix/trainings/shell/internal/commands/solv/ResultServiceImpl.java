package com.github.rasifix.trainings.shell.internal.commands.solv;

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

@Component(properties={ "url=http://www.o-l.ch/cgi-bin/results" })
public class ResultServiceImpl implements ResultService {

	private String serviceUrl;

	@Activate
	public void activate(ComponentContext context) throws MalformedURLException {
		this.serviceUrl = (String) context.getProperties().get("url");
	}
	
	@Override
	public List<Event> listEvents(final int year) throws IOException {
		HttpClient client = new DefaultHttpClient();
		String query = query(
			nameValuePair("year", year),
			nameValuePair("event", "Auswahl")
		);
		HttpGet request = new HttpGet(uri(serviceUrl, query));
		return client.execute(request, new ResponseHandler<List<Event>>() {
			@Override
			public List<Event> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
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
						
						List<Event> result = new LinkedList<Event>();
						for (Element input : inputs) {
							if (input.getAttributeValue("type").equals("radio") && input.getAttributeValue("name").equals("event")) {
								String name = input.getAttributeValue("value");
								if (!"Auswahl".equals(name)) {
									result.add(new Event(year, name));
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
	public List<Category> listCategories(final Event event) throws IOException {
		HttpClient client = new DefaultHttpClient();
		String query = URLEncodedUtils.format(
				Arrays.asList(
						new BasicNameValuePair("year", Integer.toString(event.getYear())), 
						new BasicNameValuePair("event", event.getName())
				), "UTF-8"
		);
		URI uri = uri(serviceUrl, query);
		HttpGet request = new HttpGet(uri);
		return client.execute(request, new ResponseHandler<List<Category>>() {
			@Override
			public List<Category> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
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
						List<Element> inputs = (List<Element>) pre.getChildren("A");
						
						List<Category> result = new LinkedList<Category>();
						for (Element input : inputs) {
							String cat = input.getTextTrim();
							if (!"alle".equals(cat)) {
								result.add(new Category(event, cat));
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
	public List<Runner> listRunners(Category category) throws IOException {
		HttpClient client = new DefaultHttpClient();
		String query = query(
			nameValuePair("year", "2012"), 
			nameValuePair("event", category.getEvent().getName()),
			nameValuePair("kat", category.getName()),
			nameValuePair("zwizt", "1"),
			nameValuePair("csv", "1")
		);
		HttpGet request = new HttpGet(uri(serviceUrl, query));
		return client.execute(request, new ResponseHandler<List<Runner>>() {
			@Override
			public List<Runner> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String content = EntityUtils.toString(entity);
					return parseRunners(new BufferedReader(new StringReader(content)));
				}
				return null;
			}
		});
	}

	protected static String parseField(String line, int start, int end) {
		if (line.length() < start) {
			return null;
		} else if (line.length() < end) {
			end = line.length();
		}
		return line.substring(start, end).trim();
	}

	protected static String decode(String line) {
		line = line.replaceAll("&ouml;", "š");
		line = line.replaceAll("&auml;", "Š");
		line = line.replaceAll("&uuml;", "Ÿ");
		return line;
	}
	
	protected static List<Runner> parseRunners(BufferedReader reader) throws IOException {
		List<Runner> result = new LinkedList<Runner>();
		
		// first line is header - we don't care as it is obviously wrong...
		reader.readLine();
		
		String line = reader.readLine();
		while (line != null) {
			// unescape html escape sequences (don't ask why they are in a CSV file...)
			line = decode(line);

			// ignore empty line at end
			if (line.trim().length() == 0) {
				break;
			}
			
			// poor mans CSV parser (seems to work ok)
			String[] split = line.split(";");
			
			// get the name of the poor ole runner
			String name = decode(split[1]);
			Runner runner = new Runner(name);
			result.add(runner);
			
			// absolute times (don't care, can be reconstructed)
			line = reader.readLine();

			while (line != null && line.length() > 0 && line.charAt(0) == ' ') {
				// split times
				line = reader.readLine();

				// parse split times
				String field = parseField(line, 6, 13);
				if (field != null) {
					runner.addSplit(field);
				}
				field = parseField(line, 21, 28);
				if (field != null) {
					runner.addSplit(field);
				}
				field = parseField(line, 36, 43);
				if (field != null) {
					runner.addSplit(field);
				}
				field = parseField(line, 51, 58);
				if (field != null) {
					runner.addSplit(field);
				}
				field = parseField(line, 66, 73);
				if (field != null) {
					runner.addSplit(field);
				}

				// split lost time
				line = reader.readLine();

				// next line: absolute times or next runner
				line = reader.readLine();
			}
		}
		
		return result;
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
