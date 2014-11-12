/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2014 Semantic Software Lab, http://www.semanticsoftware.info
Rene Witte
Bahar Sateli

The Semantic Assistants architecture is free software: you can
redistribute and/or modify it under the terms of the GNU Affero General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package info.semanticsoftware.semassist.server.rest.utils;
/**
 * Utility class for constants used in this project.
 * @author Bahar Sateli
 * */
public class Constants {

	public enum MIME_TYPES {
		//JSON, XML;

		APPJSON("APPLICATION/JSON"), 
		JSON("JSON"),
		XML("XML"),
		APPXML("APPLICATION/XML"),
		APPXHTML("APPLICATION/XHTML+XML"),
		TEXT("TEXT/HTML"),
		TEXTXML("TEXT/XML"),;

		   private String value;
		   private MIME_TYPES(String value)
		   {
		      this.value = value;
		   }

		   public String toString()
		   {
		      return this.value; //This will return , # or +
		   }
	}
	/**
	 * Protected constructor.
	 */
	protected Constants(){
		// Hide utility class constructor
	}

	/** User authentication failure key. */
	public final static String AUHTENTICATION_FAIL = "AUHTENTICATION_FAIL";
}
