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


/**
 * A simple command-line client for the semantic
 * assistants (SA) architecture.
 */
package info.semanticsoftware.semassist.client.commandline;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.net.*;

import net.java.dev.jaxb.array.*;
import info.semanticsoftware.semassist.server.*;
import info.semanticsoftware.semassist.csal.*;

public class SACLClient
{
    // Client-unique key to differentiate its preferences from
    // other clients.
    private static final String CLIENT_NAME = "cmdline";

    public static void main( String args[] )
    {

        if( args.length == 0 )
        {
            printHelp();
            System.exit( 0 );
        }

        final String cmd = args[0];
        final String[] params = (args.length > 1) ?
            Arrays.copyOfRange(args, 1, args.length) : new String[0];

        executeCommand( cmd, params );
        System.exit( 0 );
    }

    private static void executeCommand(final String cmd, final String[] params )
    {
        if( cmd.equals( "h" ) || cmd.equals( "help" ) || cmd.equals( "usage" ) )
        {
            printHelp();
        }
        else if( cmd.equals( "listall" ) )
        {

            if(params.length > 0)
            {
                paramsOtherServer( params );
            }

            p( "Retrieving service info from server..." );
            SemanticServiceBroker agent = ServiceAgentSingleton.getInstance();
            ServiceInfoForClientArray sia = agent.getAvailableServices();
            pl( "   done" );
            pl( "Listing services:" );
            List<ServiceInfoForClient> results = sia.getItem();
            Iterator<ServiceInfoForClient> it = results.iterator();

            while( it.hasNext() )
            {
                ServiceInfoForClient info = it.next();
                pl( info.getServiceName() );
            }

        }
        else if( "listpref".equals(cmd) )
        {
            // Retrieve & display all global preferences.
            pl("\n"+ ClientUtils.XML_CLIENT_GLOBAL +" preferences:");
            printPreferences(ClientUtils.getClientPreference(
               ClientUtils.XML_CLIENT_GLOBAL, null));

            // Retireve & display all command-line preferences.
            pl("\n"+ CLIENT_NAME +" preferences:");
            printPreferences(ClientUtils.getClientPreference(
               CLIENT_NAME, null));
        }
        else if( "setpref".equals(cmd) )
        {
            if (params.length == 0) {
               pl("Missing parameters.\n");
               printHelp();
               return;
            }

            // Check for accepted preference scopes.
            final String scope = params[0];
            if (!ClientUtils.XML_CLIENT_GLOBAL.equals(scope) &&
                !CLIENT_NAME.equals(scope)) {
               pl("Unrecognized <"+ scope +"> scope.\n");
               printHelp();
               return;
            }

            try {
               // Parse list of preferences, attributes & values to set.
               final String[] vars = params[1].split(",");

               for (int i = 0; i < vars.length; ++i) {
                  final String pair = vars[i].split("=")[0];
                  final String pref = pair.split("\\.")[0];
                  final String var = pair.split("\\.")[1];
                  final String val = vars[i].split("=")[1];
                  final Map<String, String> map = new HashMap<String, String>();
                  map.put(var, val);
                  ClientUtils.setClientPreference(scope, pref, map);
               }
            } catch (final Exception ex) {
               // Syntax error parsing pref.attr=val,...
               pl("Parameter parsing error.\n");
               printHelp();
            }
        }
        else if( cmd.equals( "recommend" ) )
        {
            UserContext ctx = buildUserContext( params );

            
            try
            {
            	
                SemanticServiceBroker agent = ServiceAgentSingleton.getInstance();
                ServiceInfoForClientArray sia = agent.recommendServices( ctx );
                pl( "   done" );
                pl( "Listing services:" );

                List<ServiceInfoForClient> results = sia.getItem();
                Iterator<ServiceInfoForClient> it = results.iterator();

                while( it.hasNext() )
                {
                    ServiceInfoForClient info = it.next();
                    pl( info.getServiceName() );
                }
            }
            catch( javax.xml.ws.WebServiceException e )
            {
                pl( "\nConnection error. Possible reasons: Exception on server or server not running." );
            }

        }
        else if( cmd.equals( "invoke" ) )
        {
            try
            {
                if( params.length == 0 )
                {
                    printHelp();
                    return;
                }

                // Extract user context from the parameters
                UserContext ctx = buildUserContext( params );

                final String serviceName = getServiceName( params[0] );
                pl( "Invoking " + serviceName + "..." );
                invokeWithParams( params , serviceName, ctx);

            }
            catch( javax.xml.ws.WebServiceException e )
            {
                pl( "\nConnection error. Possible reasons: Exception on server or server not running." );
            }


        }
        else if( cmd.equals("status")){
            
            try
            {
            	p( "Retrieving status info from server..." );
                SemanticServiceBroker agent = ServiceAgentSingleton.getInstance();
                String rs = agent.getRunningServices();
                String qs = agent.getThreadPoolQueueStatus();
                pl( "   done" );
                String[] runningservices = rs.split(Pattern.quote(";"));
                String[] queuedservices = qs.split(Pattern.quote(";"));
                pl("Loaded Services and their status:");
                for(int idx=0;idx<runningservices.length;idx++){
                	//pl(runningservices[idx]);
                	String[] rss = runningservices[idx].split(Pattern.quote("|"));
                	pl( "\t" + rss[0] + " - " + rss[1]);
                }
                pl("Queued Services:");
                for(int idx=0;idx<queuedservices.length;idx++){
                	//pl(queuedservices[idx]);
                	String[] qss = queuedservices[idx].split(Pattern.quote("|"));
                	pl( "\t" + qss[0] + " - " + qss[1]);
                }
                
                
            }
            catch( javax.xml.ws.WebServiceException e )
            {
                pl( "\nConnection error. Possible reasons: Exception on server or server not running." );
            }

        }
        else if( cmd.equals( "quit" ) )
        {
        }
        else
        {
            printHelp();
        }
    }

