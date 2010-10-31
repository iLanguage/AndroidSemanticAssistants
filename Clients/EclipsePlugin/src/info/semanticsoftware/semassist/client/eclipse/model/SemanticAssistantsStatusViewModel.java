package info.semanticsoftware.semassist.client.eclipse.model;

import info.semanticsoftware.semassist.client.eclipse.utils.Log;
import java.util.ArrayList;
import java.util.List;

/** 
 * This class nourishes the Semantic Assistants Status view class by providing an input for the SemanticAssistantsStatusViewContentProvider.
 * It has a singleton instance that carries the logs produced by the system.
 * 
 * @author Bahar Sateli
 * */
public class SemanticAssistantsStatusViewModel {
	/** Unique instance of the class */
	private static SemanticAssistantsStatusViewModel model;
	
	/** List of all the log messages */
	private static List<Log> logs = new ArrayList<Log>();

	/** Protected constructor to defeat instantiation */
	private SemanticAssistantsStatusViewModel() {
	}

	/** Global access point to the single instance. Returns the view model object */
	public static synchronized SemanticAssistantsStatusViewModel getInstance() {
		if (model != null) {
			return model;
		}
		model = new SemanticAssistantsStatusViewModel();
		return model;
	}
	
	/** This method returns all the logs produced by the system.
	 *  @return list of all the logs
	*/
	public List<Log> getLogs(){
		return logs;
	}
	
	/** This method wraps the argument string into a log instance and adds it to the logs list
	 * @param message the message to be displayed
	 */
	public static void addLog(String message){
		logs.add(new Log(message));
	}
}
