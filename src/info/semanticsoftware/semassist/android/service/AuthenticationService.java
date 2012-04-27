package info.semanticsoftware.semassist.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticationService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		return new SemAssistAuthenticator(this).getIBinder();
	}
}
