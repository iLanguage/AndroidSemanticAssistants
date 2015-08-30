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
package info.semanticsoftware.semassist.android.business;

import java.util.HashMap;
import java.util.Map;

/** 
 * This class defines a structure to store annotation features.
 * 
 * @author Bahar Sateli
 * */
public class FeatureMap {

	/** This map stores the annotation features in form of (key.value) pairs */ 
	Map<String,String> featuresMap = new HashMap<String,String>();

	public FeatureMap(){
	}

	/** This method puts the input arguments into the map */
	public void put(String key, String value){
		featuresMap.put(key, value);
	}

	/** This method returns the map containing the annotation features
	 * 
	 * @return featuresMap the map containing the annotation features
	 * */
	public Map<String,String> getFeaturesMap(){
		return featuresMap;
	}
}
