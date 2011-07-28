package info.semanticsoftware.semassist.client.eclipse.handlers;

import info.semanticsoftware.semassist.client.eclipse.model.SemanticAssistantsStatusViewModel;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.RTParamFrame;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;
import info.semanticsoftware.semassist.server.GateRuntimeParameter;
import info.semanticsoftware.semassist.server.GateRuntimeParameterArray;
import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.ServiceInfoForClient;
import info.semanticsoftware.semassist.server.UriList;
import info.semanticsoftware.semassist.server.UserContext;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import net.java.dev.jaxb.array.StringArray;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

/**
 * This class is in charge of invoking an NLP service which name is passed to the constructor.
 * @author Bahar Sateli
 * */
public class ServiceInvocationJob extends Job{

	/** This  variable is used as a container for parameters needed by some NLP services /
	/* Sample: String params = "docs=http://en.wikipedia.org/w/index.php?title=Stanley_Kubrick&printable=yes";
	*/
	String params = "";
	
	/** The singleton instance of service broker */
	private SemanticServiceBroker broker;
	
	/** The name of the service to be invoked */
	private String serviceName = null;
	
	/** The array of GATE pipelines' runtime parameters */
	private GateRuntimeParameterArray rtpArray = new GateRuntimeParameterArray();
	
	/** The arrays of strings to send to pipeline */
	private StringArray stringArray = new StringArray();
	
	/** The user context object */
	private UserContext ctx = new UserContext();
	
	/** The list of document URIs to send to pipeline */
	private UriList uriList = new UriList();
	
	/** The runtime parameters dialog */
	protected static JFrame mparamFrame = null;
	
	/** The strings containing the server XML response */
	private String 	serviceResponse = null;
	
	/** Constructor initializes the service name */
	public ServiceInvocationJob(String name) {
		super(name);
		this.serviceName = name;
	}

