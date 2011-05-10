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

public class SACLClient
{

    private static final String PROMPT = "==> ";
    private static final String WELCOME_MESSAGE = "\n\nWelcome to the command line client for the semantic text " +
                                                  "assistants architecture!\nType help or h for list of commands.\n\n";
    private static final String GOODBYE_MESSAGE = "Leaving the command line client...\n";
    private static String lastCommand = "";
    private static String lastParams = "";

    public static void main( String args[] )
    {

        if( args.length == 0 )
        {
            printHelp();
            System.exit( 0 );
        }

        String cmd = args[0];

        String params = "";
        if( args.length > 1 )
        {
            for( int i = 1; i < args.length; i++ )
            {
                params += args[i] + " ";
            }
        }

        executeCommand( cmd, params );
        System.exit( 0 );
    }

    private static void executeCommand( String cmd, String params )
    {
        if( cmd.equals( "h" ) || cmd.equals( "help" ) || cmd.equals( "usage" ) )
        {
            printHelp();
        }
        else if( cmd.equals( "again" ) || cmd.equals( "last" ) )
        {
            executeCommand( lastCommand, lastParams );
            return;
        }
        else if( cmd.equals( "printlast" ) )
        {
            pl( lastCommand + " " + lastParams );
            return;
        }
        else if( cmd.equals( "listall" ) )
        {

            if( params != null || params.length() > 0 )
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
                if( params == null || params.length() == 0 )
                {
                    printHelp();
                    return;
                }

                // Extract user context from the parameters
                UserContext ctx = buildUserContext( params );

                String serviceName = getServiceName( params );
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


        // Save for repeated usage
        lastCommand = cmd;
        lastParams = params;

    }

    private static UserContext buildUserContext( String params )
    {
        UserContext ctx = new UserContext();

        // Parse parameters
        String[] pa = params.split( " " );
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
            if( params.length() > pos + 1 )
            {
                params = params.substring( pos + 1 );
            }
            else
            {
                params = "";
            }
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

            if( params.length() > pos + 1 )
            {
                params = params.substring( pos + 1 );
            }
            else
            {
                params = "";
            }
        }

        return serviceName;
    }

    private static void invokeWithParams( String params, String serviceName, UserContext ctx )
    {
        UriList uriArray = new UriList();
        GateRuntimeParameterArray rtpArray = new GateRuntimeParameterArray();
        StringArray stringArray = new StringArray();
        String[] split = params.split( " " );
        for( int i = 0; i < split.length; i++ )
        {
            split[i] = split[i].trim();
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
                String[] runParams = paramValue.split( "," );
                if( runParams.length >= 2 )
                {
                    // runParams[0] -> for Host
                    // runParams[1] -> for Port
                    if( runParams[0].toLowerCase().contains( "host" ) && runParams[1].toLowerCase().contains( "port" ) )
                    {
                        String[] hostValue = runParams[0].split( "=" );
                        ServiceAgentSingleton.setServerHost( hostValue[1] );
                        String[] portValue = runParams[1].split( "=" );
                        ServiceAgentSingleton.setServerPort( portValue[1].substring( 0, portValue[1].length() - 1 ) );
                    }
                }
            }
        }
        SemanticServiceBroker agent = ServiceAgentSingleton.getInstance();
        String result = agent.invokeService( serviceName, uriArray, stringArray, 0L, rtpArray, ctx );
        pl( "" );
        pl( result );
    }

    private static void paramsOtherServer( String params )
    {
        String[] split = params.split( " " );
        for( int i = 0; i < split.length; i++ )
        {
            split[i] = split[i].trim();
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
                String[] runParams = paramValue.split( "," );
                if( runParams.length >= 2 )
                {
                    // runParams[0] -> for Host
                    // runParams[1] -> for Port
                    if( runParams[0].toLowerCase().contains( "host" ) && runParams[1].toLowerCase().contains( "port" ) )
                    {
                        String[] hostValue = runParams[0].split( "=" );
                        ServiceAgentSingleton.setServerHost( hostValue[1] );
                        String[] portValue = runParams[1].split( "=" );
                        ServiceAgentSingleton.setServerPort( portValue[1].substring( 0, portValue[1].length() - 1 ) );
                    }
                }
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
        pl( "  recommend langs=l1,l2,...        Lists recommended services for the" );
        pl( "            doclang=dl               given user and document languages" );
        pl( "  invoke serviceName               Invokes a language service. Input will" );
        pl( "         docs=url1;url2,...          be the documents whose URLs are given, " );
        pl( "         params=(p1=v1,p2=v2,...)    params specifies runtime parameters." );
        pl( "  printlast                        Print last command" );
        pl( "  again, last                      Execute last command" );
        pl( "  status                           Display a list of loaded Pipelines" );
        pl( "                                   Display a list of Queued(waiting) Pipelines" );

    }

    private static void prompt()
    {
        System.out.print( PROMPT );
    }

    private static boolean isExitCommand( String cmd )
    {
        cmd = cmd.toLowerCase();
        return cmd.equals( "exit" ) || cmd.equals( "quit" ) || cmd.equals( "bye" ) ||
               cmd.equals( "schluss" ) || cmd.equals( "raus" );
    }

}

