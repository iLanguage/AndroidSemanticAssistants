package info.semanticsoftware.semassist.android.application;
import android.app.Application;
import android.content.Context;

/** The Semantic Assistants app application class.
 * @author Bahar Sateli
 */
public class SemAssistApp extends Application {
	/** Application static instance. */
	private static SemAssistApp instance;

	/** Provides a global static access to the application instance.
	 * @return class instance object */
	public static SemAssistApp getInstance() {
		return instance;
	}

	/** Provides a global access to the application context.
	 * @return application context object */
	public Context getContext(){
		return instance.getApplicationContext();
	}

	/** Initializes the class instance when the activity is created. */
	@Override
	public void onCreate() {
		instance = this;
		super.onCreate();
	}
}