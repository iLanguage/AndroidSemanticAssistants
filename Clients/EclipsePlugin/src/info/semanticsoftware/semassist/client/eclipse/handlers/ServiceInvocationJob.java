package info.semanticsoftware.semassist.client.eclipse.handlers;

import info.semanticsoftware.semassist.client.eclipse.model.AnnotationParser;
import info.semanticsoftware.semassist.client.eclipse.model.SemanticAssistantsStatusViewModel;
import info.semanticsoftware.semassist.server.GateRuntimeParameterArray;
import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.UriList;
import info.semanticsoftware.semassist.server.UserContext;
import java.net.MalformedURLException;
import java.net.URL;
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

	/** The name of the service to be invoked */
	String serviceName = "";

	/** This  variable is used as a container for parameters needed by some NLP services /
	/* Sample parameters
	 * String params = "docs=http://en.wikipedia.org/w/index.php?title=Stanley_Kubrick&printable=yes";
	 * */
	String params = "";

	private GateRuntimeParameterArray rtpArray = new GateRuntimeParameterArray();
    
	private StringArray stringArray = new StringArray();

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
             UserContext ctx = buildUserContext( params );
             
             //TODO check with SA document
             if(params.equals("")){
    			 literalInvocation();
    	      }else{
                invokeWithParams( params , serviceName, ctx);
             }
             monitor.done();
             return Status.OK_STATUS;
         }
         catch( javax.xml.ws.WebServiceException e )
         {
             System.out.println( "\nConnection error. Possible reasons: Exception on server or server not running." );
             SemanticAssistantsStatusViewModel.addLog("Connection error. Possible reasons: Exception on server or server not running.");
             monitor.done();
             return Status.CANCEL_STATUS;
         }finally{
        	 Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							FileSelectionHandler.openViews();
						}
					}); 
        	this.done(ASYNC_FINISH);
         }
	}
	
	/** Builds the user context from the input params
	 * @param params parameters
	 *  */
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
	 
	/** Service invocation with parameters and context provided.
	 * @param params parameters
	 * @param serviceName name of the NLP service to be invoked
	 * @param ctx User context
	 *  */
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
	                        //FIXME look up the purpose of declaration
	                    	@SuppressWarnings("unused")
							URL u = new URL( urls[j] );
	                        uriArray.getUriList().add( urls[j] );
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
	        SemanticServiceBroker agent = ServiceAgentSingleton.getInstance();
	        String result = agent.invokeService( serviceName, uriArray, stringArray, 0L, rtpArray, ctx );
	        System.out.println( "" );
	        System.out.println( result );
	        
	    }
	 
	 /** Service invocation when the resources are literals (texts) */
	 public void literalInvocation(){
		 	SemanticServiceBroker broker = ServiceAgentSingleton.getInstance();
	        UriList uriList = new UriList();
	        
	        for(int i=0; i < stringArray.getItem().size(); i++){
	        	uriList.getUriList().add( new String( "#literal" ) );  
	        	uriList.getUriList().add(stringArray.getItem().get(i));
	        }

	        String serviceResponse = null;
	        try
	        {
	            serviceResponse = broker.invokeService( serviceName, uriList, stringArray, 0L, rtpArray, new UserContext() );
	        }
	        catch( Exception connEx)
	        {
	            SemanticAssistantsStatusViewModel.addLog("Server not found. \nPlease check the Server Host and Port and if Server is Online");
	        	System.out.println("Server not found. \nPlease check the Server Host and Port and if Server is Online");
	            return;
	        }

	      /*// returns result in sorted by type
	      Vector<SemanticServiceResult> results = ClientUtils.getServiceResults( serviceResponse );
          if( results == null )
	            {
	                // Open document showing response message
	                System.out.println( "---------- No results retrieved in response message" );
	                return;
	            }

	            for( Iterator<SemanticServiceResult> it = results.iterator(); it.hasNext(); )
	            {
	                SemanticServiceResult current = it.next();
	                System.out.println(current.toString());
	            }
	        */  
	        new AnnotationParser(serviceResponse).parseXML();
	 }
	 
	 public void addLiteral(String literal){
		 stringArray.getItem().add(literal);
	 }

}
