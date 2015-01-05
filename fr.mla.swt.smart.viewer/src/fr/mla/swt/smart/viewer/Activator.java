package fr.mla.swt.smart.viewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.mla.swt.perfect.viewer"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private static ImageRegistry imageRegistry;

	/**
	 * The constructor
	 */
	public Activator() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		if (imageRegistry == null) {
			imageRegistry.dispose();
		}
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static Image getImage(String fileName) {
		if (imageRegistry == null) {
			imageRegistry = new ImageRegistry();
		}
		Image image = imageRegistry.get(fileName);
		if (image == null) {
			URL url = Activator.class.getResource("/" + fileName);
			if (url != null) {
				try (InputStream in = url.openStream()) {
					image = new Image(Display.getCurrent(), in);
					imageRegistry.put(fileName, image);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return image;
	}

}
