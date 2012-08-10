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

import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.result.Annotation;
import info.semanticsoftware.semassist.csal.result.AnnotationVector;
import info.semanticsoftware.semassist.csal.result.AnnotationVectorArray;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;
import info.semanticsoftware.semassist.client.wiki.broker.ServerResponseHandler;
import info.semanticsoftware.semassist.client.wiki.broker.ServiceInvocationHandler;
import info.semanticsoftware.semassist.client.wiki.command.InvokeCommand;
import info.semanticsoftware.semassist.client.wiki.servlets.SemAssistServlet;
import info.semanticsoftware.semassist.client.wiki.utils.ConsoleLogger;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * This is the MediaWiki engine helper class. 
 * It is aware of how to connect to the wiki database, read from it and write the results back.
 * @author Bahar Sateli 
 * */
public class MediaWikiHelper extends WikiHelper{

	/** MediaWiki Bot object. */
	private static MediaWikiBot bot = null;

	/** Wiki URL. */
	private String wikiAddress;

	/** Wiki Bot username. */
	private String wikiUser;

	/** Wiki Bot password. */
	private String wikiPass;

	/** Annotation case flag. */
	static boolean annotationCase = false;

	/** Boundless Annotation case flag. */
	static boolean boundlessAnnotationCase = false;

	/** File case flag. */
	static boolean fileCase = false;

	/** Document case flag. */
	static boolean documentCase = false;

	/** A string object to hold file and document contents. */
	static String outputString = "";

	/** A map object to consolidate each document annotations. */
	private static MultiMap annotsMap  = null;

	/** 
	 * Returns the raw markup of a wiki page as a string.
	 * @param pageName page name
	 * @return String page markup content
	 * */
	@Override
	public String getPageContent(String pageName) {
		createBot();
		SimpleArticle sa = null;
		try {
/*		   bot = new MediaWikiBot("http://localhost/mediawiki-1.16/index.php");
		   bot.login("wikisysop", "adminpass");
		   bot = new MediaWikiBot("http://localhost/smartwiki/index.php");
		   bot.login("admin", "bahar");
			bot = new MediaWikiBot(wikiAddress);
			bot.login(wikiUser, wikiPass);*/
			if(bot.isLoggedIn()){
				ConsoleLogger.log("Bot successfully logged into the wiki.");
				//TODO remove debugging code
				System.out.println("Bot successfully logged in to the wiki...");
				ConsoleLogger.log("Retrieving page content...");
				//TODO remove debugging code
			   System.out.println("Retrieving page content: " + pageName);
			   sa = new SimpleArticle(bot.readContent(pageName));
			   return sa.getText();
			}else{
				ConsoleLogger.log("Bot could not log in to the wiki. Please check the credentials.");
				//TODO remove debugging code
				System.out.println("Bot could not log in to the wiki. Please check the credentials.");
			}
	   } catch (ActionException e) {
		e.printStackTrace();
	   } catch (ProcessException e) {
		e.printStackTrace();
	   }
		return "";
	}

	/** Sets the wiki credentials to be used by bot.
	 * @param address URL of the wiki engine
	 * @param username bot username
	 * @param password bot password
	*/
	@Override
	public void setCredentials(String address, String username, String password) {
		wikiAddress = address;
		wikiUser = username;
		wikiPass = password;
	}

