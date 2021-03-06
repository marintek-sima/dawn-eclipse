package org.eclipse.dawnsci.analysis.examples;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {
	
	public static final String PLUGIN_ID = "org.eclipse.dawnsci.analysis.examples";

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

	public static Object getService(Class<?> clazz) {
		if (context==null) return null;
		ServiceReference<?> ref = context.getServiceReference(clazz);
		if (ref==null) return null;
		return context.getService(ref);
	}
	
	
	/**
	 * @param bundleName
	 * @return file this can return null if bundle is not found
	 * @throws IOException
	 */
	public static File getBundleLocation(final String bundleName) throws IOException {
		final Bundle bundle = find(bundleName);
		if (bundle == null) {
			return null;
		}
		
		String loc = bundle.getLocation();
		if (loc==null) return null;
		
		if (loc.startsWith("reference:file:"))    loc = loc.substring("reference:file:".length());
		if (isWindowsOS() && loc.startsWith("/")) loc = loc.substring(1);
		
		return new File(loc);
	}
	
	static public boolean isWindowsOS() {
		return (System.getProperty("os.name").indexOf("Windows") == 0);
	}

	
	/**
	 * Find the first bundle with a given name.
	 * @param bundleName
	 * @return
	 */
	private static Bundle find(String bundleName) {
		for (Bundle bundle : context.getBundles()) {
			if (bundle.getSymbolicName().equals(bundleName)) return bundle;
 		}
		return null;
	}

	/**
	 * Get the bundle path using eclipse.home.location not loading the bundle.
	 * @param bundleName
	 * @return
	 */
	public static File getBundlePathNoLoading(String bundleName) {
		return new File(new File(getEclipseHome(), "plugins"), bundleName);
	}
	
	/**
	 * Gets eclipse home in debug and in deployed application mode.
	 * @return
	 */
	public static String getEclipseHome() {
		File hDirectory;
		try {
			URI u = new URI(System.getProperty("eclipse.home.location"));
			hDirectory = new File(u);
		} catch (URISyntaxException e) {
			return null;
		}

		String path = hDirectory.getName();
		if (path.equals("plugins") || path.equals("bundles")) {
			path = hDirectory.getParentFile().getParentFile().getAbsolutePath();
		} else{
			path = hDirectory.getAbsolutePath();
		}
        return path;
	}

}

