package info.semanticsoftware.semassist.server.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

/**
 * Starts the Restlet as a HTTPS secure standalone application.
 * @author Bahar Sateli
 * */
public class SecureBootstrap {

	/** Main method.
	 * @param args runtime arguments */
	public static void main(String[] args) {
		try {
			// Create a new Component.
			Component component = new Component();
			Server server = component.getServers().add(Protocol.HTTPS, 8183);

			/* I had to change the certificate exchange behavior in the keystore for when the .jks file
			 is inside a .war file. In this situation, no *file* can be retrieved from 
			 within a war file. Therefore, we get an inputstream from the keystore and 
			 recreate it in a temporary file.
			*/
			ClassLoader classLoader = SecureBootstrap.class.getClassLoader();
			InputStream stream = classLoader.getResourceAsStream("info/semanticsoftware/semassist/server/rest/keystore.jks");
			File tempKS = File.createTempFile("tempKeystore", "jks");
			tempKS.deleteOnExit();
			FileOutputStream fileoutputstream = new FileOutputStream(tempKS);
			for (int read = stream.read(); read >= 0; read = stream.read()){
				fileoutputstream.write(read);
			}
			fileoutputstream.flush();
			fileoutputstream.close();

			/* The two following lines only work when we're executing from console or Eclipse */
			//URL url = classLoader.getResource("info/semanticsoftware/semassist/server/rest/keystore.jks");
			//server.getContext().getParameters().add("keystorePath", url.getPath());
			server.getContext().getParameters().add("keystorePath", tempKS.getAbsolutePath());
			server.getContext().getParameters().add("keystorePassword", "SemAssist");
			server.getContext().getParameters().add("keyPassword", "SemAssist");

			// Attach the SA application.
			component.getDefaultHost().attach(new SemAssist());
			// Start the component.
			component.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}