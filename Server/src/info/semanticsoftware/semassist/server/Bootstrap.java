/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2009, 2010 Semantic Software Lab, http://www.semanticsoftware.info
Nikolaos Papadakis
Tom Gitzinger

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
package info.semanticsoftware.semassist.server;

import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceException;
import info.semanticsoftware.semassist.server.util.MasterData;

/**
 * This class publishes the web service in the specified context
 * @author Tom Gitzinger
 * @author Nikolaos Papadakis
 * */
public class Bootstrap{

    /**
     * The main method that starts the web service
     * @param args main method arguments
     * @throws Exception when a broker cannot be instantiated
     * */
	public static void main( String[] args ) throws Exception{
    	
        // The web service implementation class instance
		SemanticServiceBroker broker = new SemanticServiceBroker();

        try
        {
            Endpoint.publish( MasterData.getWSPublishURL(), broker );
        }
        catch( WebServiceException e )
        {
            e.printStackTrace();
        }
    }

}
