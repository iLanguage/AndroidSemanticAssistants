/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2009, 2010, 2011 Semantic Software Lab, http://www.semanticsoftware.info
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
 * Administer data like location of semantic
 * services etc.
 */
package info.semanticsoftware.semassist.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MasterData
{

    /**
     * Uniform error message introduction
     */
    public static final String ERROR_ANNOUNCEMENT = "Error: ";
    /**
     * Uniform warning message introduction
     */
    public static final String WARNING_ANNOUNCEMENT = "Warning: ";
    /**
     * Service application files must have a standard file name
     */
    public static final String SERVICE_APP_FILENAME = "serviceApp";
    public static final String LITERAL_DOC_URI = "#literal";
    private static URL DUMMY_DOCUMENT_URL;
    public static final String OWL_PREFIX_SEMASSIST_NAMESPACE = "sa";
    // File where the settings are stored
    private static final String SETTINGS_FILE = "semassist.properties";
    private static File file = null;
    private static Properties propertyFile = null;

    private static MasterData mInstance = new MasterData();


    private MasterData()
    {
        XMLFileParser.read();
        
    }
    //Function used to retrieve the number of threads we wish to allow to run concurrently on the
    //machine running the server
    public int getServerThreadsAllowed(){
    	return XMLFileParser.getServerThreadsAllowed();
    }
    
    public ArrayList<String[]> getPipelineThreadProperties(){
    	return XMLFileParser.getPipelineThreadProperties();
    }
    public String getGateHome()
    {
        //checkFile();
        //return propertyFile.getProperty( "gate.home" );
        return XMLFileParser.getGateHome();
    }

    public String getGatePluginDir()
    {
        //checkFile();
        //return propertyFile.getProperty( "gate.plugin.dir" );
        return XMLFileParser.getGatePluginDir();
    }

    public String getGateUserFile()
    {
        //checkFile();
        //return propertyFile.getProperty( "gate.user.file" );
        return XMLFileParser.getGateUserFile();
    }

    public String getServiceRepository()
    {
        //checkFile();
        // return propertyFile.getProperty( "service.repository" );
        return XMLFileParser.getServiceRepository();
    }

    public String getOntRepository()
    {
        //checkFile();
        //return propertyFile.getProperty( "ontology.repository" );
        return XMLFileParser.getOntRepository();
    }

    public String getMergeCmd()
    {
        return XMLFileParser.getMergeCmd();
    }

    public String getHTMLtoText()
    {
        return XMLFileParser.getHTMLtoText();
    }

    public static String getWSPublishURL()
    {
        InetAddress inetAddress;
        try
        {
            inetAddress = java.net.InetAddress.getLocalHost();
            return "http://" + inetAddress.getHostName() + ":" + XMLFileParser.getServerPort() +"/SemAssist";
        }
        catch( UnknownHostException ex )
        {
            Logger.getLogger( MasterData.class.getName() ).log( Level.SEVERE, null, ex );
        }
        return "";
    }

    
/*
    public static void load() throws IOException
    {

        String path = System.getProperty( "user.home" ) + File.separatorChar;
        String sc = "" + File.separatorChar;
        String containsRepository = path;

        // Decide where we put our settings file, dependent
        // on operating system
        String os = System.getProperty( "os.name" );
        if( os.toLowerCase().indexOf( "linux" ) != -1 )
        {
            path += ".semanticAssistants";
        }
        else if( os.toLowerCase().indexOf( "windows" ) != -1 )
        {
            path += "Application Data" + File.separatorChar + "SemanticAssistants";
        }
        else if( os.toLowerCase().indexOf( "mac" ) != -1 )
        {
            path += ".semanticAssistants";
        }

        file = new File( path );
        if( !file.exists() )
        {
            file.mkdirs();
        }
        file = new File( path + File.separatorChar + SETTINGS_FILE );

        // Create a default settings file if it does not exist
        if( !file.exists() )
        {
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream( file );

            // Some default properties
            String[] gh =
            {
                "usr", "local", "durmtools",
                "GATE", "gate"
            };
            String[] plugin =
            {
                "usr", "local", "durmtools", "GATE",
                "gate", "plugins"
            };
            String[] guf =
            {
                "~", "Repository", "durm", "Projects", "SemanticAssistants",
                "Server", "gate-home", "user-gate.xml"
            };
            String[] sh =
            {
                "Repository", "durm", "Projects", "SemanticAssistants",
                "Resources", "OwlServiceDescriptions"
            };
            String[] or =
            {
                "Repository", "durm", "Projects", "SemanticAssistants",
                "Resources", "ont-repository"
            };

            String[] mergeTool =
            {
                "Repository", "durm", "Resources", "misc-tools", "multidoc-combine",
                "combine-multi-docs-edit.pl"
            };




            String defaultProperties = "gate.home=" + sc + concatPath( gh, sc ) + sc;
            defaultProperties += "\ngate.plugin.dir=" + sc + concatPath( plugin, sc ) + sc;
            defaultProperties += "\nservice.repository=" + containsRepository + sc +
                                 concatPath( sh, sc ) + sc;
            defaultProperties += "\ngate.user.file=" + concatPath( guf, sc );

            defaultProperties += "\nontology.repository=" + containsRepository + concatPath( or, sc ) + sc;

            defaultProperties += "\nmerge.command=" + containsRepository +
                                 concatPath( mergeTool, sc ) + " -f %OUTPUT_FILE% %INPUT_FILES%";

            // If not windows...
            if( os.toLowerCase().indexOf( "windows" ) == -1 )
            {
                defaultProperties += "\nhtml2text.command=/usr/bin/html2text -nobs";
            }

            defaultProperties += "\nyahoo.id=insert your key here";



            /*
            defaultProperties       += "\ncorpus.repository=" + containsRepository +
            concatPath(cr, sc) + sc;
             

            defaultProperties += "\n";

            fo.write( defaultProperties.getBytes() );
            fo.flush();
            fo.close();
        }
        propertyFile = new Properties();
        propertyFile.load( new FileInputStream( file ) );

    }*/

    public static URL getDummyDocumentURL()
    {
        if( DUMMY_DOCUMENT_URL == null )
        {
            try
            {
                DUMMY_DOCUMENT_URL = new URL( "http://none" );
            }
            catch( MalformedURLException e )
            {
                e.printStackTrace();
            }
        }

        return DUMMY_DOCUMENT_URL;
    }

    protected static String concatPath( String[] elements, String sep )
    {
        String result = "";
        for( int i = 0; i < elements.length; i++ )
        {
            if( !result.equals( "" ) )
            {
                result += sep;
            }

            result += elements[i];
        }
        return result;
    }

    /**
     * @return the mInstance
     */
    public static MasterData Instance()
    {
        return mInstance;
    }

}
