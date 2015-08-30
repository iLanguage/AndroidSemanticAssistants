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