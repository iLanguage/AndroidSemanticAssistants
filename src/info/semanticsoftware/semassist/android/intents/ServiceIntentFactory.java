package info.semanticsoftware.semassist.android.intents;

/** Enumeration class for service intents */
enum Intents {person_extractor};

/**
 * Command Factory class implements Factory Design Pattern.
 * @author Bahar Sateli
 * */
public class ServiceIntentFactory {

	/** 
	 * Private constructor since it is a utility class.
	 */
	private ServiceIntentFactory(){}

	/**
	 * Returns a concrete service object based on the intent action.
	 * @param action action retrieved from intent
	 * @return service object created by the factory
	 * */
	public static ServiceIntent getService(final String intentAction){
		// we are only interested in the last part of the action name
		String action = intentAction.substring(intentAction.lastIndexOf(".")+1);
		switch(Intents.valueOf(action.toLowerCase())){
		case person_extractor:
			return new PersonExtractorIntent();
		default:
			return null;
		}
	}
}
