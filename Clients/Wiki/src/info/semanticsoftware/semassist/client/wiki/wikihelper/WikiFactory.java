/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2012, 2013 Semantic Software Lab, http://www.semanticsoftware.info
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
package info.semanticsoftware.semassist.client.wiki.wikihelper;

/** 
 * Wiki Factory class implements Factory Design Pattern.
 * It creates a concrete wiki engine object based on the input.
 * @author Bahar Sateli
 */
public class WikiFactory {
	/** 
	 * Private constructor since it is a utility class.
	 */
	private WikiFactory(){}

	/**
	 * Returns a concrete wiki engine object based on the input.
	 * @param engine engine name retrieved from request
	 * @return WikiEngine wiki engine object created from the factory
	 * */
	public static WikiEngine getWiki(final String engine){
		return new MediaWiki();
	}
}