	/**
	* Writes the input content to the same wiki page as the source.
	* @param targetName scope of the target
	* @param content content to write
	*/
	@Override
	public void writeToSamePage(final String targetName, final String content) {
		createBot();
		String targetPrefix = null;
		String log = "";
		if(targetName.equals("body")){

			log = "Results will be written in the body of the same page as the resource.";
			targetPrefix = "";
		}else if(targetName.equals("talk")){

			log = "Results will be written in the talk of the same page as the resource";
			targetPrefix = "Talk:";
		}else{
			System.out.println("Wrong targetName for \"self\" target:" + targetName);
		}
		parseResults(content);

        if(annotationCase){	
        try{
            @SuppressWarnings("unchecked")
    		Set<String> documents = annotsMap.keySet();
            for(String document: documents){
            	//String docToken = ServiceInvocationHandler.tokens[Integer.parseInt(document)];
            	// complete URL
            	String docToken = document;
            	//String docLocalURL = docToken.substring(docToken.lastIndexOf("/")+1);
    			//FIXME this is hack for MediaWiki, should be fixed
            	int temp = docToken.lastIndexOf("index.php") + "index.php".length() + 1;
    			// local name
            	String docLocalURL = docToken.substring(temp);
            	String resultsToWrite = getAnnotationResult(document, docToken, docLocalURL);

            	try{

            		//int tokenNum = Integer.parseInt(document);
            		//String pageURL = ServiceInvocationHandler.tokens[tokenNum];
            		//String pageURL = document;
    				//int index = pageURL.lastIndexOf("/");
            		///int index = pageURL.lastIndexOf("index.php") + "index.php".length() + 1;
    				///pageURL = pageURL.substring(index);
            		System.out.println("DEBUGG:: writing to " + docLocalURL);
    				Article article = new Article(bot, targetPrefix.concat(docLocalURL));
    				//FIXME: without this the bot replaces the text, why? I have no idea!
    				String pageContent = article.getText();

    				String finale = SemAssistServlet.getWiki().getParser().updateTemplate(pageContent, InvokeCommand.serviceName, docLocalURL, resultsToWrite.toString());

    				// Clear the article
    				article.setText("");

    				article.setText(finale);

    				article.save();
    			} catch (NumberFormatException e){
    				e.printStackTrace();
    			}catch (ActionException e) {
    				e.printStackTrace();
    			} catch (ProcessException e) {
    				e.printStackTrace();
    			} catch (Exception e){
    				e.printStackTrace();
    			}
            }
            annotsMap.clear();
	        }catch(NumberFormatException e){
	        	System.out.println(e.getMessage());
	        }
	        ConsoleLogger.log(log);
	        annotationCase = false;
            return;
        }
        if(boundlessAnnotationCase){
        	try{
        		//FIXME fix the index
            	String docToken = ServiceInvocationHandler.tokens[0];
            	int temp = docToken.lastIndexOf("index.php") + "index.php".length() + 1;
            	//String docLocalURL = docToken.substring(docToken.lastIndexOf("/")+1);
            	String docLocalURL = docToken.substring(temp);
            	String resultsToWrite = getBoundlessResult(docToken,docLocalURL);

        		//int index = docToken.lastIndexOf("/");
				//String pageURL = docToken.substring(index + 1);
        		Article article = new Article(bot, targetPrefix.concat(docLocalURL));
        		//FIXME: without this the bot replaces the text, why? I have no idea!
				String pageContent = article.getText();

				String cleanedContent = SemAssistServlet.getWiki().getParser().updateTemplate(pageContent, InvokeCommand.serviceName, docLocalURL, resultsToWrite);

				// Clear the article
				article.setText("");
				// Write back the content with updated SA templates
				article.setText(cleanedContent);
				article.save();
			} catch (NumberFormatException e){
				e.printStackTrace();
			}catch (ActionException e) {
				e.printStackTrace();
			} catch (ProcessException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
	        ConsoleLogger.log(log);
			boundlessAnnotationCase = false;
        	return;
        }
        if(fileCase){
        	try{
        		Article article = new Article(bot, InvokeCommand.serviceName);
        		//FIXME: without this the bot replaces the text, why? I have no idea!
				@SuppressWarnings("unused")
				String pageContent = article.getText();
				// Clear the article
				article.setText("");
				// Write back the content with updated SA templates
				article.setText(outputString);
				article.save();
			} catch (NumberFormatException e){
				e.printStackTrace();
			}catch (ActionException e) {
				e.printStackTrace();
			} catch (ProcessException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
			ConsoleLogger.log("Results will be written in: " + InvokeCommand.serviceName);
			outputString = "";
        	fileCase = false;
        	return;
        }
        if(documentCase){
        	documentCase = false;
        	return;
        }
	}

	/**
	* Writes the input content to a specified wiki page.
	* @param targetName wiki page name
	* @param _content content to write
	* @param extenral shows whether target is another wiki engine
	*/
	@Override
	public void writeToOtherPage(String targetName, String _content, boolean external) {
		MediaWikiBot wikibot = null;
		if(external){
			wikibot = createBot(ServerResponseHandler.getWikiAddress(), ServerResponseHandler.getWikiUser(), ServerResponseHandler.getWikiPass());
		}else{
			createBot();
			wikibot = bot;
		}
		parseResults(_content);
        if(annotationCase){
        	try{
            @SuppressWarnings("unchecked")
    		Set<String> documents = annotsMap.keySet();
            for(String document: documents){
            	//String docToken = ServiceInvocationHandler.tokens[Integer.parseInt(document)];
            	//String docLocalURL = docToken.substring(docToken.lastIndexOf("/")+1);
            	//String resultsToWrite = getAnnotationResult(document, docToken, docLocalURL);
            	String docToken = document;
            	int temp = docToken.lastIndexOf("index.php") + "index.php".length() + 1;
            	//String docLocalURL = docToken.substring(docToken.lastIndexOf("/")+1);
            	String docLocalURL = docToken.substring(temp);
            	String resultsToWrite = getAnnotationResult(document, docToken, docLocalURL);
            	try{
            		Article article = new Article(wikibot, targetName);

            		//FIXME: without this the bot replaces the text, why? I have no idea!
    				String content = article.getText();
    				String finale = SemAssistServlet.getWiki().getParser().updateTemplate(content, InvokeCommand.serviceName, docLocalURL, resultsToWrite.toString());

    				// Clear the article
    				article.setText("");

    				article.setText(finale);

    				article.save();
    			} catch (NumberFormatException e){
    				e.printStackTrace();
    			}catch (ActionException e) {
    				e.printStackTrace();
    			} catch (ProcessException e) {
    				e.printStackTrace();
    			} catch (Exception e){
    				e.printStackTrace();
    			}
            }
            annotsMap.clear();
        	}catch(NumberFormatException e){
        		System.out.println(e.getMessage());
        	}
    		ConsoleLogger.log("Results will be written in: " + targetName);
        	annotationCase = false;
            return;
        }
        if(boundlessAnnotationCase){
            //FIXME fix the index
        	String docToken = ServiceInvocationHandler.tokens[0];
        	int temp = docToken.lastIndexOf("index.php") + "index.php".length() + 1;
        	//String docLocalURL = docToken.substring(docToken.lastIndexOf("/")+1);
        	String docLocalURL = docToken.substring(temp);
        	String resultsToWrite = getBoundlessResult(docToken,docLocalURL);
        	try{
        		Article article = new Article(wikibot, targetName);
        		//FIXME: without this the bot replaces the text, why? I have no idea!
				String content = article.getText();
				String cleanedContent = SemAssistServlet.getWiki().getParser().updateTemplate(content, InvokeCommand.serviceName, docLocalURL, resultsToWrite);
				// Clear the article
				article.setText("");
				// Write back the content with updated SA templates
				article.setText(cleanedContent);
				article.save();
			} catch (NumberFormatException e){
				e.printStackTrace();
			}catch (ActionException e) {
				e.printStackTrace();
			} catch (ProcessException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
    		ConsoleLogger.log("Results will be written in: " + targetName);
			outputString = "";
			boundlessAnnotationCase = false;
        	return;
        }
        if(fileCase){
        	try{
        		Article article = new Article(bot, InvokeCommand.serviceName);
        		//FIXME: without this the bot replaces the text, why? I have no idea!
				@SuppressWarnings("unused")
				String content = article.getText();
				// Clear the article
				article.setText("");
				// Write back the content with updated SA templates
				article.setText(outputString);
				article.save();
			} catch (NumberFormatException e){
				e.printStackTrace();
			}catch (ActionException e) {
				e.printStackTrace();
			} catch (ProcessException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
			ConsoleLogger.log("Results will be written in: " + InvokeCommand.serviceName);
			outputString = "";
        	fileCase = false;
        	return;
        }
        if(documentCase){
        	documentCase = false;
        	return;
        }
	}

	/******************************/
	/**** Should be refactored ****/
	/******************************/

	public static String getAnnotationsString(SemanticServiceResult current){
	       
		   HashMap<String, AnnotationVectorArray> annotationsPerDocument = new HashMap<String, AnnotationVectorArray>();

        // Keys are document IDs or URLs
        HashMap<String, AnnotationVector> map = current.mAnnotations;
        Set<String> keys = map.keySet();

        for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); )
        {
            String docID = it2.next();

            if( annotationsPerDocument.get( docID ) == null )
            {
                annotationsPerDocument.put( docID, new AnnotationVectorArray() );
            }

            AnnotationVectorArray v = annotationsPerDocument.get( docID );
            v.mAnnotVectorArray.add( map.get( docID ) );
        }

        // Assemble annotations string
        if( annotationsPerDocument.size() > 0 )
        {
            return getAnnotationsString(annotationsPerDocument);
        }
        
        return "Could not read the annotation content!";
	       
	     }
	   
	   static String getAnnotationsString( HashMap<String, AnnotationVectorArray> map )
	    {
	        if( map == null )
	        {
	            return "";
	        }

	        StringBuffer sb = new StringBuffer();

	        // The key is annotation document ID (URL or number), the values are
	        // annotation instances, basically
	        Set<String> keys = map.keySet();

	        for( Iterator<String> it = keys.iterator(); it.hasNext(); )
	        {
	            String docID = it.next();
	            try{
		            sb.append( "Annotations for document " + ServiceInvocationHandler.tokens[Integer.parseInt(docID)] + ":" + System.getProperty("line.separator"));
		            System.out.println( "Annotations for document " + ServiceInvocationHandler.tokens[Integer.parseInt(docID)] + ":" + System.getProperty("line.separator"));
	            }catch(Exception e){
	            	e.printStackTrace();
	            }
	            AnnotationVectorArray va = map.get( docID );
	            sb.append( getAnnotationsString( va ) );
	        }

	        return sb.toString();
	    }
	   
	   static String getAnnotationsString( AnnotationVectorArray annotVectorArr )
	    {

	        StringBuffer strBuffer = new StringBuffer();


	        if( annotVectorArr == null )
	        {
	            return "";
	        }


	        for( Iterator<AnnotationVector> it = annotVectorArr.mAnnotVectorArray.iterator(); it.hasNext(); )
	        {
	            AnnotationVector annotVector = it.next();

	            strBuffer.append( "Annotation Type: " + annotVector.mType + System.getProperty("line.separator"));

	            System.out.println( "Annotation Type: " + annotVector.mType + System.getProperty("line.separator"));

	            strBuffer.append( listAnnotations( annotVector ) );
	        }

	        // sort annotations by start

	        //FIXME check Elian's changes in ClientUtils class for sorting annotation by offset
	        // ClientUtils.SortAnnotations( annotVectorArr );
	        //

	        //for ( Iterator<Annotation> it2 = ClientUtils.mAnnotArray.iterator(); it2.hasNext(); )
	       // {
	            // Create Side Notes
	        //}

	        return strBuffer.toString();

	    }

	    static String listAnnotations( AnnotationVector as )
	    {
	        if( as == null )
	        {
	            return "";
	        }

	        StringBuffer sb = new StringBuffer();

	        for( Iterator<Annotation> it = as.mAnnotationVector.iterator(); it.hasNext(); )
	        {
	            Annotation annotation = it.next();

	            if( annotation.mContent != null && !annotation.mContent.equals( "" ) )
	            {
	                sb.append( "Start: " + annotation.mStart + ", end: " + annotation.mEnd + ", content: " + annotation.mContent + System.getProperty("line.separator") );
	            }

	            if( annotation.mFeatures == null || annotation.mFeatures.size() == 0 )
	            {
	                sb.append(System.getProperty("line.separator"));
	                continue;
	            }

	            if( annotation.mFeatures.size() > 1 )
	            {
	                sb.append( "Features: " + System.getProperty("line.separator") + System.getProperty("line.separator"));
	            }

	            Set<String> keys = annotation.mFeatures.keySet();


	            for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); )
	            {
	                String currentKey = it2.next();
	                sb.append( currentKey + ": " + annotation.mFeatures.get( currentKey ) + System.getProperty("line.separator") + System.getProperty("line.separator"));
	            }

	            sb.append(System.getProperty("line.separator"));
	        }
	        return sb.toString();
	    }

