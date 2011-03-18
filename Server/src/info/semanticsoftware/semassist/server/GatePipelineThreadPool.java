package info.semanticsoftware.semassist.server;

import info.semanticsoftware.semassist.server.util.Logging;
import info.semanticsoftware.semassist.server.util.MasterData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GatePipelineThreadPool {
	private static GatePipelineThreadPool gptp=null; //single declaration
	private HashMap<String,ExecutorService> tpes = new HashMap<String,ExecutorService>();
	private ExecutorService nonfixedtpes = null;
	
	private GatePipelineThreadPool(){//default PRIVATE constructor
		Logging.log("Set the fixed thread pool: " + getFixedNumberOfNeededThreads());
		setUpThreadPoolMap();
		Logging.log("Set the non fixed thread pool: " + (MasterData.Instance().getServerThreadsAllowed()-getFixedNumberOfNeededThreads()));
		nonfixedtpes = Executors.newFixedThreadPool(MasterData.Instance().getServerThreadsAllowed()-getFixedNumberOfNeededThreads());
	}
	
	public static GatePipelineThreadPool getInstance(){
		if(gptp==null){//if it does not exist then create it
			gptp = new GatePipelineThreadPool();
		}
		return gptp;
	}
	
	public ExecutorService getThreadPool(String filename, boolean isComposite){
		
		ExecutorService e=null;
		if(!isComposite){
			for(String s: tpes.keySet()){
				Logging.log("key: " + s);
				
				String searchName =  new java.io.File(filename).getParent().replace("..", "");
				Logging.log("filename: " + searchName);
				if(s.indexOf(searchName)>=0){
					Logging.log("Using Fixed Thread Pool");
					e = tpes.get(s);
				}
			}
		}
		
		if(e == null){
			Logging.log("Using Non Fixed Thread Pool");
			e = nonfixedtpes;
		}
		return e;
	}
	
	public String submitNewThread(CallableServiceThread cst){
		
		Future<String> ftp = getThreadPool(cst.getAppFileName(),cst.isServiceComposite()).submit(cst);//send the thread to the thread pool and wait for it's completion    	
		String returnValue="";//used to store the return value from the thread
    	try{
    		returnValue = ftp.get(); //query the thread and get the result (String)
    	}catch(Exception e){
    		returnValue = e.getMessage(); 
    	}    	
    	return returnValue;
	}
	
	public boolean isAFixedService(String _service){
		boolean retVal=false;
		Logging.log("tested: " + _service);
		ArrayList<String[]> s = MasterData.Instance().getPipelineThreadProperties();
		for(String[] sa:s){
			Logging.log("test array value for thread " + sa[3]);
			if(sa[3].equalsIgnoreCase(_service)){
				retVal=true;
			}
		}
		return retVal;
	}
	
	public void setUpThreadPoolMap(){
		ArrayList<String[]> s = MasterData.Instance().getPipelineThreadProperties();
		for(String[] sa:s){
			Logging.log("Setup :" + sa[3] + "  number of threads: " + sa[1]);
			tpes.put(new java.io.File(sa[3]).getPath(), Executors.newFixedThreadPool(Integer.parseInt(sa[1])));
		}		
	}
	
	public int getFixedNumberOfNeededThreads(){
		ArrayList<String[]> s = MasterData.Instance().getPipelineThreadProperties();
		int x = 0;
		for(String[] sa:s){
			x+=Integer.parseInt(sa[1]);
		}
		return x;
	}
}
