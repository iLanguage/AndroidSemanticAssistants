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
package info.semanticsoftware.semassist.server;

import info.semanticsoftware.semassist.server.util.GATEPipelineOutput;
import info.semanticsoftware.semassist.server.util.GATERuntimeParameter;
import info.semanticsoftware.semassist.server.util.Logging;
import info.semanticsoftware.semassist.server.util.MasterData;
import info.semanticsoftware.semassist.server.util.ServiceInfo;
import info.semanticsoftware.semassist.server.util.ServiceInfoForClient;
import info.semanticsoftware.semassist.server.util.UserContext;

import java.io.*;
import java.util.*;
//import javax.xml.ws.Endpoint;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.*;
import java.net.*;


import javax.jws.soap.SOAPBinding;

import edu.stanford.smi.protegex.owl.jena.*;


import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;

import com.hp.hpl.jena.ontology.OntModel;

import com.hp.hpl.jena.rdf.model.Literal;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.QuerySolution;




import info.semanticsoftware.semassist.server.output.OutputBuilder;
import info.semanticsoftware.semassist.server.output.XMLOutputBuilder;
import info.semanticsoftware.semassist.server.util.*;
//import info.semanticsoftware.semassist.server.output.*;


import gate.*;
import gate.creole.*;
//import gate.gui.*; // for debugging only
//import gate.util.*;
import gate.gui.MainFrame;

@WebService()
@SOAPBinding( style = SOAPBinding.Style.RPC )
public class SemanticServiceBroker
{

	private static int nThreads=3;
    // Some constants related to GATE
    private static boolean mGateInited = false;
    // private HashMap<String, String> serviceToDirectory = new HashMap<String, String>();
    private HashMap<String, ServiceInfo> mAvailableServices = new HashMap<String, ServiceInfo>();
    /**
	 * @return the mAvailableServices
	 */
	protected HashMap<String, ServiceInfo> getmAvailableServices() {
		return mAvailableServices;
	}


	// The OWL model that will hold the information
    // on the language services
    protected JenaOWLModel mOwlModel = null;
    // A mReasoner instance
    private ProtegeReasoner mReasoner = null;

    
    private ExecutorService tpes = null; //this is my thread pool.
    
    
    public SemanticServiceBroker() throws Exception
    {
        // Let's see what we have to offer
        readServiceMetadata();

        // For the reasoning, have a model created containing
        // all the language services as OWL individuals
        getOWLModel();


        // Initialize GATE
        initGate();
        tpes = Executors.newFixedThreadPool(nThreads); //initialize the thread pool with the number of allowed
        												//concurrent threads
    }

    
    @WebMethod()
    public ServiceInfoForClient[] getAvailableServices()
    {
        // Initialize GATE
        testGatePathInit();

        List<ServiceInfoForClient> resultList = new ArrayList<ServiceInfoForClient>();
        Set<String> keys = mAvailableServices.keySet();


        for( Iterator<String> it = keys.iterator(); it.hasNext(); )
        {
            ServiceInfo si = (ServiceInfo) mAvailableServices.get( it.next() );

            if( si.getPublishAsNLPService() )
            {
                ServiceInfoForClient sic = new ServiceInfoForClient( si );
                //System.out.println("Providing info on " + sic.getServiceName());
                resultList.add( sic );
            }
        }

        ServiceInfoForClient[] result = new ServiceInfoForClient[0];
        return resultList.toArray( result );
    }

    private void testGatePathInit()
    {
        try
        {
            // Get GATE home
            // MasterData.load();
            //String gateHomePath = MasterData.getGateHome();
           // XMLFileParser.readAnt();

        }
        catch( Exception ex )
        {
            Logger.getLogger( SemanticServiceBroker.class.getName() ).log( Level.SEVERE, null, ex );
        }

    }

    /**
     * The purpose of this method is to -- in contrast to
     * @link{getAvailableServices} -- take the user's context,
     * passed as parameter into account. Reasoning is performed,
     * and the individuals (language services) that are eligible
     * will be looked up in the availableServices map.
     */
    @WebMethod()
    public ServiceInfoForClient[] recommendServices( @WebParam( name = "ctx" ) UserContext ctx )
    {
        if( mOwlModel == null )
        {
            Logging.log( "---------- Warning: Service recommendation requested but no OWL model present." );
            return null;
        }

        // Get an OntModel for collaboration with Pellet
        OntModel ontModel = mOwlModel.getOntModel();

        String queryString = buildQueryString( ctx );
        System.out.println( queryString );

        //Query query = new Query(queryStr);

        Query query = QueryFactory.create( queryString );

        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create( query, ontModel );
        List<ServiceInfoForClient> resultList = new ArrayList<ServiceInfoForClient>();
        try
        {
            ResultSet results = qe.execSelect();

            // Output query results
            // ResultSetFormatter.out(System.out, results, query);

            while( results.hasNext() )
            {
                QuerySolution soln = results.nextSolution();
                // RDFNode nameNode  = soln.get("name");
                Literal nameLit = soln.getLiteral( "name" );
                String sName = nameLit.getString();
                // Logging.log("---------------- sName: |" + sName + "|");

                ServiceInfo si = (ServiceInfo) mAvailableServices.get( sName );
                ServiceInfoForClient sic = new ServiceInfoForClient( si );
                resultList.add( sic );
            }
        }
        catch( Exception e )
        {
            Logging.exception( e );
        }
        finally
        {
            qe.close();
        }

        ServiceInfoForClient[] result = new ServiceInfoForClient[0];
        return resultList.toArray( result );
    }

