package info.semanticsoftware.semassist.client.eclipse.handlers;

import info.semanticsoftware.semassist.client.eclipse.model.Resource;
import info.semanticsoftware.semassist.client.eclipse.model.SemanticAssistantsStatusViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/** 
 * This class stores the information of one evaluation session at a time.
 * It provides a global access point to the protected arraylist of resources
 *  
 *  @author Bahar Sateli
 */

public class EvaluationSession {
	
	/** List of files to be evaluated */
	private static ArrayList<Resource> resources = new ArrayList<Resource>();
	
	/** A job instance to execute the service invocation */
	public static ServiceInvocationJob invocationJob;

	/** A protected constructor that exists only to defeat the instantiation */
	protected EvaluationSession(){
	      // Exists only to defeat instantiation.
	}
	
	/** 
	 * Global access point to the protected instance 
	 * @return A static reference to arraylist of resources
	 * */
	public static synchronized ArrayList<Resource> getResources(){
		if(resources == null){
			new EvaluationSession();
		}
		return resources;
	}
	
	/** Invokes the selected service 
	 * 
	 * @param serviceName Name of the service
	 * */
	public static void invoke(String serviceName){
		invocationJob = new ServiceInvocationJob(serviceName);
		invocationJob.setUser(true);
		for(int i=0; i < resources.size(); i++){
			try {
				InputStream inputStream = (InputStream) (resources.get(i).getFile()).getContents();
				InputStreamReader isr = new InputStreamReader(inputStream);
				BufferedReader br = new BufferedReader(isr);
				String theLine;
				String literal = "";
				while ((theLine = br.readLine()) != null) {
					if(literal.equals("")){
						literal = theLine;
					}else{
						literal = literal + System.getProperty("line.separator") + theLine;
					}
				}
				invocationJob.addLiteral(literal);		
			}catch (IOException e1) {
				System.err.println("Could not read from the stream.");
				e1.printStackTrace();
			
			} catch (CoreException e) {
				System.err.println("Could not read the content of files.");
				e.printStackTrace();
			}
		}
		
		invocationJob.addJobChangeListener(new JobChangeAdapter() {
	        public void done(IJobChangeEvent event) {
		            if (event.getResult().isOK()){
		            	// Great! Invocation was successful.
		            }else{
		  	     	  SemanticAssistantsStatusViewModel.addLog("Service invocation failed.");
		  	     	  System.err.println("Service invocation failed.");
		            }
	            }
	         });
		
		invocationJob.schedule();
	}
}