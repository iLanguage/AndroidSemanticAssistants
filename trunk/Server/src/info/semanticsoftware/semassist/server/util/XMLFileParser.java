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
import java.util.ArrayList;
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
    
    private static ArrayList<String[]> alServerPipeline = new ArrayList<String[]>();
    
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
        
        threadXMLProperties(project, propFile);
        
//        if( mGateHome ==null || mGatePluginDir==null|| mServiceRepository==null
//                || mGateUserFile==null || mOntRepository==null || mMergeCmd==null
//                || mHTMLtoText==null|| mServerPort==null)
//        {
//            System.out.print( "A property is missing/misspelled from " + propFile );
//        }
        
        if(mGateHome == null){
        	System.out.println( "gate-home property is missing/misspelled from " + propFile );
        }
        if(mGatePluginDir == null){
        	System.out.println( "gate.plugin.dir property is missing/misspelled from " + propFile );
        }
        if(mServiceRepository == null){
        	System.out.println( "service.repository property is missing/misspelled from " + propFile );
        }
        if(mGateUserFile == null){
        	System.out.println( "gate.user.file property is missing/misspelled from " + propFile );
        }
        if(mOntRepository == null){
        	System.out.println( "ontology.repository property is missing/misspelled from " + propFile );
        }
        if(mMergeCmd == null){
        	System.out.println( "merge.command property is missing/misspelled from " + propFile );
        }
        if(mHTMLtoText == null){
        	System.out.println( "html2text.command property is missing/misspelled from " + propFile );
        }
        if(mServerPort == null){
        	System.out.println( "server.port.wsdl property is missing/misspelled from " + propFile );
        }
        

    }

	private static void threadXMLProperties(Project project, String propFile) {
		//*************************************************
        //Added by d_barbie...  this should allow for setting the correct pipeline numbers
        String readPipelineName = "";
        String readPipelinePooled = "";
        String readPipelineStartup = "";
        String readPipelineAppPath = "";
        int minThreadsAdded = 0;
        int xmlPropertyPipelineIDX = 1;
        
        readPipelineName = project.getProperty( "server.pipeline." + xmlPropertyPipelineIDX + ".name" );
        
        while(readPipelineName != null && !readPipelineName.equals("")){
        	readPipelinePooled = project.getProperty( "server.pipeline." + xmlPropertyPipelineIDX + ".number.pooled" );
        	readPipelineStartup = project.getProperty( "server.pipeline." + xmlPropertyPipelineIDX + ".startup" );
        	readPipelineAppPath = project.getProperty( "server.pipeline." + xmlPropertyPipelineIDX + ".fullpath" );
        	
        	String[] mServerPipeline = new String[4];
        	
        	mServerPipeline[0] = readPipelineName;
        	mServerPipeline[1] = readPipelinePooled;
        	mServerPipeline[2] = readPipelineStartup;
        	mServerPipeline[3] = readPipelineAppPath;
        	alServerPipeline.add(mServerPipeline);
        	xmlPropertyPipelineIDX++; //needed to iterate through the different xml thread numbers 
        	readPipelineName = project.getProperty( "server.pipeline." + xmlPropertyPipelineIDX + ".name" );

        	try{
        		minThreadsAdded += Integer.parseInt(mServerPipeline[1]);
        	}catch(Exception e){
        		minThreadsAdded+=0;
        	}
        };
        
        try{
        	mServerThreadsAllowed = Integer.parseInt(project.getProperty("server.threads.allowed"));
        }catch(Exception ex){
        	System.out.println( "The server.threads.allowed property in the " + propFile + " file has not been set correctly.\nYou must have it set to a valid numerical value\n" );
        }
        if(mServerThreadsAllowed <= minThreadsAdded){//checking to see if the total pipelines requested is larger than the set one.  If so show error message and set it with the correct value
        	mServerThreadsAllowed = minThreadsAdded+2;//adding a default of 2 more threads
        	System.out.println("The actual minimum threads requested by the pipeline properties are larger than the max allowed threads set in the server.threads.allowed property.\nThe value will be readjusted to contain the minimum pipelines requested/needed");
        }
        //end ...d_barbie
        //***************************************************
	}

    public static ArrayList<String[]> getPipelineThreadProperties(){
    	return alServerPipeline;
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
