package info.semanticsoftware.semassist.server;

import info.semanticsoftware.semassist.server.util.Logging;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Class is used to Maintain and manage the pipelines in memory and by the thread pools
 * this class is a singleton.
 *  
 * 		GatePipelineRegistery reference to itself
 * 		idService List of the services
 * 		pipeMap maps the pipelines to the threads
 *
 */

public class GatePipelineRegistery {
	private static GatePipelineRegistery gpr=null; //single declaration
	private ArrayList<ServiceStatus> idService = new ArrayList<ServiceStatus>(); //needed for tracking the status of each pipeline in memory
	private HashMap<String,PipelineThreadInfo> pipeMap = new HashMap<String,PipelineThreadInfo>(); //maps each pipeline to its individual thread
		
	
	private GatePipelineRegistery(){//default PRIVATE constructor
	}
	
	/**
	 * method will verify the pipeline registry for pipelines that have completed their task and no longer need
	 * to be loaded in memory.
	 * @param pipelinename the name used to identify the pipeline name
	 * @return integer value used to identify the position in the registry of the first EOF pipeline
	 * */
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
	/**
	 * method will iterate through the Registry to identify the terminal pipelines.
	 * These pipelines are the ones that have finished executing and have no longer any use
	 * They will not get cleared they are just being identified should the registry need
	 * to be cleaned
	 * */