	/** This method runs when the job is scheduled to run by operating system.
	 * @return the status of the job when it is finished
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Retrieving annotations...", 1000000000);
		try
         {
             System.out.println( "Invoking " + serviceName + "..." );
    
             // Build user context from the parameters
             ctx = buildUserContext( params );
             
             invokeService( params , serviceName, ctx);

             monitor.done();
             return Status.OK_STATUS;
         }
         catch( javax.xml.ws.WebServiceException e )
         {
             System.err.println( "\nConnection error! Possible reasons: Exception on server or server not running." );
             SemanticAssistantsStatusViewModel.addLog("Connection error! Possible reasons: Exception on server or server not running.");
             monitor.done();
             return Status.CANCEL_STATUS;
         }
	}
	
	/** Builds the user context from the input parameters
	 * @param params parameters
	 *  */
	private UserContext buildUserContext( String params )
	    {
	        ctx = new UserContext();

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
	 
	/** Service invocation with parameters and context provided.
	 * @param params parameters
	 * @param serviceName name of the NLP service to be invoked
	 * @param ctx User context
	 *  */
	 private void invokeService( String params, String serviceName, UserContext ctx ){
		 		uriList = new UriList();
	        	        
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
								// We first try to cast the parameter to a URL instance to ensure it is in the right format
		                    	URL url = new URL( urls[j] );
		                        uriList.getUriList().add(url.toString());
		                    }
		                    catch( MalformedURLException e )
		                    {
		                        System.out.println( "Could not parse URL " + urls[j] );
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
		        
		        for(int i=0; i < stringArray.getItem().size(); i++){
		   	        	uriList.getUriList().add( new String( "#literal" ) );  
		   	        	uriList.getUriList().add(stringArray.getItem().get(i));
		   	    }
		        
		        // Before invoking the service, let's check if this pipeline requires runtime parameters...
	        	Iterator<ServiceInfoForClient> it = ServiceInformationThread.serviceInfos.iterator();
	 	        while( it.hasNext() )
	 	        {
	 	        		ServiceInfoForClient info = it.next();
		 	            if(info.getServiceName().equals(serviceName)){
		 	            	// Get all the runtime parameters needed
		 	            	List<GateRuntimeParameter> runtimeParams = info.getParams();
		 	            	
		 	            	if( runtimeParams.iterator().hasNext() ){
			 				   if( mparamFrame != null ){
			 	            		 if( !mparamFrame.isVisible() )
				                        {
				                            mparamFrame = null;
				                        }
				                        else
				                        {
				                            return;
				                        }
			 	            	 }
			 	            	mparamFrame = buildRTParamFrame( info, runtimeParams );
			                    mparamFrame.pack();
			                    mparamFrame.setLocation( 430, 430 );
			                    mparamFrame.setVisible( true );
		 	            	}else{
			 				    rtpArray = new GateRuntimeParameterArray();
			 				    doRunSelectedService();
			 	            }
		 	            	
		 	            	// We've found the service description, no more loops is needed
		 	            	break;
		 	            }
	 	        }
	 }
	 
	 /** This method adds literal strings to the list of service input 
	  * @param literal the literal text content
	  * */
	 public void addLiteral(String literal){
		 stringArray.getItem().add(literal);
	 }
	 
	 /**
	  * This method creates a frame to prompt the user for runtime parameters
	  * @param info The selected service information
	  * @param params The selected service list of runtime parameters
	  * @return JFrame The swing frame to be shown to the user
	  * @see info.semanticsoftware.semassist.csal.RTParamFrame
	  * */
	 private JFrame buildRTParamFrame(ServiceInfoForClient info, List<GateRuntimeParameter> params){

          Vector<GateRuntimeParameter> mandatory = new Vector<GateRuntimeParameter>();
          Vector<GateRuntimeParameter> optional = new Vector<GateRuntimeParameter>();

          for( Iterator<GateRuntimeParameter> it = params.iterator(); it.hasNext(); )
          {
              GateRuntimeParameter p = it.next();
              if( p.isOptional() )
              {
                  optional.add( p );
              }
              else
              {
                  mandatory.add( p );
              }
          }

          // Show window for parameter settings
          RTParamFrame frame = new RTParamFrame( info );
          frame.setOkActionListener( new ParamActionListener( frame ) );
          frame.setMandatories( mandatory );
          frame.setOptionals( optional );

          return frame;
      }
	   
	 private class ParamActionListener implements ActionListener{

          private RTParamFrame frame = null;
          public ParamActionListener( RTParamFrame f )
          {
              frame = f;
          }

          @Override
          public void actionPerformed( ActionEvent e )
          {
              GateRuntimeParameterArray params = frame.getParams();
              System.out.println( "------ Retrieved params array from the frame: " );
              List<GateRuntimeParameter> list = params.getItem();
              Iterator<GateRuntimeParameter> it = list.iterator();

              while( it.hasNext() )
              {
                  GateRuntimeParameter p = it.next();
                  //FIXME buggy, returns null
                  System.out.println( "------   Parameter: " + p.getParamName() + " = " + p.getStringValue());
              }

              frame = null;
              rtpArray = params;
              doRunSelectedService();
          }
      }
	
	 /**
	  * This methods invokes the NLP service with the proper runtime parameters 
	  * */ 
	 private void doRunSelectedService(){
		  try{
	        	broker = ServiceAgentSingleton.getInstance();
		        serviceResponse = broker.invokeService(serviceName, uriList, stringArray, 0L, rtpArray, ctx );
	        }catch( Exception connEx){
	            SemanticAssistantsStatusViewModel.addLog("Server not found. Please check the Server Host and Port and if Server is Online");
	        	System.err.println("Server not found. Please check the Server Host and Port and if Server is Online");
	            return;
	        }
	        
	        System.out.println("");
	        System.out.println(serviceResponse);
	        
	        // The response is ready, now let's decide how we're going to present it
	        handleResponse(serviceResponse);
			SemanticAssistantsStatusViewModel.addLog("Service invocation terminated.");

	        // Open the Semantic Assistants views
	        Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								FileSelectionHandler.openViews();
							}
						}); 
	        
	        // Signal that the job is done
	        this.done(ASYNC_FINISH);
	  }
	  
	 /**
	  * This method parses the server response and return the annotations in a proper format
	  * @param serviceResponse The server XML response
	  * */ 
	 private void handleResponse(String serviceResponse){
		  String documentString="";
		  boolean isDocument = false;
		  // returns result is sorted by annotation type
	      Vector<SemanticServiceResult> results = ClientUtils.getServiceResults( serviceResponse );
	       
	      if( results == null ) {
	                System.err.println( "No results retrieved in the response message." );
		            SemanticAssistantsStatusViewModel.addLog("No results retrieved in the response message.");
	                return;
		  }
	      
	      for( Iterator<SemanticServiceResult> it = results.iterator(); it.hasNext();){
	                SemanticServiceResult current = it.next();
	                if(current.mResultType.equals(SemanticServiceResult.FILE)){
	                	String fileContent = broker.getResultFile(current.mFileUrl);
	                	// Get file extension from MIME type or default to text if unknown.
	                	String fileExt = ClientUtils.getFileNameExt(current.mMimeType);
	                	if(fileExt == null){
                        fileExt = ClientUtils.FILE_EXT_TEXT;
	                	}
						ServerResponseHandler.createFile(fileContent, fileExt);
	                }
	                else if(current.mResultType.equals(SemanticServiceResult.BOUNDLESS_ANNOTATION)){
	                	//TODO find a way to handle annotation_in_whole type
	                	System.out.println("Annotation Case (Append to data structure). I don't know how to handle this!");
	                }
	                else if(current.mResultType.equals(SemanticServiceResult.ANNOTATION)){
	                	ServerResponseHandler.createAnnotation(current);
	                }
	                else if(current.mResultType.equals(SemanticServiceResult.DOCUMENT)){
	                	documentString += current.mFileUrl + System.getProperty("line.separator");
	                	isDocument = true;
	                }
	      }
	            
	      // FIXME Dirty hack!
	      if(isDocument){
	            	ServerResponseHandler.createDocument(documentString);
	      }
	  }
}
