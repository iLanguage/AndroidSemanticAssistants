package info.semanticsoftware.semassist.android.application;
import android.app.Application;
import android.content.Context;

public class SemAssistApp extends Application {
    private static SemAssistApp instance;

    public static SemAssistApp getInstance() {
        return instance;
    }

    public Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}