    private static UserContext buildUserContext( final String[] pa )
    {
        UserContext ctx = new UserContext();

        // Parse parameters
        for( int i = 0; i < pa.length; i++ )
        {
            String[] pvPair = pa[i].split( "=" );
            if( pvPair[0].equals( "langs" ) )
            {
                String[] langs = pvPair[1].split( "," );
                for( int j = 0; j < langs.length; j++ )
                {
                    ctx.getMUserLanguages().add( langs[j] );
                }
            }
            else if( pvPair[0].equals( "doclang" ) )
            {
                ctx.setMDocLang( pvPair[1] );
            }
        }

        return ctx;
    }

   public static <T> String arrayToString(final T[] arr, final String separator) {
      final StringBuffer result = new StringBuffer();
      if (arr != null && arr.length > 0) {
         result.append(arr[0].toString());
         for (int i = 1; i < arr.length; ++i) {
            result.append(separator + arr[i].toString());
         }
      }
      return result.toString();
   }

    /**
     * Get the content of a possibly quoted string.
     */
    private static String getServiceName( String params )
    {
        String serviceName = null;
        params = params.trim();

        // Test for service name being enclosed in quotes
        if( params.charAt( 0 ) == '"' )
        {
            int pos = params.indexOf( '"', 1 );
            if( pos < 0 )
            {
                pl( "Error: quotes not closed" );
                return null;
            }

            serviceName = params.substring( 1, pos );
        }
        else
        {
            int pos = params.indexOf( ' ' );
            if( pos < 0 )
            {
                serviceName = params.substring( 0 );
            }
            else
            {
                serviceName = params.substring( 0, pos );
            }
        }

        return serviceName;
    }

    private static void invokeWithParams( final String[] params,
         final String serviceName, final UserContext ctx )
    {
        UriList uriArray = new UriList();
        GateRuntimeParameterArray rtpArray = new GateRuntimeParameterArray();
        StringArray stringArray = new StringArray();
        final String[] split = new String[params.length];
        for( int i = 0; i < split.length; i++ )
        {
            split[i] = params[i].trim();
            int pos = split[i].indexOf( '=' );
            if( pos < 0 )
            {
                continue;
            }
            // Parse parameter-value pairs
            String paramName = split[i].substring( 0, pos );
            if( split[i].length() <= pos + 1 )
            {
                continue;
            }
            String paramValue = split[i].substring( pos + 1 );
            // Handle input document URLs
            if( paramName.equals( "docs" ) )
            {
                String[] urls = paramValue.split( ";" );
                for( int j = 0; j < urls.length; j++ )
                {
                    try
                    {
                        URL u = new URL( urls[j] );
                        uriArray.getUriList().add( urls[j] );
                    }
                    catch( MalformedURLException e )
                    {
                        pl( "Could not parse URL " + urls[j] );
                    }
                }
            }
            else if( paramName.equals( "params" ) )
            {
               parseHostPortValue(paramValue);
            }
        }
        SemanticServiceBroker agent = ServiceAgentSingleton.getInstance();
        String result = agent.invokeService( serviceName, uriArray, stringArray, 0L, rtpArray, ctx );
        pl( "" );
        pl( result );
    }

