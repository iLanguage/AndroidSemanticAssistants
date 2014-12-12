/*
* Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants
* 
* Copyright (C) 2014 Semantic Software Lab, http://www.semanticsoftware.info
* Rene Witte
* Bahar Sateli
* 
* This file is part of the Semantic Assistants architecture, and is 
* free software, licensed under the GNU Lesser General Public License 
* as published by the Free Software Foundation, either version 3 of 
* the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
