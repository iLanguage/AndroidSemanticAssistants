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

import java.util.Iterator;
import java.util.List;

import net.java.dev.jaxb.array.StringArray;
import info.semanticsoftware.semassist.server.GateRuntimeParameter;
import info.semanticsoftware.semassist.server.GateRuntimeParameterArray;
import info.semanticsoftware.semassist.server.ServiceInfoForClient;
import info.semanticsoftware.semassist.server.ServiceInfoForClientArray;
import info.semanticsoftware.semassist.server.UriList;
import info.semanticsoftware.semassist.server.UserContext;
import info.semanticsoftware.semassist.client.wiki.servlets.SemAssistServlet;
import info.semanticsoftware.semassist.client.wiki.utils.ConsoleLogger;

/**
 * This class handles the request to the Semantic Assistants server.
 * @author Bahar Sateli
 * */
public class ServiceInvocationHandler {

	/** Array of GATE pipeline's runtime parameters. */
	private static GateRuntimeParameterArray rtpArray = new GateRuntimeParameterArray();

	/** Array of Strings to send to pipeline. */
	private static StringArray stringArray = new StringArray();

	/** User context object. */
	private static UserContext ctx = new UserContext();

	/** List of document URIs to send to pipeline. */
	private static UriList uriList = new UriList();

	/** List of document URLs to process. */
	public static String[] tokens;

	/**
	 * Adds the entry to the list of documents.
	 * @param entries a concatinated list of document URLs
	 * */
	public static void setStringArray(String entries){
		stringArray.getItem().clear();
		uriList.getUriList().clear();
		tokens = entries.split("\\|");
		//TODO remove debugging line
		System.out.println("Number of inputs: " + tokens.length);
		ConsoleLogger.log("Number of documents to process: " + tokens.length);
		for(String token : tokens){
			//TODO remove debugging line
			System.out.println("URL: " + token);
			ConsoleLogger.log("Processing \"" + token + "\"");
			//String pageName = token.substring(token.lastIndexOf("/") + 1);
			//FIXME this is hack for MediaWiki, should be fixed
			int temp = token.lastIndexOf("index.php") + "index.php".length() + 1;
			String pageName = token.substring(temp);

			//String wikitext = MWHelper.getPageContent(pageName);
			String wikitext = SemAssistServlet.getWiki().getHelper().getPageContent(pageName);
			//String htmlContent = MWParser.getHTML(wikitext);
			String htmlContent = SemAssistServlet.getWiki().getParser().transformToHTML(wikitext);
			String url = "#" + token;
			uriList.getUriList().add(url);
			stringArray.getItem().add(htmlContent);
			/*try {
				// Create one directory
				boolean success = (new File(System.getProperty("user.home")+ System.getProperty("file.separator") +"sa-temp").mkdir());
				if (success) {
					System.out.println("Directory created");
				}
				FileWriter fstream = new FileWriter(System.getProperty("user.home")+ System.getProperty("file.separator") + "sa-temp" + System.getProperty("file.separator") +"output.html");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(htmlContent);
				out.close();

				File f = new File(System.getProperty("user.home")+ System.getProperty("file.separator") + "sa-temp" + System.getProperty("file.separator") +"output.html");
				System.out.println("file://" + f.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}*/
		}
	}

	/**
	 * Assigns runtime parameters to the specified pipeline.
	 * @param services array of available services
	 * @param name pipeline name
	 * @param entries concatinated list of runtime parameters
	 * */
	public static void setRTParams(ServiceInfoForClientArray services, String name, String entries){
		rtpArray.getItem().clear();
		// String of runtime parameters read from the request cookie
		String[] tokens = entries.split("\\|");

		for(ServiceInfoForClient service : services.getItem()){
			// Find what are this service runtime parameters are
			if(service.getServiceName().equals(name)){
				List<GateRuntimeParameter> params = service.getParams();
				// the order of service's runtime parameters are kept for presentation, thus it's safe to sequentially iterate
				for(Iterator<GateRuntimeParameter> itr = params.iterator(); itr.hasNext();){
					GateRuntimeParameter param = itr.next();
					String type = param.getType();
					for(String token : tokens){
						String[] parts = token.split("=");
						String paramValue = parts[1];
						try{
							if(param.getParamName().equals(parts[0])){
								if(type.equals("int")){
									param.setIntValue(Integer.parseInt(paramValue));
								}else if(type.equals("string")){
									param.setStringValue(paramValue);
								}else if(type.equals("double")){
									param.setDoubleValue(Double.parseDouble(paramValue));
								}else if(type.equals("boolean")){
									param.setBooleanValue(Boolean.parseBoolean(paramValue));
								}else if(type.equals("url")){
									param.setUrlValue(paramValue);
								}
								break;
							}
						}catch(Exception e){
							System.out.println(e.getMessage());
						}
						rtpArray.getItem().add(param);
					}
				}
			break;
			}
		}	
	}

	/**
	 * Makes a service call for the specified pipeline.
	 * to the Semantic Assistants server
	 * @param name pipeline name
	 * */
	public static void invokeService(final String name){
		String serviceResponse = null;
		try{
			//TODO remove debugging line
			System.out.println("Invoking " + name + "...");
			ConsoleLogger.log("Invoking \"" + name + "\"");
			for(int i=0; i < stringArray.getItem().size(); i++){
				System.out.println("# " + stringArray.getItem().get(i));
			}
			serviceResponse = SemAssistServlet.broker.invokeService(name, uriList, stringArray, 0L, rtpArray, ctx);
			System.out.println("Server Response: " + serviceResponse);
			//TODO remove debugging line
			System.out.println("Writing the results...");
			ConsoleLogger.log("Service invocation finished. Writing the results...");
			ServerResponseHandler.writeResponse(serviceResponse);
			//TODO remove debugging line
			ConsoleLogger.log("Execution finished.");
			System.out.println("Service invocation finished.");
		}catch (Exception connEx){
			connEx.printStackTrace();
			System.err.println("Server not found. \nPlease check the Server Host and Port and if Server is Online");
		}
	}
}
