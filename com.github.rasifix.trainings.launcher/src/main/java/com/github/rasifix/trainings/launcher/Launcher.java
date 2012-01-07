package com.github.rasifix.trainings.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import com.github.rasifix.trainings.application.Application;

public class Launcher {
	
	private static final String PROP_APPID = "appid";
	
    public static void main(final String[] args) throws Exception {
    	if (args.length == 0) {
    		System.err.println("usage: trainings <appid> <args>");
    		System.exit(-1);
    	}
    	
    	final String appid = args[0];
    	
        try {
        	final Framework framework = launchFramework();
            final BundleContext bundleContext = framework.getBundleContext();
            
            installBundles(bundleContext);            
            
            Application application = getApplicationService(bundleContext, appid);
            application.start(args);
            
            stopFramework(framework);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

	private static Framework launchFramework() throws Exception {
		// export com.github.rasifix.trainings.application from system bundle
		// we need to access the package from both the launcher and the bundles
		final Map<String, String> config = Collections.singletonMap(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "com.github.rasifix.trainings.application; version=1.0.0");
		
		Framework framework = getFrameworkFactory().newFramework(config);
		framework.init();
		framework.start();
		
		return framework;
	}

	private static void installBundles(BundleContext bundleContext) throws BundleException {
		File[] files = new File("/Users/sir/.trainings/bundles/").listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".jar");
			}
		});
		
		if (files != null) {
			System.out.println("... installing " + files.length + " bundles");
			for (File file : files) {
				System.out.println("    " + file.getName());
				bundleContext.installBundle(file.toURI().toString());
			}
		}
		
		for (Bundle bundle : bundleContext.getBundles()) {
			if (bundle.getState() != Bundle.ACTIVE) {
		    	System.out.println("... starting " + bundle.getSymbolicName());
		    	bundle.start();
			}
		}
		
        waitForBundles(bundleContext);
	}

	private static void waitForBundles(final BundleContext bundleContext) {
		System.out.println("... waiting for bundles to start");
		waitForBundleStarted(bundleContext);
		System.out.println("... all bundles started");
	}

	private static Application getApplicationService(final BundleContext bundleContext, final String appid) throws InvalidSyntaxException {
		Collection<ServiceReference<Application>> applications = bundleContext.getServiceReferences(Application.class, "(" + PROP_APPID + "=" + appid + ")");
		if (applications.isEmpty()) {
			throw new IllegalArgumentException("no application found with id " + appid);
		}
		
		// get the application service and start it
		Application application = bundleContext.getService(applications.iterator().next());
		if (application == null) {
			// probably only theoretically possible
			throw new IllegalArgumentException("application with id " + appid + " disappearted, try again");
		}
		return application;
	}

	private static void stopFramework(Framework framework) throws BundleException, InterruptedException {
		framework.stop();
		framework.waitForStop(0);
		System.exit(0);
	}

    private static void waitForBundleStarted(BundleContext bundleContext) {
    	long start = System.currentTimeMillis();
    	
    	Set<Bundle> bundles = new HashSet<Bundle>(Arrays.asList(bundleContext.getBundles()));
    	while (!bundles.isEmpty() && System.currentTimeMillis() - start < 5000) {
    		Iterator<Bundle> bundlesIt = bundles.iterator();
    		while (bundlesIt.hasNext()) {
    			Bundle next = bundlesIt.next();
    			if (next.getState() == Bundle.ACTIVE) {
    				bundlesIt.remove();
    			}
    		}
    	}
    	
    	if (!bundles.isEmpty()) {
    		throw new IllegalStateException("not all bundles have been started");
    	}
	}

	private static FrameworkFactory getFrameworkFactory() throws Exception {
        URL url = Launcher.class.getClassLoader().getResource("META-INF/services/org.osgi.framework.launch.FrameworkFactory");
        if (url != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            try {
                for (String s = br.readLine(); s != null; s = br.readLine()) {
                    s = s.trim();
                    // Try to load first non-empty, non-commented line.
                    if ((s.length() > 0) && (s.charAt(0) != '#')) {
                        return (FrameworkFactory) Class.forName(s).newInstance();
                    }
                }
            } finally {
                if (br != null) br.close();
            }
        }

        throw new Exception("Could not find framework factory.");
    }

}
