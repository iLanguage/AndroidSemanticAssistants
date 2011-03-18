package info.semanticsoftware.semassist.server;

import info.semanticsoftware.semassist.server.util.Logging;

import java.util.ArrayList;
import java.util.HashMap;
/*
 * Created this class as a Singleton
 * needed to maintain and manage the GATE pipelines used by the thread pool
 */
public class GatePipelineRegistery {
	private static GatePipelineRegistery gpr=null; //single declaration
	private ArrayList<ServiceStatus> idService = new ArrayList<ServiceStatus>();
	private HashMap<String,PipelineThreadInfo> pipeMap = new HashMap<String,PipelineThreadInfo>();
		//array list to hold the processes
	
	private GatePipelineRegistery(){//default PRIVATE constructor
	}
	
	
	public int getEndOfLifePipelinePosition(String pipelinename){
		//this function will search for a pipeline that is marked to be terminated and that is inactive
		//it will look for the first pipeline that does not match the passed pipeline name
		//if none are found it will return the pipeline name position that can be terminated
		int retunPosition=-1; //nothing found
		for(int retPos=0;retPos<idService.size();retPos++){ //loop through registry
			ServiceStatus ss = idService.get(retPos); //get ServiceStatus Object
			if(ss.isToBeTerminated() && //is it terminated (not necessarily inactive) 
					ss.getStatus() == ServiceStatus.STATUS_INACTIVE){ //is it inactive
				retunPosition=retPos; //store the position
				if(!ss.getGateResourceName().equals(pipelinename)){
					retPos = idService.size(); //if it does not match the current pipeline name
				}
			}
		}
		return retunPosition;
	}
	
	public void markUnusedPipileinesRegistry(){
		int settingcount=0;
		int registrycount=0;
		int idx=0;
		int activeCT=0;
		for(PipelineThreadInfo ps:pipeMap.values()){
			settingcount=ps.getMaxConcurrent();
			registrycount=getPipelineCount(ps.getPipelineName());
			if(settingcount<registrycount){
				for(ServiceStatus ss:idService){
					Logging.log("ss:"+ss.getGateResourceName() + " & ps:" + ps.getPipelineName());
					if(ss.getGateResourceName().equals(ps.getPipelineName())){
						if(ss.getStatus()==ServiceStatus.STATUS_INACTIVE){
							ss.setToBeTerminated(true);
							if(settingcount >= --registrycount){
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public int getTerminateServicePosition(){
    	int servicePosition=-1;
    	for(int idx=0;idx<idService.size();idx++){  //for loop is used due to the small size
        	if(idService.get(idx).isToBeTerminated()){
        		servicePosition = idx;
        		idx=idService.size();
        	}
        }
        return servicePosition;
	}
	
	public int getPipelineMapperCount(){
		return pipeMap.size();
	}
	
	public int getPipelineCount(String pipelinename){
		int ct=0;
		for(ServiceStatus ss:idService){
			if(ss.getGateResourceName().equals(pipelinename)){
				ct++;
			}
		}
		return ct;
	}
	
	public void addPipelineThreadInfo(PipelineThreadInfo ps){
		pipeMap.put(ps.getPipelineName(), ps);
	}
	
	public PipelineThreadInfo getPipelineThreadInfo(String pipelineName){
		return pipeMap.get(pipelineName);
	}
	
	public static GatePipelineRegistery getInstance(){//return current instance of the Regsiter
		if(gpr==null){//if it does not exist then create it
			gpr = new GatePipelineRegistery();
		}
		return gpr;
	}
	
	public void clear(){//clear the register
		idService.clear();
	}
	
	public int getCurrentRegisterSize(){//return the register size
		return idService.size();
	}
	
	public int addGateProcess(String _serviceName, Object _gateService, int status, PipelineThreadInfo _pti, boolean terminate){//add the service status 
		idService.add(new ServiceStatus(_serviceName,_gateService, status, _pti,terminate));
		return idService.size()-1;
	}
	
	public void removeGateProcess(String _serviceName){//remove the service OVERLOADED
		idService.remove(getInactiveServicePosition(_serviceName));
	}
	
	public void removeGateProcess(int _servicePosition){//remove the service  OVERLOADED
		idService.remove(_servicePosition);
	}
	
	public int getServicePosition(Object _service){ //get the actual position of the servcie object
    	int servicePosition=-1;
    	for(int idx=0;idx<idService.size();idx++){  //for loop is used due to the small size
        	if(idService.get(idx).getGatePipeline().equals(_service)){
        		servicePosition = idx;
        		idx=idService.size();
        	}
        }
        return servicePosition;
	}
		
	public int getInactiveServicePosition(String _serviceName){//return the first service with the at name that is inactive
    	int servicePosition=-1;
    	for(int idx=0;idx<idService.size();idx++){
    		_serviceName = _serviceName.replace("..", "");    		
        	if(idService.get(idx).getServiceName().toUpperCase().indexOf(_serviceName.toUpperCase())>=0 &&
        			idService.get(idx).getStatus() == ServiceStatus.STATUS_INACTIVE){
        		servicePosition = idx;
        		idx=idService.size();
        	}
        }
        return servicePosition;
	}
	
	public int getInactiveServicePosition(){//return the first service that is inactive
    	int servicePosition=-1;
    	for(int idx=0;idx<idService.size();idx++){
        	if(idService.get(idx).getStatus() == ServiceStatus.STATUS_INACTIVE){
        		servicePosition = idx;
        		idx=idService.size();
        	}
        }
        return servicePosition;
	}
	
	public ServiceStatus getGateProcessServiceStatus(int _servicePosition){ //return the servicestatus object
		return idService.get(_servicePosition);
	}
	
	public void ActivateGateProcess(int _servicePosition){
		idService.get(_servicePosition).setStatus(ServiceStatus.STATUS_ACTIVE);
	}
	public void InActivateGateProcess(int _servicePosition){
		idService.get(_servicePosition).setStatus(ServiceStatus.STATUS_INACTIVE);
	}
}
