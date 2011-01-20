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
package info.semanticsoftware.semassist.server.util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 *
 * @author nikolaos
 */
public class XMLFileParser
{

    private static String mGateHome = "";
    private static String mGatePluginDir = "";
    private static String mGateUserFile = "";
    private static String mServiceRepository = "";
    private static String mOntRepository = "";
    private static String mMergeCmd = "";
    private static String mHTMLtoText = "";
    private static String mServerPort = "";
    private static int mServerThreadsAllowed = 5; //Added this variable to hold the number of threads
    											  //read from the xml properties file for the Semassist	

    public static void read()
    {
        
        Project project = new Project();
        String propFile = "SemassistProperties.xml";
        try
        {

            File buildFile = new File( ".." + File.separator + propFile );
            project.init();
            ProjectHelper.configureProject( project, buildFile );
        }
        catch( Exception ex )
        {
            System.out.print( "Cannot find " + propFile );
            Logging.exception( ex );
        }


        mGateHome = project.getProperty( "gate-home" );
        mGatePluginDir = project.getProperty( "gate.plugin.dir" );
        mServiceRepository = project.getProperty( "service.repository" );
        mGateUserFile = project.getProperty( "gate.user.file" );
        mOntRepository = project.getProperty( "ontology.repository" );
        mMergeCmd = project.getProperty( "merge.command" );
        mHTMLtoText = project.getProperty( "html2text.command" );
        mServerPort = project.getProperty( "server.port.wsdl" );
        try{
        	mServerThreadsAllowed = Integer.parseInt(project.getProperty("server.threads.allowed"));
        }catch(Exception ex){
        	System.out.print( "The server.threads.allowed property in the " + propFile + " file has not been set correctly.\nYou must have it set to a valid numerical value\n" );
        }

        if( mGateHome ==null || mGatePluginDir==null|| mServiceRepository==null
                || mGateUserFile==null || mOntRepository==null || mMergeCmd==null
                || mHTMLtoText==null|| mServerPort==null)
        {
            System.out.print( "A property is missing/misspelled from " + propFile );
        }

    }

   
	protected static int getServerThreadsAllowed() {
		return mServerThreadsAllowed;
	}

	public static String getGateHome()
    {
        return mGateHome;
    }

    public static String getGatePluginDir()
    {
        return mGatePluginDir;
    }

    public static String getGateUserFile()
    {
        return mGateUserFile;
    }

    public static String getServiceRepository()
    {
        return mServiceRepository;
    }

    public static String getOntRepository()
    {
        return mOntRepository;
    }

    static String getMergeCmd()
    {
        return mMergeCmd;
    }

    static String getHTMLtoText()
    {
        return mHTMLtoText;
    }

    static String getServerPort()
    {
        return mServerPort;
    }

}
