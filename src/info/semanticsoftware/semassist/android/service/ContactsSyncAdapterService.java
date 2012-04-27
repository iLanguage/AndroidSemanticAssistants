package info.semanticsoftware.semassist.android.service;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ContactsSyncAdapterService extends Service{

	private static SyncAdapterImpl sSyncAdapter = null;
	@SuppressWarnings("unused")
	private static ContentResolver mContentResolver = null;

	public ContactsSyncAdapterService(){
		super();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		IBinder ret = null;
		ret = getSyncAdapter().getSyncAdapterBinder();
		return ret;
	}

	private SyncAdapterImpl getSyncAdapter(){
		if(sSyncAdapter == null){
			sSyncAdapter = new SyncAdapterImpl(this);
		}
		return sSyncAdapter;
	}

	private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter{
		private Context mContext;
		public SyncAdapterImpl(Context context) {
			super(context, true);
			mContext = context;
		}

		@Override
		public void onPerformSync(Account account, Bundle extras,
				String authority, ContentProviderClient provider,
				SyncResult syncResult) {
			try {
				ContactsSyncAdapterService.performSync(mContext, account, extras, authority, provider, syncResult);
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			}
		}
	}

	private static void performSync(Context context, Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) throws OperationCanceledException{
		mContentResolver = context.getContentResolver();
		Log.i("Sync", "performSync: " + account.toString());
		//TODO implement the sync once we have an actual sync server
	}
}