    /**
     * Apply the service named <code>mServiceName</code> to the passed documents.
     * Documents that are passed literally (i.e. normal Strings) must be passed
     * in the <code>literalDocs</code> array. In that case, give the special URI
     * <code>MasterData.LITERAL_DOC_URI</code> must be given, so that
     * the agent knows that it should look for the document in the <code>literalDocs</code>
     * array. Order matters, i.e. the first document specified via <code>LITERAL_DOC_URI</code>
     * is taken from the first position in the array, etc.
     *
     * The number connID serves as a connection identifier.
     */
    @WebMethod()
    public String invokeService( @WebParam( name = "serviceName" ) String serviceName,
                                 @WebParam( name = "documents" ) URIList documents,
                                 @WebParam( name = "literalDocs" ) String[] literalDocs,
                                 @WebParam( name = "connID" ) long connID,
                                 @WebParam( name = "gateParams" ) GATERuntimeParameter[] gateParams,
                                 @WebParam( name = "userCtx" ) UserContext userCtx )
    {
    	//TODO: comment out the MainFrame call
    	MainFrame.getInstance().setVisible(true); // for debugging only
		
    	//create a new Thread that offers the call function
    	//it will allow the return of the future results once the thread has completed
		CallableServiceThread workers = new CallableServiceThread(serviceName, documents, literalDocs, connID, gateParams, userCtx, this);
		
		Future<String> ftp = tpes.submit(workers);//send the thread to the thread pool and wait for it's completion
    	
		String returnValue="";//used to store the return value from the thread
    	try{
    		returnValue = ftp.get(); //query the thread and get the result (String)
    	}catch(Exception e){
    		returnValue = e.getMessage(); 
    	}    	
    	return returnValue;    	
    }
    
    
    /*
     * Function created to pass the contents of the invokeService(webmethod) to a callable
     * protected function that could be referenced from another class in the same package.
     * In this case we are refering to the CallableServiceThread.
     * 
     * This method was necessary to keep the SemanticServiceBroker class attributes from having to be redefined
     * to protected or public
     */
    protected String invokeServiceCall(String serviceName,URIList documents,String[] literalDocs,
    		long connID,GATERuntimeParameter[] gateParams, UserContext userCtx){
        for( int i = 0; i < gateParams.length; i++ )
        {
            Logging.log( "---------------- Name: " + gateParams[i].getParamName() + ", value: " + gateParams[i].getValueAsObject() );
        }

        // Check if mServiceName exists
        
        if( !getmAvailableServices().containsKey( serviceName ) )
        {
        
            return MasterData.ERROR_ANNOUNCEMENT + "Service does not seem to exist.";
        }
        
        ServiceInfo si = getmAvailableServices().get( serviceName );
        
        // Create initial ServiceExecutionStatus object
        ServiceExecutionStatus status = new ServiceExecutionStatus();
        
        status.setParams( gateParams );
        
        // Assemble corpus
        Corpus corpus = getCorpusFromURIs( documents, literalDocs );
        
        Logging.log( "---------------- Corpus assembled. Size: " + corpus.size() );
        
        // if (corpus.size() > 0 || corpus.size() == 0) return "";
        status.setCorpus( corpus );
        
        // Pass service description(s) to the status object
        String concatenation = si.getConcatenationOf();
        
        Vector<ServiceInfo> descriptions = new Vector<ServiceInfo>();
        
        if( concatenation.trim().equals( "" ) )
        {
            Logging.log( "---------------- Single language service" );
        
            descriptions.add( si );
        
        }
        else
        {
        
            Logging.log( "---------------- Concatenated language services" );
            descriptions = getServiceDescriptionVector( concatenation );
        
        }
        
        status.setServiceInfos( descriptions );
        
        // Pass connection ID and user context to status object
        status.setConnID( connID );
        
        status.setUserContext( userCtx );
        
        // Run the language services one by one. Information is
        // passed from one to another via the shared corpus.
        Iterator<ServiceInfo> ito = descriptions.iterator();
        
        ServiceInfo latestService = null;
        
        Runtime runtime = Runtime.getRuntime();
        
        while( ito.hasNext() )
        {
            latestService = ito.next();
            status = runOneService( latestService, status );
            Logging.log( "---------------- totalMemory: " + runtime.totalMemory() );
            Logging.log( "---------------- maxMemory  : " + runtime.maxMemory() );
            if( status == null )
            {
                Logging.log( "---------------- Status object is null. Aborting..." );
                return "Exception on server.";
            }
        }

        Logging.log( "---------------- All services executed." );
        Iterator<GATEPipelineOutput> it = status.mExpectedOutputs.iterator();
        while( it.hasNext() )
        {
            GATEPipelineOutput o = it.next();
            Logging.log( "---------------- Expected output: " + o.getHRFormat() + ", " +
                         o.getAnnotation() + ", " + o.getFileURL() );
        }

        // Collect result
        String finalResult = getOutputInfo( new XMLOutputBuilder(),
                status.mExpectedOutputs, status.getCorpus() );
        Logging.log( "---------------- Assembled output string." );


	// cleanup corpus and documents
        cleanup( status );
        return finalResult;
    }
    
    @WebMethod()
    public String getResultFile( @WebParam( name = "resultFileUrl" ) URL url )
    {
        Logging.log( "---------------- Result file requested: " + url.toString() );
        try
        {
            File f = new File( url.toURI() );
            BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( f ) ) );
            StringBuffer result = new StringBuffer();
            String line = "";
            while( (line = reader.readLine()) != null )
            {
                result.append( line );
            }