		/**
		* Returns the content of a boundless annotation as a string.
		* @param String the wiki page name
		* @param String the wiki page URL
		* @return String Content of a boundless annotation
		*/
		static String getBoundlessResult(String docToken, String docLocalURL){
			StringBuffer resultsToWrite = new StringBuffer();
	        resultsToWrite.append(System.getProperty("line.separator"));
	    	resultsToWrite.append("{{SemAssist-Start|serviceName="+ InvokeCommand.serviceName + "|doc=" + docLocalURL + "|url=" + docToken +"}}");
	        resultsToWrite.append(System.getProperty("line.separator"));
	        resultsToWrite.append(SemAssistServlet.getWiki().getParser().translateBoundlessAnnotation(outputString));
	        resultsToWrite.append(System.getProperty("line.separator"));
	    	resultsToWrite.append("{{SemAssist-End|serviceName="+ InvokeCommand.serviceName + "|doc=" + docLocalURL + "}}");
	    	System.out.println("DEBUGG:: resultsToWrite " + resultsToWrite.toString());
	    	return resultsToWrite.toString();
		}

		/**
		* Returns the content of an annotation as a string.
		* @param document the source document ID
		* @param docToken the wiki page name
		* @param docLocalURL the wiki page URL
		* @return String content of an annotation
		*/
		static String getAnnotationResult(String document, String docToken, String docLocalURL){
	    	StringBuffer resultsToWrite = new StringBuffer();
			resultsToWrite.append(System.getProperty("line.separator"));
	    	resultsToWrite.append("{{SemAssist-Start|serviceName="+ InvokeCommand.serviceName + "|doc=" + docLocalURL + "|url=" + docToken +"}}");
	    	resultsToWrite.append(System.getProperty("line.separator"));
	    	resultsToWrite.append("{{SemAssist-TableStart}}");
	    	resultsToWrite.append(System.getProperty("line.separator"));

	    	@SuppressWarnings("unchecked")
	    	Collection<AnnotationVector> annotsForDocument = (Collection<AnnotationVector>) annotsMap.get(document);
	    	for(Iterator<AnnotationVector> itr = annotsForDocument.iterator(); itr.hasNext();){
	    		AnnotationVector annotsVector = itr.next();
	    		resultsToWrite.append(SemAssistServlet.getWiki().getParser().translateAnnotation(annotsVector));
	    	}
	    	resultsToWrite.append("{{SemAssist-TableEnd}}");
	    	resultsToWrite.append(System.getProperty("line.separator"));
	    	resultsToWrite.append("{{SemAssist-End|serviceName="+ InvokeCommand.serviceName + "|doc=" + docLocalURL + "}}");
	    	return resultsToWrite.toString();
		}

