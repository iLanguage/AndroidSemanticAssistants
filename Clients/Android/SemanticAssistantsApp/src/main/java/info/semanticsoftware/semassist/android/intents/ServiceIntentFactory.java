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
package info.semanticsoftware.semassist.android.intents;

/** Enumeration class for service intents */
enum Intents {person_extractor, contact_finder};

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
		case contact_finder:
			return new ContactFinderIntent();
		default:
			return null;
		}
	}
}