            return result.toString();
        }
        catch( FileNotFoundException e )
        {
            Logging.log( "---------------- Could not find file " + url.toString() + " on disk. Omitting." );
        }
        catch( URISyntaxException e )
        {
            e.printStackTrace();
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }


        return "";
    }

    protected Vector<ServiceInfo> getServiceDescriptionVector( String concatenation )
    {
        Vector<ServiceInfo> result = new Vector<ServiceInfo>();

        String[] serviceArray = concatenation.split( "," );
        for( int i = 0; i < serviceArray.length; i++ )
        {
            ServiceInfo currentInfo = mAvailableServices.get( serviceArray[i].trim() );

            // For each parameter, let it know which pipeline
            // it belongs to because we are going to execute
            // several pipelines in a row
            //Utils.addPipelineInfo(currentInfo);

            if( currentInfo == null )
            {
                Logging.log( "---------------- Found no ServiceInfo object for " + serviceArray[i] );
            }
            result.add( currentInfo );
        }

        return result;
    }

    /*
     * Needed to modify this method in order to now either load a new service or pull an already existing service
     * from the threadpool.
     */
    
    protected ServiceExecutionStatus runOneService( ServiceInfo currentService, ServiceExecutionStatus status )
    {
        // Get a handle of the application file
        File serviceAppFile = new File( currentService.getAppFileName() );

        Logging.log( "\n\n---------------- Next application: " + serviceAppFile.toString() );
        if( !serviceAppFile.exists() )
        {
            Logging.log( MasterData.ERROR_ANNOUNCEMENT + "Service application file \"" +
                         serviceAppFile.toString() + "\" does not seem to exist." );
            return null;
        }

        // Load the application
        Logging.log( "---------------- Preparing to load application..." );
        
        //check if service already exists
        //TODO: check what happens here
        
        //check the to see if an already existing inactive service with the same type that is needed
        //can be used.   if so return it's position in the process register
        int gateProcessRegisterIDX = GateProcessRegister.getInstance().getInactiveServicePosition(serviceAppFile.getAbsolutePath());
        //make sure the serviceApp is completely clear
        Object serviceApp=null;
        Logging.log("Service Register INDEX " + gateProcessRegisterIDX);
        if(gateProcessRegisterIDX < 0){ //if no service is found
        	Logging.log("Service not found in Register");
        	serviceApp = loadGateApp( serviceAppFile ); //load a new service into memory
        	Logging.log("Created New Service");
        	
        	if(nThreads<=GateProcessRegister.getInstance().getCurrentRegisterSize()){ //check to see if the register exceeds the number of threads (processes) allowed
        		//we need to make space in the register to accept this new service
        		int inactiveProcessPosition = GateProcessRegister.getInstance().getInactiveServicePosition(serviceAppFile.getAbsolutePath());
        		//find an inactive service of the same type (there should only be a value if a service has finished between the previous call and now
        		if(inactiveProcessPosition < 0){//if no current inactive service with the same name is found
        			inactiveProcessPosition = GateProcessRegister.getInstance().getInactiveServicePosition();
        				//return any inactive service
        		}
        		if(inactiveProcessPosition >= 0){//if an inactive service is found
        			//we need to get it's reference and delete it from the GATE list of active processes (pipelines)
        			Object gateProcess = GateProcessRegister.getInstance().getGateProcessServiceStatus(inactiveProcessPosition).getGateService();
        				//retrieve service from register
        			if( !(gateProcess instanceof CorpusController) ){
        				Factory.deleteResource((SerialController)gateProcess);
        			}else{
        				Factory.deleteResource((CorpusController)gateProcess);
        			}
        			GateProcessRegister.getInstance().removeGateProcess(inactiveProcessPosition);
        				//remove the reference from the gate process register
        		}
        		
        	}
        	
			GateProcessRegister.getInstance().addGateProcess(serviceAppFile.getAbsolutePath(), serviceApp);
				//add the new process to the Register
        }else{
        		//if the process does already exists and it is inactive(free to use)
        	serviceApp = GateProcessRegister.getInstance().getGateProcessServiceStatus(gateProcessRegisterIDX).getGateService();
        		//retrieve service from register
        	GateProcessRegister.getInstance().ActivateGateProcess(gateProcessRegisterIDX);
        		//reactivate the process so it cannot be reused
        }
        Logging.log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++Process ID Started: " + serviceApp.hashCode());
        // Does the pipeline demand merged input documents?
        if( currentService.getMergeInputDocs() )
        {
            Logging.log( "---------------- Beginning to merge input documents" );
            // File merged = Utils.mergeInput(documents, literalDocs);
            status.setCorpus( info.semanticsoftware.semassist.server.util.Utils.mergeCorpusDocuments( status.getCorpus() ) );
        }

        // For parameters that are not given, use the default value
        status.mParams = addDefaultParamValues( currentService, status.mParams );

        // Check if all necessary runtime parameters are given
        if( !checkRuntimeParameters( currentService, status.mParams ) )
        {
            String output = MasterData.ERROR_ANNOUNCEMENT + "Not all required runtime " +
                            "parameters were given for service \"" + currentService.getServiceName() + "\"";
            Logging.log( output );
            return null;
        }

        // Possibly pass runtime parameters to the pipeline
        SerialController serialCtrl = (SerialController) serviceApp;
        Logging.log("serviceApp was set correctly");
        
        
        
        
        if( status.mParams != null && status.mParams.size() > 0 )
        {
            GateUtils.passRuntimeParameters( serialCtrl, status.mParams, currentService.getServiceName() );
        }

        // Not all output artifacts that the pipeline can theoretically
        // produce are produced for any combination of input parameter values.
        // Given the current input parameter values, check which output
        // artifacts to expect.
        Vector<GATEPipelineOutput> expectedOutputs = OwlUtils.getExpectedOutputs( currentService.mOutputArtifacts,
                status.mParams, currentService.getServiceName() );
        // Always save information on the latest expected
        // output artifacts in the status object
        status.mExpectedOutputs = expectedOutputs;

        // The pipeline might produce one or more files as (part of)
        // its output. If so, create temporary files, whose URLs will
        // be passed to the pipeline as targets for its output.
        boolean outputFilesPresent = GateUtils.assignOutputFileLocations( serialCtrl, expectedOutputs );
        if( !outputFilesPresent )
        {
            String message = "----------------" + MasterData.ERROR_ANNOUNCEMENT +
                             "Could not create temporary file for result output (IOException).";
            Logging.log( message );
            return null;
        }

        // The pipeline might produce a corpus as (part of) its
        // output. If so, find out how to set this corpus and
        // do it.
        Corpus resultCorpus = null;
        if( currentService.producesCorpus() )
        {
            Logging.log( "---------------- Language service produces a corpus..." );

	    /* RW: don't do anything special here 
            try
            {
                resultCorpus = Factory.newCorpus( "Result Corpus" );
            }
            catch( gate.creole.ResourceInstantiationException e )
            {
                Logging.exception( e );
                return null;
            }

            Logging.log( "---------------- Trying to assign empty corpus..." );
            boolean corpusAssigned = GateUtils.assignResultCorpus( serialCtrl,
                    currentService, resultCorpus );
            if( !corpusAssigned )
            {
                String message = "----------------" + MasterData.ERROR_ANNOUNCEMENT +
                                 "No parameter to assign output corpus to found.";
                Logging.log( message );
                return null;
            }
	    */
        }


        // Give the input (should only be 1 document) to
        // the relevant parameters, if there are any such parameters.
        Vector<GATERuntimeParameter> pti = currentService.mParamsTakingInput;
        if( pti.size() > 0 )
        {
            Corpus c = status.getCorpus();
            if( c.size() > 1 )
            {
                String warning = MasterData.ERROR_ANNOUNCEMENT + "Corpus contains more than one (" + c.size() +
                                 ") document and its text is supposed to be assigned to one or more runtime parameters. " +
                                 "Currently, this is not supported.";
                Logging.log( warning );
                return null;
            }
            Logging.log( "---------------- Passing input document to parameters..." );
            Document doc = GateUtils.getFirstDocument( c );
            GateUtils.passDocToParameters( serialCtrl, pti, doc );
        }

        // Check if application accepts a corpus. Right now,
        // there are two ways for the input document(s): //
        //   1. The get assembled in a corpus and a corpus
        //      controller runs on that corpus.
        //   2. One document is passed to a runtime parameter
        //      as a string.
        CorpusController corpusController = null;
        if( !(serviceApp instanceof CorpusController) )
        {
	    //RW check if this can be fixed in the service description, commented out for now
	    /*
            if( currentService.mParamsTakingInput.size() == 0 )
            {
                String warning = MasterData.ERROR_ANNOUNCEMENT + "Stored application \"" +
                                 currentService.getServiceName() + "\" is not a CorpusController, and no parameter " +
                                 "is designated (giveInputToParameter property) to take the input. " +
                                 "I do not know what to do with the input documents. Aborting...";
                Logging.log( warning );
                return null;
            }
	    */

	    // RW: set the corpus
            Logging.log( "---------------- Assigning corpus to pipeline..." );
            boolean corpusAssigned = GateUtils.assignResultCorpus( serialCtrl, currentService, status.getCorpus() );
	    // RW: maybe add the error handling from above...

	    // Run the pipeline
            try
            {
                Logging.log( "---------------- Running application..." );
                
                serialCtrl.execute();
                Logging.log( "---------------- Cleaning up controller..." );
                
              //TODO: DUPLICATED CODE TO BE REMOVED AND PLACED AT THE END OF FUNCTION
                int positionOfCurrentService = GateProcessRegister.getInstance().getServicePosition(serviceApp);  
                if(positionOfCurrentService < 0){
                	Logging.log("We have a problem with locating objects in Register!");
                }else{
                	Logging.log("Setting the Register Status to INACTIVE");
                	GateProcessRegister.getInstance().InActivateGateProcess(positionOfCurrentService);
                }
                Logging.log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++Process ID Stopped: " + serviceApp.hashCode());
                //TODO: Factory.deleteResource( serialCtrl );
                
                // If a corpus was produced as result, let
                // the execution status know
                /* RW: commented out, since we use the same corpus as above
		if( currentService.producesCorpus() )
                {
                    status.setCorpus( resultCorpus );
                }
		*/
            }
            catch( gate.creole.ExecutionException e )
            {
                Logging.exception( e );
                // return MasterData.ERROR_ANNOUNCEMENT  + "ExecutionException on server";
                return null;
            }

        }
        else
        {
            corpusController = (CorpusController) serviceApp;

            // Feed the corpus to the pipeline

            // TODO: Actually, we should check if the pipeline
            // uses some kind of document as input.
            try
            {
                Logging.log( "---------------- Running application..." );
                GateUtils.runApplicationOnCorpus( corpusController, status.getCorpus() );
                Logging.log( "---------------- Cleaning up controller..." );

                //TODO: DUPLICATED CODE TO BE REMOVED AND PLACED AT THE END OF FUNCTION
                int positionOfCurrentService = GateProcessRegister.getInstance().getServicePosition(serviceApp);  
                if(positionOfCurrentService < 0){
                	Logging.log("We have a problem with locating objects in Register!");
                }else{
                	Logging.log("Setting the Register Status to INACTIVE");
                	GateProcessRegister.getInstance().InActivateGateProcess(positionOfCurrentService);
                }
                Logging.log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++Process ID Stopped: " + serviceApp.hashCode());
                //TODO:  Factory.deleteResource( corpusController );
            }
            catch( gate.creole.ExecutionException e )
            {
                Logging.exception( e );
                // return MasterData.ERROR_ANNOUNCEMENT  + "ExecutionException on server";
                return null;
            }
        }

        Logging.log( "---------------- Application " + currentService.getServiceName() + " executed..." );

        return status;
    }

    /*
    // Assemble corpus
    Corpus corpus = getCorpusFromURIs(documents, literalDocs);
    System.out.println("---------------- Corpus assembled. Size: " + corpus.size());
    // if (corpus.size() > 0 || corpus.size() == 0) return "";


    // For parameters that are not given, use the default value
    gateParams = addDefaultParamValues(currentService, gateParams);

    // Check if all necessary runtime parameters are given
    if (!checkRuntimeParameters(currentService, gateParams)) {
    String output = MasterData.ERROR_ANNOUNCEMENT + "Not all required runtime " +
    "parameters were given for service \"" + currentService.getServiceName()  + "\"";
    Logging.log(output);
    return output;
    }

    // Possibly pass runtime parameters to the pipeline
    SerialController serialCtrl = (SerialController) serviceApp;
    if (gateParams != null && gateParams.length > 0) {
    GateUtils.passRuntimeParameters(serialCtrl, gateParams);
    }


    // Not all output artifacts that the pipeline can theoretically
    // produce are produced for any combination of input parameter values.
    // Given the current input parameter values, check which output
    // artifacts to expect.
    Vector<GATEPipelineOutput> mExpectedOutputs = OwlUtils.getExpectedOutputs(currentService.mOutputArtifacts, gateParams);


    // The pipeline might produce one or more files as (part of)
    // its output. If so, create temporary files, whose URLs will
    // be passed to the pipeline as targets for its output.
    Iterator<GATEPipelineOutput> it_output = mExpectedOutputs.iterator();

    while (it_output.hasNext()) {
    GATEPipelineOutput o = it_output.next();
    if (o == null) continue;

    GATERuntimeParameter ofp = o.getParameterForFileURL();
    if (ofp != null) {
    Logging.log("------------- Setting output file parameter " + ofp.getParamName());

    // Create a temporary file
    File outFile = Utils.createTempFile();
    if (outFile == null) {
    return MasterData.ERROR_ANNOUNCEMENT +
    "Could not create temporary file for result output (IOException).";
    }


    // Assign the file's URL as value of the runtime parameter
    URL fileURL = null;
    try {
    fileURL = outFile.toURI().toURL();
    ofp.setUrlValue(fileURL);
    } catch (MalformedURLException e) {
    Logging.exception(e);
    return MasterData.ERROR_ANNOUNCEMENT +
    "Caught MalformedURLException.";
    } catch (Exception e) {
    Logging.exception(e);
    }

    // Let the output object know where its file is
    o.setFileURL(fileURL);
    GateUtils.passRuntimeParameter(serialCtrl, ofp);
    }
    }


    // Finally feed the corpus to the pipeline
    try {
    Logging.log("------------ Running application...");
    GateUtils.runApplicationOnCorpus(corpusController, corpus);
    } catch (gate.creole.ExecutionException e) {
    Logging.exception(e);
    return MasterData.ERROR_ANNOUNCEMENT  + "ExecutionException on server";
    }
    System.out.println("------------ Application executed. Retrieving results...");

    // Process results
    String finalResult = getOutputInfo(new XMLOutputBuilder(), mExpectedOutputs, corpus);

    System.out.println("------------ Assembled output string. Cleaning up...");

    System.out.println("------------ Cleaning up corpus...");
    Factory.deleteResource(corpus);
    System.out.println("------------ Cleaning up controller...");
    Factory.deleteResource(corpusController);

    return finalResult;
    }
     */
    protected Object loadGateApp( File serviceAppFile )
    {
        Object result;
        try
        {
        	result = gate.util.persistence.PersistenceManager.loadObjectFromFile( serviceAppFile );
        }
        catch( Exception e )
        {
            Logging.exception( e );
            Class c = e.getClass();
            return MasterData.ERROR_ANNOUNCEMENT +
                   "Could not instantiate the application on the server (" + c.getName() + ").";
        }
        return result;
    }

    @WebMethod( exclude = true )
    public static void initGate()
    {

        if( !mGateInited )
        {

            try
            {
                // Get GATE home
            	Logging.log("# of Threads: " + nThreads);
            	nThreads = MasterData.Instance().getServerThreadsAllowed();
            	Logging.log("# of Threads: " + nThreads);
                String gateHomePath = MasterData.Instance().getGateHome();
                String gatePluginDir = MasterData.Instance().getGatePluginDir();
                String gateUserFile = MasterData.Instance().getGateUserFile();
                
                File gateHome = new File( gateHomePath );

                Gate.setGateHome( gateHome );
                Gate.setPluginsHome( new File( gatePluginDir ) );
                Gate.setSiteConfigFile( new File( gateHomePath + "/gate.xml" ) );
                Gate.setUserConfigFile( new File( gateUserFile ) );
                Gate.init();


                // Load plugins, for example...
                //URL url = new URL("file://" + gatePluginDir + "/ANNIE");
                // System.out.println("URL: " + url);
                //Gate.getCreoleRegister().registerDirectories(url);


                mGateInited = true;
            }
            catch( Exception e )
            {
                System.out.println( e );
                System.out.println( "You might want to adapt your ~/.semanticAssistants/semassist.properties" );
            }

        }

    }

    protected void readServiceMetadata() throws Exception
    {
        // Try to acquire the location of the service repository
        try
        {
            File rep = new File( MasterData.Instance().getServiceRepository() );
            Vector<ServiceInfo> concatenatedServices = new Vector<ServiceInfo>();
            Iterator<File> it = Arrays.asList( rep.listFiles() ).iterator();

            // Iterate over the files in the service repository.
            // Every .owl file will be read.
            while( it.hasNext() )
            {
                File currentFile = it.next();
                // Filter out .CVS
                if( currentFile.getName().equals( ".CVS" ) )
                {
                    continue;
                }
                if( currentFile.getName().equals( "CVS" ) )
                {
                    continue;
                }

                // Is the current file an .owl file?
                if( currentFile.getName().endsWith( ".owl" ) )
                {
                    Logging.log( "\n\n---------------- Reading service description file (for Java object) " +
                                 currentFile.getName() + "  -------------\n\n" );
                    ServiceInfo si = null;
                    try
                    {
                        // Get information on the service from the .owl file
                        si = OwlUtils.getServiceInfoFromFile( currentFile );
                    }
                    catch( NullPointerException e )
                    {
                        System.out.println( "\nService description not properly read. " +
                                            "Some reason in getServiceInfo() for file " + currentFile.getName() );
                        Logging.exception( e );
                    }

                    if( si != null )
                    {
                        // si.setServiceDir(currentDir.getName());
                        mAvailableServices.put( si.getServiceName(), si );
                        if( si.getConcatenationOf() != null && !si.getConcatenationOf().equals( "" ) )
                        {
                            concatenatedServices.add( si );
                        }
                    }
                    else
                    {
                        Logging.log( "\nService Info object was null for file " + currentFile.getName() );
                    }
                }
                else
                {
                    Logging.log( "---------------- Omitting file " + currentFile.getName() +
                                 ", not an .owl file" );
                }


            } // while (for each service)
            info.semanticsoftware.semassist.server.util.Utils.completeServiceInformation( concatenatedServices, mAvailableServices );

        }
        catch( NullPointerException e )
        {
            Logging.exception( e );
        }
    }

    protected void getOWLModel() throws Exception
    {
        // Try to acquire the location of the service repository
        try
        {
            File rep = new File( MasterData.Instance().getServiceRepository() );
            Iterator<File> it = Arrays.asList( rep.listFiles() ).iterator();
            Vector<File> sdFiles = new Vector<File>();

            // Iterate over the directories in the service repository.
            // Each directory should contain the GATE application and
            // a descriptory file.
            while( it.hasNext() )
            {
                File currentFile = it.next();
                // Filter out .CVS
                if( currentFile.getName().equals( ".CVS" ) )
                {
                    continue;
                }
                if( currentFile.getName().equals( "CVS" ) )
                {
                    continue;
                }

                if( !currentFile.getName().endsWith( ".owl" ) )
                {
                    continue;
                }

                System.out.println( "\n\n---------- Reading service description (for model) for " +
                                    currentFile.getName() + "  -------------\n\n" );

                /*
                String filePath = "";
                try {
                File f = OwlUtils.getServiceDescriptionFile(currentFile);
                filePath = f.getCanonicalPath();
                sdFiles.add(f);
                } catch (NullPointerException e) {
                System.out.println("\nNullPointerException: Failed adding OWL information for file " + filePath);
                Logging.exception(e);
                } catch (IOException e) {
                Logging.exception(e);
                }
                 */
                sdFiles.add( currentFile );

            } // while (for each service)

            // Now, get a model containing all services
            mOwlModel = OwlUtils.createServicesModel( sdFiles );

        }
        catch( NullPointerException e )
        {
            Logging.exception( e );
        }
    }

    protected String buildQueryString( UserContext ctx )
    {
        String result = "PREFIX cu: <http://localhost/ConceptUpper.owl#>\n";
        result += "PREFIX sa: <http://localhost/SemanticAssistants.owl#>\n";
        result += "SELECT ?x ?name \n" +
                  "WHERE {\n" +
                  "  ?x sa:hasGATEName ?name " +
                  " .\n  {{?x cu:hasFormat sa:GATECorpusPipeline_Format} UNION " +
                  "  \n   {?x cu:hasFormat sa:GATEConditionalCorpusPipeline_Format} UNION " +
                  "  \n   {?x cu:hasFormat sa:GATEPipeline_Format}} " +
                  " .\n  ?x sa:publishAsNLPService true ";



        // User/output languages
        Vector<String> ulangs = ctx.mUserLanguages;
        if( ulangs != null && ulangs.size() > 0 )
        {

            // Test for "no language given" (semantics: any language works)
            result += " .\n  {" +
                      "  \n    {" +
                      " OPTIONAL {?x sa:hasOutputNaturalLanguage ?lang} " +
                      " .\n      FILTER (!bound(?lang)) " +
                      "  \n    }" +
                      "  \n    UNION " +
                      // Test for one of the languages (and
                      // for the special anyLanguage instance
                      "  \n    {" +
                      "  \n      {?x sa:hasOutputNaturalLanguage cu:anyLanguage} ";
            // Add output language restrictions
            Iterator<String> it = ulangs.iterator();
            while( it.hasNext() )
            {
                String current = it.next();
                result += " UNION {?x sa:hasOutputNaturalLanguage cu:" + current + "} ";
            }
            result += "  \n    }" +
                      "  \n  } ";
        }

        // Document/input languages
        if( ctx.mDocLang != null && !ctx.mDocLang.equals( "" ) )
        {
            // Add input document restrictions
            result += " .\n  {" +
                      "  \n    {" +
                      " OPTIONAL {?x sa:hasInputNaturalLanguage ?lang2} " +
                      " .\n      FILTER (!bound(?lang2)) " +
                      "  \n    }" +
                      "  \n    UNION " +
                      "  \n    {?x sa:hasInputNaturalLanguage cu:" + ctx.mDocLang + "}" +
                      "  \n  }";
        }

        result += "\n}\n";

        return result;
    }

    protected Corpus getCorpusFromString( String argument )
    {
        Corpus corpus = null;
        try
        {
            corpus = GateUtils.getCorpusFromString( argument );
        }
        catch( gate.creole.ResourceInstantiationException e )
        {
            Logging.exception( e );
        }
        return corpus;
    }

    protected Corpus getCorpusFromURIs( URIList documents, String[] literalDocs )
    {
        Corpus corpus = null;
        try
        {
            corpus = Factory.newCorpus( "Transient Corpus" );
        }
        catch( ResourceInstantiationException e )
        {
            Logging.exception( e );
            return null;
        }

        // System.out.println("--------- Creating corpus. Documnts: " + documents.length + ". Literal: " + literalDocs.length);

        int literalIndex = 0;
        for( int i = 0; i < documents.uriList.size(); i++ )
        {
            URI current = documents.uriList.get( i );

            // If the current document is given as a string: Get
            // it from the literalDocs array
            if( current == null ||
                current.toString().equals( MasterData.LITERAL_DOC_URI ) ||
                current.toString().startsWith( "#" ) )
            {
                if( literalDocs != null && literalIndex <= literalDocs.length - 1 )
                {
                    // Indices are fine. Create new document and add it to the corpus
                    Document d;

                    try
                    {
                        d = Factory.newDocument( literalDocs[literalIndex] );
                        // Some GATE components need documents to have
                        // a URL, so we make sure every document has one
                        d.setSourceUrl( MasterData.getDummyDocumentURL() );
                        literalIndex++;
                        corpus.add( d );
                    }
                    catch( ResourceInstantiationException e )
                    {
                        Logging.exception( e );
                    }


                }
                else
                {
                    // Issue warning
                    Logging.log( MasterData.WARNING_ANNOUNCEMENT + "Document URI says literal, " +
                                 "but no more literally passed documents found. Skipping..." );
                }
            }
            else
            {
                URL u = null;

                try
                {
                    u = current.toURL();
                    Document d = Factory.newDocument( u );
                    corpus.add( d );

                }
                catch( MalformedURLException e )
                {
                    Logging.log( MasterData.WARNING_ANNOUNCEMENT + "URL could not be " +
                                 "constructed from URI \"" + current.toString() + "\". Skipping." );
                }
                catch( IllegalArgumentException e )
                {
                    Logging.log( MasterData.WARNING_ANNOUNCEMENT + "URI \"" + current.toString() +
                                 "\" seems not to be absolute. Skipping." );
                }
                catch( ResourceInstantiationException e )
                {
                    Logging.exception( e );
                }
            }

        } // end for


        return corpus;
    }

    /**
     * Attention: Do not use this method if mParams contains
     * parameters for multiple GATE pipelines. This method does not
     * check pipeline affiliation.
     */
    protected GATERuntimeParameter[] addDefaultParamValues( ServiceInfo info, GATERuntimeParameter[] params )
    {
        // Assemble the initial return vector
        Vector<GATERuntimeParameter> v = new Vector<GATERuntimeParameter>();
        if( params != null )
        {
            for( int i = 0; i < params.length; i++ )
            {
                v.add( params[i] );
            }
        }

        // Get the *possible* parameters
        Vector<GATERuntimeParameter> p = info.mParams;
        Iterator<GATERuntimeParameter> it = p.iterator();

        // Walk over the possible parameters
        while( it.hasNext() )
        {
            GATERuntimeParameter param = it.next();

            // Does the parameter have a default value?
            if( param.getDefaultValueString() != null )
            {

                boolean isGiven = false;
                for( int i = 0; i < params.length; i++ )
                {
                    if( param.getParamName().equals( params[i].getParamName() ) &&
                        param.getPRName().equals( params[i].getPRName() ) )
                    {
                        isGiven = true;
                        break;
                    }
                } // end for

                if( !isGiven )
                {
                    // Value not given and default value available
                    GATERuntimeParameter newParam = new GATERuntimeParameter( param );
                    newParam.takeDefaultValue();
                    v.add( newParam );
                }
            }
        }

        return v.toArray( params );
    }

    /**
     * Takes a vector of (typically) client-provided parameters and
     * adds parameter objects holding their default values to this
     * vector.
     */
    protected Vector<GATERuntimeParameter> addDefaultParamValues( ServiceInfo info, Vector<GATERuntimeParameter> clientParams )
    {
        // We need this separate vector to avoid a
        // ConcurrentModificationException
        Vector<GATERuntimeParameter> addThese = new Vector<GATERuntimeParameter>();
        //Logging.log("---------------- Number of clientParams: " + clientParams.size());

        // Get the *possible* parameters and walk over them
        Vector<GATERuntimeParameter> p = info.mParams;
        Iterator<GATERuntimeParameter> it = p.iterator();
        while( it.hasNext() )
        {
            GATERuntimeParameter param = it.next();
            // Logging.log("---------------- Examining parameter with default value " + param.getDefaultValueString());

            if( param.getDefaultValueString() != null )
            {
                boolean isGiven = false;

                Iterator<GATERuntimeParameter> itClient = clientParams.iterator();
                while( itClient.hasNext() )
                {
                    GATERuntimeParameter clientParam = itClient.next();

                    // Does the parameter belong to the pipeline represented by info?
                    if( !clientParam.getPipelineName().equals( "" ) &&
                        !clientParam.getPipelineName().equals( info.getServiceName() ) )
                    {
                        Logging.log( "---------------- Not the same service name. Skipping..." );
                        continue;
                    }

                    // Same PR name and parameter name?
                    if( param.getParamName().equals( clientParam.getParamName() ) &&
                        param.getPRName().equals( clientParam.getPRName() ) )
                    {
                        // Logging.log("---------------- Parameter " + param.getParamName() + " given...");
                        isGiven = true;
                        break;
                    }
                }


                if( !isGiven )
                {
                    Logging.log( "---------------- Parameter " + param.getParamName() + " not given. Setting to default..." );
                    // Value not given and default value available
                    GATERuntimeParameter newParam = new GATERuntimeParameter( param );
                    newParam.takeDefaultValue();
                    addThese.add( newParam );
                }

            } // end if has default value
        } // end outer while

        clientParams.addAll( addThese );
        return clientParams;
    }

    /**
     * Checks if, according to the service description data
     * given in <code>info</info>, the <code>gateParams</code>
     * array contains all the required parameters.
     */
    protected boolean checkRuntimeParameters( ServiceInfo info, GATERuntimeParameter[] gateParams )
    {
        Vector<GATERuntimeParameter> v = new Vector<GATERuntimeParameter>();
        for( int i = 0; i < gateParams.length; i++ )
        {
            v.add( gateParams[i] );
        }
        return checkRuntimeParameters( info, v );
    }

    protected boolean checkRuntimeParameters( ServiceInfo info, Vector<GATERuntimeParameter> gateParams )
    {
        if( !info.hasMandatoryRuntimeParams() )
        {
            return true;
        }
        if( gateParams == null || gateParams.size() == 0 )
        {
            return false;
        }


        //boolean result = true;
        Vector<GATERuntimeParameter> p = info.mParams;
        Iterator<GATERuntimeParameter> it = p.iterator();

        // Remember what has already been checked
        boolean[] accountedFor = new boolean[gateParams.size()];
        for( int i = 0; i < accountedFor.length; i++ )
        {
            if( !gateParams.get( i ).getPipelineName().equals( info.getServiceName() ) )
            {
                accountedFor[i] = true;
            }
            else
            {
                accountedFor[i] = false;
            }
        }
        int numAccountedFor = 0;
        int numOptional = 0;


        // Walk over the possible parameters
        while( it.hasNext() )
        {
            GATERuntimeParameter param = it.next();
            if( !param.getOptional() )
            {
                String paramName = param.getParamName();
                String prName = param.getPRName();
                boolean isThere = false;

                // Check in the gateParams vector if the
                // parameter is there
                for( int i = 0; i < gateParams.size(); i++ )
                {
                    if( accountedFor[i] )
                    {
                        continue;
                    }
                    if( paramName.equals( gateParams.get( i ).getParamName() ) &&
                        prName.equals( gateParams.get( i ).getPRName() ) )
                    {
                        isThere = true;
                        accountedFor[i] = true;
                        numAccountedFor--;
                    }
                }

                if( !isThere )
                {
                    // Not all required parameters are given
                    System.out.println( "--------- Parameter " + paramName + " is missing." );
                    return false;
                }

            }
            else
            {
                numOptional++;
            }
        } // end while

        // Now we could check if numOptional == numAccountedFor
        // and take according action. Omitted for now.
        return true;
    }

    /**
     * This function has a builder assemble the response for the
     * client.
     */
    protected String getOutputInfo( OutputBuilder builder, Vector<GATEPipelineOutput> outputs, Corpus corpus )
    {

        builder.setCorpus( corpus );

        if( outputs == null )
        {
            return builder.getEmptyResponse();
        }

        // Logging.log("---------------- getOutputInfo(): Size = " + outputs.size());
        Iterator<GATEPipelineOutput> it = outputs.iterator();
        builder.reset();
        while( it.hasNext() )
        {
            builder.addOutput( it.next() );
        }

        String s = builder.getResult();
        Logging.log( "---------------- Got result. s = " );
        Logging.log( s );
        return s;
    }


    /**
     * Cleanup documents and corpus after service execution
     */
    protected void cleanup( ServiceExecutionStatus status ) {

	 // remove documents from memory
	 Iterator<Document> it_doc = status.getCorpus().iterator();
	 while( it_doc.hasNext() ) {
	     Document d = it_doc.next();
	     if (d != null) {
		 Logging.log("------------ Cleaning up document...");
		 it_doc.remove();
		 Factory.deleteResource(d);
	     }
	 }

	 Logging.log("------------ Cleaning up corpus...");
	 Factory.deleteResource( status.getCorpus() );	 
     }
}



