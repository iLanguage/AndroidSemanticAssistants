package info.semanticsoftware.semassist.server;

import info.semanticsoftware.semassist.server.util.GATERuntimeParameter;
import info.semanticsoftware.semassist.server.util.Logging;
import info.semanticsoftware.semassist.server.util.ServiceInfo;
import info.semanticsoftware.semassist.server.util.UserContext;
import java.util.Calendar;
import java.util.concurrent.Callable;

/*
 * Class is using threading
 * used to wrap the invokemethod from the SemanticServiceBroker class
 * 
 * Using the same variable call definitions and is necessary to have the visibility of the called function as protected
 */
public class CallableServiceThread implements Callable<String> {
    private long serviceNumber;
    
    private String serviceName;
    private URIList documents;
    private String[] literalDocs;
    private long connID;
    private GATERuntimeParameter[] gateParams;
    private UserContext userCtx; 
    private SemanticServiceBroker SSB;
    private ServiceInfo si;
    
    public CallableServiceThread(String _serviceName,URIList _documents,String[] _literalDocs,
    		long _connID,GATERuntimeParameter[] _gateParams, UserContext _userCtx, SemanticServiceBroker _SSB) {
    	
    	Calendar calendar = Calendar.getInstance();
    	java.util.Date now = calendar.getTime();
    	java.sql.Timestamp cts = new java.sql.Timestamp(now.getTime());

    	serviceNumber = cts.getTime(); //used to indicate the start time of the thread
    	serviceName=_serviceName;
    	documents=_documents;
    	literalDocs=_literalDocs;
    	connID=_connID;
    	gateParams=_gateParams;
    	userCtx=_userCtx;
    	SSB=_SSB;//setting a reference of the semanticservicebroker object (used to call the function)
    	si = SSB.getmAvailableServices().get( serviceName );
    }

    public String call() {
    	Logging.log("Starting Thread: #" + serviceNumber);

    	//the SemanticServiceBroker InvokeService call
    	String finalResult = SSB.invokeServiceCall(serviceName, documents, literalDocs, connID, gateParams, userCtx);
    	//to hold the result and return the value
        Logging.log("Stoping Thread: #" + serviceNumber);
    	Calendar calendar = Calendar.getInstance();
    	java.util.Date now = calendar.getTime();
    	java.sql.Timestamp cts = new java.sql.Timestamp(now.getTime());
        Logging.log("\n\n\n\n\n\n\nDURATION: " + (cts.getTime()-serviceNumber) + "\n\n\n\n\n\n\n");
    	return(finalResult);
    }
    
    public boolean isServiceComposite(){
    	return si.isConcatenation();
    }
    
    public String getAppFileName(){
    	return si.getAppFileName();
    }
    public String getService(){
    	return serviceName;
    }
}
