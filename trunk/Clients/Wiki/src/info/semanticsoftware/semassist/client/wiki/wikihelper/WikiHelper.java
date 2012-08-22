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
 * This class provides abstract helper functions to communicate with a wiki engine.
 * @author Bahar Sateli 
 */
public abstract class WikiHelper {
	/** Returns the give page name's content as a string
	 * @param pageName wiki page name
	 * @return the pageNames's content
	 */
	public abstract String getPageContent(final String pageName);

	/** Sets the credential that is to be used by the wiki bot.
	 * @param address wiki address URL
	 * @param username bot's username
	 * @param password bot's password
	 */
	public abstract void setCredentials(final String address, final String username, final String password);

	/** Writes the given content into the target page within the same wiki engine as the resource.
	 * @param targetName wiki page name
	 * @param _content content to be written into the target page
	 */
	public abstract void writeToSamePage(final String targetName, String _content);

	/** Writes the given content into the target page, either within the wiki engine or
	  * to an external one (that is decided by the value of external input argument)
	  * @param targetName wiki page name
	  * @param _content content to be written
	  * @param external boolean variable to indicate wether the destination is an external wiki
	  */
	public abstract void writeToOtherPage(final String targetName, String _content, boolean external);

	/** Creates a wiki bot. */
	public abstract void createBot();

	/** Creates a wiki page for semantic entity retrieval.
	 * @param type entity type
	 */
	public abstract void createTypePage(final String type);
}
