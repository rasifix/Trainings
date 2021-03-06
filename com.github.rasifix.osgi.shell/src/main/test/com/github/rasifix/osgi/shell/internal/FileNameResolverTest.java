package com.github.rasifix.osgi.shell.internal;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.github.rasifix.osgi.shell.internal.FileNameResolver;

public class FileNameResolverTest {
	
	@Test
	public void testResolveHomeFolder() throws Exception {
		FileNameResolver resolver = new FileNameResolver(new File("."));
		List<File> files = resolver.resolveFiles("~");
		System.out.println(files);
	}
	
	@Test
	public void testResolveWildcard() throws Exception {
		FileNameResolver resolver = new FileNameResolver(new File("."));
		List<File> files = resolver.resolveFiles("~/Dropbox/garmin/2012/02/*.FIT");
		System.out.println(files);
	}
	
}