   /**
    * @param paramValue Comma delimited string with host/port key value pairs.
    */
   private static void parseHostPortValue(final String paramValue) {
      final String[] runParams = paramValue.split( "," );
      if( runParams.length >= 2 ) {
         // runParams[0] -> for Host
         // runParams[1] -> for Port
         if( runParams[0].toLowerCase().contains( "host" ) &&
             runParams[1].toLowerCase().contains( "port" ) ) {
            final String host = runParams[0].split("=")[1];
            final String tmp = runParams[1].split("=")[1];
            final String port = tmp.substring(0, tmp.length() - 1);

            // Force server connection ignoring any configured connection preferences.
            ServiceAgentSingleton.setServerHost( host );
            ServiceAgentSingleton.setServerPort( port );
            pl("Force connection to server <"+ host +":"+ port +">");
         }
      }
   }

    private static void paramsOtherServer(final String[] params )
    {
        final String[] split = new String[params.length];
        for( int i = 0; i < params.length; i++ )
        {
            split[i] = params[i].trim();
            int pos = split[i].indexOf( '=' );
            if( pos < 0 )
            {
                continue;
            }
            // Parse parameter-value pairs
            String paramName = split[i].substring( 0, pos );
            if( split[i].length() <= pos + 1 )
            {
                continue;
            }
            String paramValue = split[i].substring( pos + 1 );
            // Handle runtime parameters
            if( paramName.equals( "params" ) )
            {
               parseHostPortValue(paramValue);
            }
        }
    }

    private static void pl( String s )
    {
        System.out.println( s );
    }

    private static void p( String s )
    {
        System.out.print( s );
    }

    private static void printHelp()
    {
        pl( "List of commands:" );
        pl( "  h, help, usage                   Prints this list" );
        pl( "  listall                          List all available NLP services" );
        pl( "  listpref                         List all preferences available to this client" );
        pl( "  setpref ["+ ClientUtils.XML_CLIENT_GLOBAL +"|"+ CLIENT_NAME +"]         Set the value of a client preference/attribute" );
        pl( "     pref1.attr1=val,...             at the given scope." );
        pl( "  recommend langs=l1,l2,...        Lists recommended services for the" );
        pl( "            doclang=dl               given user and document languages" );
        pl( "  invoke serviceName               Invokes a language service. Input will" );
        pl( "         docs=url1;url2,...          be the documents whose URLs are given, " );
        pl( "         params=(p1=v1,p2=v2,...)    params specifies runtime parameters." );
        pl( "  status                           Display a list of loaded Pipelines" );
        pl( "                                   Display a list of Queued(waiting) Pipelines" );

    }

    private static void printPreferences(final ArrayList<XMLElementModel> preferences)
    {
      for (final XMLElementModel pref : preferences) {
         final Map<String, String> attr = pref.getAttribute();
         final Set<String> keys = attr.keySet();
         for (final String key : keys) {
            pl(pref.getName() +"."+ key +"="+ attr.get(key));
         }
      }
    }

    // FIXME: Duplication from Eclipse Utils.java to be consolidated in CSAL
    // once all duplicated client ServiceAgentSingletons implementations are
    // refactored.
    public static void propertiesReader()
      throws NullPointerException {
		// Should return only one item in the list
	   ArrayList<XMLElementModel> server = ClientUtils.getClientPreference(CLIENT_NAME, "server");
		
   	// if there are no server defined for this client. then look for the last called one in the global scope
		if (server.size() == 0) {
	      server = ClientUtils.getClientPreference(ClientUtils.XML_CLIENT_GLOBAL, "lastCalledServer");
   	}
   	// Note that if the former case, if by mistake there are more than
      // one server defined, we pick the first one. If the specific host/port
      // attributes are not found, the preference file is corrupt &
      // implicitly throw an exception.
		ServiceAgentSingleton.setServerHost(server.get(0).getAttribute().get(ClientUtils.XML_HOST_KEY));
	   ServiceAgentSingleton.setServerPort(server.get(0).getAttribute().get(ClientUtils.XML_PORT_KEY));
    }
}

