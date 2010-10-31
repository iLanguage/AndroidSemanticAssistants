package info.semanticsoftware.semassist.client.eclipse.model;

import info.semanticsoftware.semassist.client.eclipse.handlers.EvaluationSession;
import java.util.ArrayList;
import java.util.List;

/** 
 * This class nourishes the Semantic Assistants view class by providing an input for the SemanticAssistantsViewContentProvider.
 * It has a singleton instance that carries the resources being evaluated by the selected semantic service.
 * 
 * @author Bahar Sateli
 * */
public class SemanticAssistantsViewModel {

	/** Unique instance of the class */
	private static SemanticAssistantsViewModel model;

	/** Protected constructor to defeat instantiation */
	private SemanticAssistantsViewModel() {
	}

	/** Global access point to the single instance. 
	 * @return the view model object */
	public static synchronized SemanticAssistantsViewModel getInstance() {
		if (model != null) {
			return model;
		}
		model = new SemanticAssistantsViewModel();
		return model;
	}
	
	/** Returns the result of the service invocation
	 * @return a list containing the result instances
	 *  */
	public List<Result> getResults(){
		List<Result> results = new ArrayList<Result>();
		if(EvaluationSession.getResources().size() == 0){
			System.err.println("No resources selected.");
			results.add(new Result("No files selected","","", "" ,"","" , "", "", "","",null));
		}else{
			for(int i=0; i < EvaluationSession.getResources().size(); i++){
				for(int j=0; j < EvaluationSession.getResources().get(i).getAnnotations().size(); j++){
					results.add(new Result(EvaluationSession.getResources().get(i).getAnnotations().get(j).getID(), EvaluationSession.getResources().get(i).getFile().getProject().getName(), EvaluationSession.getResources().get(i).getFile().getName(), EvaluationSession.getResources().get(i).getAnnotations().get(j).getContent(), EvaluationSession.getResources().get(i).getAnnotations().get(j).getType(), EvaluationSession.getResources().get(i).getAnnotations().get(j).getStart(), EvaluationSession.getResources().get(i).getAnnotations().get(j).getEnd(), (Resource.getFileLocation(EvaluationSession.getResources().get(i).getFile()).toString()), EvaluationSession.getResources().get(i).getAnnotations().get(j).getFeatures(),  EvaluationSession.getResources().get(i).getFile().getProjectRelativePath().toString(), EvaluationSession.getResources().get(i).getAnnotations().get(j).getFeatureMap()));                                     
				}
			}	
		}	
		
		return results;
	}
}
