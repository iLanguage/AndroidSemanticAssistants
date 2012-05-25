/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2012, 2013 Semantic Software Lab, http://www.semanticsoftware.info
Rene Witte
Bahar Sateli

The Semantic Assistants architecture is free software: you can
redistribute and/or modify it under the terms of the GNU Affero General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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