		/**
		* Parses the XML result and delegates the control to the right method.
		* @param String XML message
		*/
		static void parseResults(String _content){
				// returns result in sorted by type
		        Vector<SemanticServiceResult> results = ClientUtils.getServiceResults( _content );
		        //FIXME what's this for?
		        /*// Key is annotation document URL or ID
		        HashMap<String, AnnotationVectorArray> annotationsPerDocument = new HashMap<String, AnnotationVectorArray>();*/
		        for( Iterator<SemanticServiceResult> it = results.iterator(); it.hasNext(); ){
		            SemanticServiceResult current = it.next();
		            if( current.mResultType.equals( SemanticServiceResult.ANNOTATION ) ){  
		            	if(annotsMap == null){
		            		annotsMap = new MultiValueMap();
		            	}

		            	/** List of annotations that maps document IDs to annotation instances */
		    			HashMap<String, AnnotationVector> allAnnotations = current.mAnnotations;
		    			Set<String> documents = allAnnotations.keySet();
			        	String documentID=null;
		    			for( Iterator<String> it2 = documents.iterator(); it2.hasNext(); ){
		    				documentID = it2.next();
		    				annotsMap.put(documentID, allAnnotations.get(documentID));
		    			}
		    			annotationCase = true;
		            }else if(current.mResultType.equals( SemanticServiceResult.BOUNDLESS_ANNOTATION)){
		            	outputString = getAnnotationsString(current);
		            	boundlessAnnotationCase = true;
		            }else if(current.mResultType.equals( SemanticServiceResult.DOCUMENT)){
		            	documentCase = true;
		            }else if(current.mResultType.equals( SemanticServiceResult.FILE)){
		            	outputString = SemAssistServlet.broker.getResultFile(current.mFileUrl);
		            	fileCase = true;
		            }
		        }
		}