	public void markUnusedPipileinesRegistry(){
		int settingcount=0; //place holder for the number of pipelines that are wanted in memory
		int registrycount=0; //place holder for the number of pipelines that are currently in memory
		for(PipelineThreadInfo ps:pipeMap.values()){ //iterate through the pipelines
			settingcount=ps.getMaxConcurrent(); //Retrieve the max allowed number pipelines
			registrycount=getPipelineCount(ps.getPipelineName()); //Retrieve the current number of pipelines
			if(settingcount<registrycount){ //verify if the max allowed has been surpassed by the current
				for(ServiceStatus ss:idService){ //iterate through the pipeline registry threads
					if(ss.getGateResourceName().equals(ps.getPipelineName())){//verify if the pipeline name matches
						if(ss.getStatus()==ServiceStatus.STATUS_INACTIVE){ //verify that it is currently not being used by a process
							ss.setToBeTerminated(true);//set the pipeline to be terminated during the clean event
							if(settingcount >= --registrycount){ //verify that we no longer surpass the max allowed
								break; //exit for loops
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * method will return the position of the pipeline in memory that should be terminated. 
	 * @return integer value indicating the position of the pipelines that need to be terminated
	 * */
	
	public int getTerminateServicePosition(){
    	int servicePosition=-1; //start position (indicates nothing found)
    	for(int idx=0;idx<idService.size();idx++){  //for loop is used due to the small size
        	if(idService.get(idx).isToBeTerminated()){ //verify if it should be terminated 
        		servicePosition = idx;//assign the position
        		idx=idService.size();//exit the loop
        	}
        }
        return servicePosition;//return the position
	}
	
	/**
	 * method will return the number of the pipelines dedicated to memory.
	 * This does not indicate how many have been loaded to memory but only the space that they will take
	 * @return integer value indicating the size of registry
	 * */
	
	public int getPipelineMapperCount(){
		return pipeMap.size();//return size of hash map
	}
	
	/**
	 * method will return the count of the requested pipeline currently in memory. 
	 * @param pipelinename name the requested pipeline
	 * @return integer value indicating the size of registry
	 * */
	
	public int getPipelineCount(String pipelinename){
		int ct=0;//initial count 
		for(ServiceStatus ss:idService){//iterate through the status registry
			if(ss.getGateResourceName().equals(pipelinename)){//validate the pipelines match
				ct++;//increase the count
			}
		}
		return ct;//return the value
	}
	
	/**
	 * method will add the thread info to the memory mapper 
	 * @param PipelineThreadInfo contains the necessary information for the pipeline to execute as a thread
	 * */
	
	public void addPipelineThreadInfo(PipelineThreadInfo ps){
		pipeMap.put(ps.getPipelineName(), ps);
	}
	
	/**
	 * method will add the thread info to the memory mapper 
	 * @param PipelineThreadInfo contains the necessary information for the pipeline to execute as a thread
	 * */
	
	public PipelineThreadInfo getPipelineThreadInfo(String pipelineName){
		return pipeMap.get(pipelineName);
	}
	
	/**
	 * singleton handler.  static method that will either return an existing instance of the class or
	 * instantiate and then return the class instance  
	 * @return  GatePipelineRegitery
	 * */
	
	public static GatePipelineRegistery getInstance(){//return current instance of the Regsiter
		if(gpr==null){//if it does not exist then create it
			gpr = new GatePipelineRegistery();
		}
		return gpr;
	}
	
	/**
	 * Method to clear all registry entries 
	 * */
	public void clear(){//clear the register
		idService.clear();
	}
	/**
	 * Method that returns the size of the register
	 * @return integer, size of the register 
	 * */
	public int getCurrentRegisterSize(){//return the register size
		return idService.size();
	}
	/**
	 * Method adds a newly created process to the registry
	 * @param _serviceName : the name of the service being added to the registry
	 * @param _gateService : The actual instance of the gate service
	 * @param status :  whether it is active(1) or inactive(0)
	 * @param _pti : object holds the thread parameters for the specific service
	 * @param terminate: true or false should the thread be terminated after execution.
	 * @return integer, new size of the register 
	 * */
	public int addGateProcess(String _serviceName, Object _gateService, int status, PipelineThreadInfo _pti, boolean terminate){//add the service status 
		idService.add(new ServiceStatus(_serviceName,_gateService, status, _pti,terminate));
		return idService.size()-1;
	}
	
	/**
	 * Method removes the process from the registry
	 * @param _serviceName : the name of the service being removed from the registry
	 * */
	
	public void removeGateProcess(String _serviceName){//remove the service OVERLOADED
		idService.remove(getInactiveServicePosition(_serviceName));
	}
	/**
	 * Method removes the process from the registry
	 * @param _servicePosition : the position of the service being removed from the registry
	 * */	
	public void removeGateProcess(int _servicePosition){//remove the service  OVERLOADED
		idService.remove(_servicePosition);
	}
	/**
	 * Method will return the position of the gate service instance
	 * @param _service : the instance of the gate process
	 * @return gets the position of the requested service
	 * */
	public int getServicePosition(Object _service){ //get the actual position of the service object
    	int servicePosition=-1;
    	for(int idx=0;idx<idService.size();idx++){  //for loop is used due to the small size
        	if(idService.get(idx).getGatePipeline().equals(_service)){
        		servicePosition = idx;
        		idx=idService.size();
        	}
        }
        return servicePosition;
	}
	/**
	 * Method will return the position of the first process with the name requested that is inactive
	 * @param _serviceName : The name of the service we are requesting
	 * @return gets the position of the requested service
	 * */
		
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
	
	/**
	 * Method will return all pipelines that are currently executing.
	 * 
	 * @return The string value returned is in the following format
	 * 			{Active:Inactive}|{name of the resource executing};
	 * 
	 * When the string value is returned it must be split on ';' to extract into pipeline array
	 * and each entry must then be split on '|' as to get the activity and the name
	 * 
	 * */
	
	public String getActivePipelines(){
		String as = "";
		for(int idx=0;idx<idService.size();idx++){
        		as += idService.get(idx).getStatusDescription();
        		as += "|" + idService.get(idx).getGateResourceName();
        		as += ";";
        }
    	return as;
	}
	/**
	 * Method will return the position of the first process that is inactive
	 * 
	 * @return gets the position of the first inactive service
	 * */
	
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
	
	/**
	 * Method will return the ServiceStatus Object
	 * @param _servciePosition : position of the service in the List
	 * @return ServiceStatus object
	 * */
	public ServiceStatus getGateProcessServiceStatus(int _servicePosition){ //return the servicestatus object
		return idService.get(_servicePosition);
	}
	
	/**
	 * Method will flag service as active
	 * @param _servciePosition : position of the service in the List
	 * */
	
	public void ActivateGateProcess(int _servicePosition){
		idService.get(_servicePosition).setStatus(ServiceStatus.STATUS_ACTIVE);
	}

	/**
	 * Method will flag service as inactive
	 * @param _servciePosition : position of the service in the List
	 * */
	
	public void InActivateGateProcess(int _servicePosition){
		idService.get(_servicePosition).setStatus(ServiceStatus.STATUS_INACTIVE);
	}
}
