package info.semanticsoftware.semassist.client.eclipse;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle. 
 * This is the main class of this plug-in that will be loaded initially.
 * It extends the AbstractUIPlugin, which tells the Eclipse Run-time that this plug-in is related to the Eclipse Platform UI.
 * 
 * @author Bahar Sateli
 */
public class Activator extends AbstractUIPlugin {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "info.semanticsoftware.semassist.client.eclipse";

	/** The shared instance of the plug-in */
	private static Activator plugin;
	
	/** This variable stores the full path to the plug-in's properties folder in Eclipse workspace .metadata directory */
	public static String metadata;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/**
	 * This method starts the plug-in.
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		System.out.println("Semantic Assistants Plug-in Started Successfully.");
		metadata = getStateLocation().toString();
	}

	/**
	 * This method stops the plug-in.
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("Semantic Assistants Plug-in Stopped Successfully.");
		plugin = null;
		super.stop(context);
	}

	/**
	 * This method is called by the Eclipse run-time to return the plug-in's shared instance
	 *
	 * @return the plug-in's shared plug-in instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