	/** Returns a MediaWikiBot object with the provided credentials.
	 * @return {@link MediaWikiBot} the MediaWiki bot
	 */
	@Override
	public void createBot() {
	   /*bot = new MediaWikiBot("http://localhost/mediawiki-1.16/index.php");
	   bot.login("wikisysop", "adminpass");
	   bot = new MediaWikiBot("http://localhost/smartwiki/index.php");
	   bot.login("admin", "bahar");*/
		try {
			System.out.println("Creating a bot: " + wikiAddress + wikiUser + wikiPass);
			bot = new MediaWikiBot(wikiAddress);
			bot.login(wikiUser, wikiPass);
		} catch (MalformedURLException e1) {
			   System.err.println("Could not access the wiki. The wiki URL is malformed.");
			   e1.printStackTrace();
		} catch (ActionException e) {
			e.printStackTrace();
		}
		
	}

	/** Returns a MediaWikiBot object with the provided credentials.
	 * @param iWikiAddress the wiki engine URL
	 * @param iWikiUser the bot username
	 * @param iWikiPass the bot password
	 * @return {@link MediaWikiBot} the MediaWiki bot
	 */
	private MediaWikiBot createBot(String iWikiAddress, String iWikiUser, String iWikiPass) {
		try {
			System.out.println("Creating a bot: " + iWikiAddress + iWikiUser + iWikiPass);
			MediaWikiBot bot = new MediaWikiBot(iWikiAddress);
			bot.login(iWikiUser, iWikiPass);
		} catch (MalformedURLException e1) {
			   System.err.println("Could not access the wiki. The wiki URL is malformed.");
			   e1.printStackTrace();
		} catch (ActionException e) {
			e.printStackTrace();
		} 
		return bot;
	}

	/**
	* Creates a new page wiki Semantic MediaWiki query.
	* @param String the entity type to query
	*/
	@Override
	public void createTypePage(String type) {
		try {
			Article article = new Article(bot, "Property:"+type);
			//article.setText("List of pages containing " + type + " entities:<br>" + "{{#ask: [[hasType::"+type +"]] |format=ul | default=No pages found!}}");
			article.setText("[[Has type::Text| ]]{{ListType|type=" + type +"}}");
			article.save();
		}catch (ActionException e) {
			e.printStackTrace();
		} catch (ProcessException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
