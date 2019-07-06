package com.github.rasifix.lazycouch.internal;

import org.osgi.service.component.annotations.Component;

import com.github.rasifix.lazycouch.CouchServer;
import com.github.rasifix.lazycouch.LazyCouchFactory;


@Component
public class LazyCouchFactoryImpl implements LazyCouchFactory {

	@Override
	public CouchServer open(String server, int port) {
		return new CouchServerImpl(server, port);
	}

}
