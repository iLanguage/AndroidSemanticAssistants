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

import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * Starts the Restlet as a standalone application.
 * @author Bahar Sateli
 * */
public class Bootstrap {

	/** Main method.
	 * @param args runtime arguments. args[0] is a port number. */
	public static void main(String[] args) {
		try {
			Component component = new Component();
			if(args.length > 0){
				//Add a new HTTP server listening on the user provided port.
				component.getServers().add(Protocol.HTTP, Integer.parseInt(args[0]));
			}else{
				//Add a new HTTP server listening on the default port.
				component.getServers().add(Protocol.HTTP, 8182);
			}
			// Attach the SA application.
			component.getDefaultHost().attach(new SemAssist());
			// Start the component.
			component.start();
		}catch (NumberFormatException e){
			e.printStackTrace();
			System.err.println("Port number format exception: " + args[0]);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
