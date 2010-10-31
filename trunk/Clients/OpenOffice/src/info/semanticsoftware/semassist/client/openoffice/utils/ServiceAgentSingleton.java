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
package info.semanticsoftware.semassist.client.openoffice.utils;

import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.SemanticServiceBrokerService;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JOptionPane;
import javax.xml.namespace.QName;

public class ServiceAgentSingleton
{

    private static SemanticServiceBrokerService service = null;
    private static SemanticServiceBroker agent = null;

    public static synchronized SemanticServiceBroker getInstance()
    {
        try
        {
            if( agent == null || UNOUtils.getServerInfoChanged() )
            {
                if( service == null || UNOUtils.getServerInfoChanged() )
                {

                    service = new SemanticServiceBrokerService( new URL( createURL() ),
                            new QName( "http://server.semassist.semanticsoftware.info/",
                            "SemanticServiceBrokerService" ) );

                    UNOUtils.setServerInfoChanged( false );
                }
                agent = service.getSemanticServiceBrokerPort();
            }

        }
        catch( MalformedURLException e )
        {
            //e.printStackTrace();
            JOptionPane.showMessageDialog( null,"Server not found. \nPlease check the Server Host and Port",
                    "MalformedURL", JOptionPane.ERROR_MESSAGE );
        }
        catch(  java.lang.NoClassDefFoundError we)
        {
             JOptionPane.showMessageDialog( null,"Please Add the CSAL.jar to your openOffice Java ClassPath",
                    "Connection Refused", JOptionPane.ERROR_MESSAGE );

             return null;
        }
       

        return agent;
    }

    private static String createURL()
    {
        return "http://" + UNOUtils.getServerHost() + ":" + UNOUtils.getServerPort() + "/SemAssist?wsdl";
    }

}



