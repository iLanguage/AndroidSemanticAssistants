package info.semanticsoftware.semassist.server;

import info.semanticsoftware.semassist.server.util.Logging;

import java.util.ArrayList;
/*
 * Created this class as a Singleton
 * needed to maintain and manage the GATE pipelines used by the thread pool
 */
public class GateProcessRegister {
	private static GateProcessRegister gpr=null; //single declaration
	private ArrayList<ServiceStatus> idService = new ArrayList<ServiceStatus>();
		//array list to hold the processes
	
	private GateProcessRegister(){//default PRIVATE constructor
	}
	
	public static GateProcessRegister getInstance(){//return current instance of the Regsiter
		if(gpr==null){//if it does not exist then create it
			gpr = new GateProcessRegister();
		}
		return gpr;
	}
	
	public void clear(){//clear the register
		idService.clear();
	}
	
	public int getCurrentRegisterSize(){//return the register size
		return idService.size();
	}
	
	public int addGateProcess(String _serviceName, Object _gateService){//add the service status 
		idService.add(new ServiceStatus(_serviceName,_gateService,ServiceStatus.STATUS_ACTIVE));
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
        	if(idService.get(idx).getGateService().equals(_service)){
        		servicePosition = idx;
        		idx=idService.size();
        	}
        }
        return servicePosition;
	}
	
	public int getInactiveServicePosition(String _serviceName){//return the first service with the at name that is inactive
    	int servicePosition=-1;
    	for(int idx=0;idx<idService.size();idx++){
        	if(idService.get(idx).getServiceName().equals(_serviceName) &&
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
