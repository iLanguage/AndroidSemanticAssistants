package info.semanticsoftware.semassist.server;

import info.semanticsoftware.semassist.server.util.Logging;
import info.semanticsoftware.semassist.server.util.MasterData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
/**
 * Class is used to manage and maintain the Thread pools
 * This class is a singleton
 * 		GatePipelineThreadPool holds a reference to itself
 * 		tpes maps the individuas thread pools to the pipelines
 * 		nonfixedtpes thread pool used for pipelines that have not been predefined 
 *
 */

public class GatePipelineThreadPool {
	private static GatePipelineThreadPool gptp=null; //singleton declaration
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
	/**
	 * Method returns the number of waiting threads for each one of the pools 
	 * @return string value containing all the queued threads
	 * 			{name of services}|{quantity of pipelines waiting};
	 * 
	 * When the string value is returned it must be split on ';' to extract into pipeline array
	 * and each entry must then be split on '|' as to get the activity and the name
	 * 
	 * */

	
	public String getThreadPoolQueueStatus(){
		String keyValue = "";
		String ths = "";
		ArrayList<String[]> s = MasterData.Instance().getPipelineThreadProperties();
		for(String key:tpes.keySet()){
			for(int idx=0;idx<s.size();idx++){
				try{
					if(s.get(idx) !=null){
						String[] ss = s.get(idx);
						if(ss != null){
							String sFile = new java.io.File(ss[3]).getPath();
							if(sFile.equalsIgnoreCase(key)){
								keyValue = s.get(idx)[0];
								idx = s.size();
							}
						}
					}
				}catch(Exception e){
					Logging.log("Catch error");
					keyValue = "N/A";
				}
			}
			ths += keyValue + "|" + ((ThreadPoolExecutor)tpes.get(key)).getQueue().size() + ";";
		}
		ths += "Undefined" + "|" + ((ThreadPoolExecutor)nonfixedtpes).getQueue().size() + ";";
		return ths;
	}
	/**
	 * Method returns thread pool for the requesting service 
	 * 
	 * @param filename : file name used to identify the application pipeline
	 * @return ThreadPool
	 * 
	 * */
	public ExecutorService getThreadPool(String filename){
		
		ExecutorService e=null;
		for(String s: tpes.keySet()){
			String searchName =  new java.io.File(filename).getParent().replace("..", "");
			if(s.indexOf(searchName)>=0){
				Logging.log("Using Fixed Thread Pool");
				e = tpes.get(s);
			}
		}
		
		if(e == null){
			Logging.log("Using Non Fixed Thread Pool");
			e = nonfixedtpes;
		}
		return e;
	}
	/**
	 * Method verifies if the service passed has been previous set by the properties file 
	 * 
	 * @param _service : name of service
	 * @return boolean (true,false)
	 * 
	 * */
	
	public boolean isAFixedService(String _service){
		boolean retVal=false;
		//Logging.log("tested: " + _service);
		ArrayList<String[]> s = MasterData.Instance().getPipelineThreadProperties();
		for(String[] sa:s){
			//Logging.log("test array value for thread " + sa[3]);
			if(sa[3].equalsIgnoreCase(_service)){
				retVal=true;
			}
		}
		return retVal;
	}
	/**
	 * Method reads data from the properties file and creates the different thread pools
	 * needed for the execution of the program and management of the requesting threads
	 * 
	 * */
	public void setUpThreadPoolMap(){
		ArrayList<String[]> s = MasterData.Instance().getPipelineThreadProperties();
		for(String[] sa:s){
			Logging.log("Setup :" + sa[0] + "  number of threads: " + sa[4]);//message to indicate what threads are being set up
			try{
				tpes.put(new java.io.File(sa[3]).getPath(), Executors.newFixedThreadPool(Integer.parseInt(sa[4])));
			}
			catch(Exception ex){
				System.out.println("Could not define Thread Pool Size for " + sa[0] + "\n the max.concurrent property is not correctly defined, defaulted to 1");
			}
		}		
	}
	/**
	 * Method returns a global count of all the maximum threads that will be allowed to execute
	 * this method is mainly used to validate the min max values of the global thread count property 
	 * 
	 * */
	
	public int getFixedNumberOfNeededThreads(){
		ArrayList<String[]> s = MasterData.Instance().getPipelineThreadProperties();
		int x = 0;
		for(String[] sa:s){
			x+=Integer.parseInt(sa[4]);
		}
		return x;
	}
}
