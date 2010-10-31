package info.semanticsoftware.semassist.client.eclipse.handlers;

import org.eclipse.jface.dialogs.IInputValidator;

/** This class helps to validate inputs for Semantic Assistants service settings
 * @author Bahar Sateli
 *  */
public class ServiceSettingsValidator implements IInputValidator {
	
	/** The source passing the argument to be validated */
	public static String source;

  /**
   * Validates the String. Returns null for no error, or an error message
   * 
   * @param newText the String to validate
   * @return String
   */
	@Override
	public String isValid(String newText) {
		  if(newText.isEmpty()){
			  return null;
		  }
	    
		 if(source.equals("port")){
			 try{  
			        Integer.parseInt(newText);  
			        return null;  
			     }catch( Exception e){  
			        return "Port value must be numerical";  
			     } 
		 }else{
				return null; 
		
		 }
	  
	}

}
