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

/** Wiki Engine abstraction class.
 * @author Bahar Sateli
 */
public abstract class WikiEngine {

	/** Returns wiki's helper class.
	 * @return the wiki helper singleton object
	 */
	public abstract WikiHelper getHelper();

	/** Returns wiki's ontology keeper class. 
	 * @return the wiki ontology keeper singleton object
	 */
	public abstract WikiOntologyKeeper getOntologyKeeper();

	/** Returns wiki's parser class. 
	 * @return the wiki parser singleton object
	 */
	public abstract WikiParser getParser();
}
