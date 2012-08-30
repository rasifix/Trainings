package com.github.rasifix.lazycouch;

public interface LazyCouchFactory {
	
	CouchServer open(String server, int port);
	
}
