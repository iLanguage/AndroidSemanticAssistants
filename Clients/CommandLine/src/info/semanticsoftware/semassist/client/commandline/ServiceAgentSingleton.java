/*
    Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

    This file is part of the Semantic Assistants architecture.

    Copyright (C) 2009 Semantic Software Lab, http://www.semanticsoftware.info
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
package info.semanticsoftware.semassist.client.commandline;

import info.semanticsoftware.semassist.server.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;

public class ServiceAgentSingleton
{

    private static SemanticServiceBrokerService service = null;
    private static SemanticServiceBroker broker = null;
    private static String serverHost="";
    private static String serverPort="";

    public static SemanticServiceBroker getInstance()
    {
        try
        {
            if( broker == null )
            {
                if( service == null )
                {
                    // NOTE: This redundant check is to maintain behaviour of the
                    // command-line params=(Host=<hostname>,Port=<portnum>) override
                    // option & relies on the short lived lifespan of the command line
                    // process to work. May want to remove this functionality once each
                    // client's ServiceAgentSingletons are consolidated in CSAL.
                    if( serverHost.isEmpty() || serverPort.isEmpty() ) {
                        // Cache the configured server & port members of this singleton.
                        SACLClient.propertiesReader();
                    }

                    if( !serverHost.isEmpty() && !serverPort.isEmpty() )
                    {
                        //System.out.println( "using params");

                        service = new SemanticServiceBrokerService( new URL( createURL() ),
                            new QName( "http://server.semassist.semanticsoftware.info/",
                            "SemanticServiceBrokerService" ) );
                    }
                    else
                    {
                        //System.out.println( "not using params");
                        
                        service = new SemanticServiceBrokerService();
                    }

                }
                broker = service.getSemanticServiceBrokerPort();
            }
        }
        catch( MalformedURLException e )
        {
            e.printStackTrace();
        }
        return broker;
    }

    private static String createURL()
    {
        return "http://" + serverHost + ":" + serverPort + "/SemAssist?wsdl";
    }

    public static void setServerHost(String value)
    {
       serverHost = value;
    }

    public static void setServerPort(String value )
    {
        serverPort = value;
    }

}

