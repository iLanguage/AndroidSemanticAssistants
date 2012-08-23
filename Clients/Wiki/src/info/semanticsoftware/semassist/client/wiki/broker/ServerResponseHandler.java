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
package info.semanticsoftware.semassist.client.wiki.broker;

import info.semanticsoftware.semassist.client.wiki.servlets.SemAssistServlet;

/**
 * This class handles incoming requests and decides
 * where the results should be stored.
 * @author Bahar Sateli
 * */
public class ServerResponseHandler {

	/** The destination wiki page. The target value can from any of three below.
	 * <ul>
	 * <li> "self", i.e., the same wiki engine az the resource
	 * <li> "other", i.e., another wiki page in the same engine
	 * <li> "otherwiki", i.e, a different wiki engine 
	 * </ul> 
	 */
	private static String target = null;

	/** The name of the destination wiki page. */
	private static String targetName = null;

	/** URL of the wiki engine. */
	private static String wikiAddress = null;

	/** Username for the bot to login to the wiki. */
	private static String wikiUser = null;

	/** Password for the bot to login to the wiki. */
	private static String wikiPass = null;

	/** Private class constructor. */
	private ServerResponseHandler(){
		//defeat instantiation for utility class
	}

	/**
	 * Writes the analysis results to the specified wiki page.
	 * @param responseContent NLP analysis results
	 * */
	public static void writeResponse(final String responseContent){
		try{
			if(target.equals("self")){
				SemAssistServlet.getWiki().getHelper().writeToSamePage(targetName, responseContent);
			}else if(target.equals("other")){
					//MWHelper.writeToOtherPage(targetName, _responseContent, MWHelper.getInstance());
				SemAssistServlet.getWiki().getHelper().writeToOtherPage(targetName, responseContent, false);
			}else if(target.equals("otherWiki")){
				// The function is the same, we just use a different bot
				//MWHelper.writeToOtherPage(targetName, _responseContent, MWHelper.getNewBot(wikiAddress, wikiUser, wikiPass));
				SemAssistServlet.getWiki().getHelper().writeToOtherPage(targetName, responseContent, true);
			}else{
				System.out.println("Undefined target option!");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/** Sets the wiki target name.
	 * @param input the wiki engine name
	 */
	public static void setTarget(final String input){
		target = input;
	}

	/** Sets the destination wiki page name.
	 * @param input the wiki page name
	 */
	public static void setTargetName(final String input){
		targetName = input;
	}

	/** Sets the wiki address URL.
	 * @param input wiki engine name
	 */
	public static void setWikiAddress(final String input){
		wikiAddress = input;
	}

	
	/** Sets the wiki bot username.
	 * @param input wiki bot username
	 * */
	public static void setWikiUser(final String input){
		wikiUser = input;
	}

	/** Sets the wiki bot password.
	 * @param input wiki bot password
	 * */
	public static void setWikiPass(final String input){
		wikiPass = input;
	}

	/** Gets the wiki address URL.
	 * @return the wiki address URL
	 * */
	public static String getWikiAddress(){
		return wikiAddress;
	}

	/** Gets the wiki bot username.
	 * @return wiki bot username
	 * */
	public static String getWikiUser(){
		return wikiUser;
	}

	/** Gets the wiki bot password.
	 * @return wiki bot password
	 * */
	public static String getWikiPass(){
		return wikiPass;
	}
}
