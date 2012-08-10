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
package info.semanticsoftware.semassist.client.wiki.servlets;

import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.ServiceInfoForClientArray;
import info.semanticsoftware.semassist.client.wiki.broker.ServiceAgentSingleton;
import info.semanticsoftware.semassist.client.wiki.command.Command;
import info.semanticsoftware.semassist.client.wiki.command.CommandFactory;
import info.semanticsoftware.semassist.client.wiki.utils.ConsoleLogger;
import info.semanticsoftware.semassist.client.wiki.wikihelper.WikiEngine;
import info.semanticsoftware.semassist.client.wiki.wikihelper.WikiFactory;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URLDecoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is the main Servlet class. It receives all the incoming client requests and acts upon the command.
 * @author Bahar Sateli
 */
public class SemAssistServlet extends HttpServlet {

	/** Static access to the wiki engine object. */
	private static WikiEngine wiki = null;

	/** Auto-generated universal version identifier for this class. */
	private static final long serialVersionUID = 1L;

	/** Semantic Assistants server broker object */
	public static SemanticServiceBroker broker = null;

	/** Array of available services read from Semantic Assistants server. */
	public static ServiceInfoForClientArray infos =null;

	/** The URL of the page sending the request from the request HTTP header. */
	public static String refererURL;

	/** thisWikiAddress parameter read from request cookies. */
	private String thisWikiEngine = "";
	
	/** thisWikiAddress parameter read from request cookies. */
	private String thisWikiAddress = "";

	/** thisWikiUser parameter read from request cookies. */
	private String thisWikiUser = "";

	/** thisWikiPass parameter read from request cookies. */
	private String thisWikiPass = "";

	/** semassist-server parameter read from request cookies. */
	private String semassist_server = "";

	/** action parameter read from request query string. */
	private String action = null;

	/** Absolute file path of Wiki ontology file in the repository. */
	public static String WIKI_ONTOLOGY ="";

	/** Absolute file path of Semantic Assistants Upper ontology file in the repository. */
	public static String UPPER_ONTOLOGY ="";

	/** Absolute file path of ontology repository folder. */
	public static String ONTOLOGY_REPO ="";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SemAssistServlet() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		ServletContext context = config.getServletContext();
		ONTOLOGY_REPO = context.getRealPath("/ontology-repository");
		WIKI_ONTOLOGY = context.getRealPath("/ontology-repository/WikiOntology.owl");
		UPPER_ONTOLOGY = context.getRealPath("/ontology-repository/ConceptUpper.owl");
		new BaseOntologyKeeper();
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		broker = null;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	/**
	 * Processes an HTTP request and acts upon the action.
	 * @param request HTTP request object
	 * @param response HTTP response object
	 * @throws ServletException a general exception when servlet encounters difficulty
	 * @throws IOException a general exception when servlet encounters IO difficulty
	 * */
	protected synchronized void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Reset the servlet's memory
		thisWikiEngine = "";
		thisWikiAddress = "";
		thisWikiUser = "";
		thisWikiPass = "";
		semassist_server = "";
		
		// Resolve the request
		initializeParemeters(request);

		// if the referrer is not in the header, then maybe it's in the query string
		if(refererURL == null){
			refererURL = request.getParameter("url");
		}

		if(action == null || action == "" || refererURL == null){
			// no direct access
			RequestDispatcher dispatcher = request.getRequestDispatcher("help.jsp");
			dispatcher.forward(request, response);
		}else if(thisWikiAddress.equals("") || thisWikiUser.equals("") || thisWikiPass.equals("")){
			// no unknown wiki
			RequestDispatcher dispatcher = request.getRequestDispatcher("settings.jsp");
			dispatcher.forward(request, response);
		}else{
			ServiceAgentSingleton.setServer(semassist_server);
			broker = ServiceAgentSingleton.getInstance();
			infos = broker.getAvailableServices();
			wiki = WikiFactory.getWiki(thisWikiEngine.substring(0, thisWikiEngine.indexOf("-")));
			Command command = CommandFactory.getCommand(action);
			
			if(!command.equals(null)){
				command.execute(request, response);
			}else{
				// prepare the response output writer
				PrintWriter out = response.getWriter();
				response.setContentType("text/html");
				ConsoleLogger.log("Exception on executing command: " + action + ". Aborting the request.");
				out.println(ConsoleLogger.getConsoleLogs());
				ConsoleLogger.clearConsole();
				out.flush();
			}
		}
	}

	/**
	 * Reads servlet parameters from the request query string, cookies and HTTP header.
	 * @param request HTTP request object to read cookies from
	 * */
	private void initializeParemeters(final HttpServletRequest request){
		// from HTTP header
		refererURL = request.getHeader("referer");
		// from query string
		action = request.getParameter("action");

		// from cookies
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for(int i=0; i < cookies.length; i++){
				if(cookies[i].getName().equals("semassist-server")){
					try {
						semassist_server = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					continue;
				}

				if(cookies[i].getName().equals("thisWikiAddress")){
					try {
						thisWikiAddress = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					continue;
				}

				if(cookies[i].getName().equals("thisWikiEngine")){
					try {
						thisWikiEngine = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					continue;
				}

				if(cookies[i].getName().equals("thisWikiUser")){
					try {
						thisWikiUser = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					continue;
				}

				if(cookies[i].getName().equals("thisWikiPass")){
					try {
						thisWikiPass = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					continue;
				}
			}
		}
	}

	/**
	 * Returns the HTML content of the specified URL.
	 * @param url page URL to read the contents from
	 * @return String HTML content of the page
	 * */
	public String getPageContent(final String url){
		String pageName = "";
		pageName = url.substring("http://".length());
		pageName = pageName.substring(pageName.lastIndexOf("/")+1);
		try {
			return read();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns only the contents between <code>head</code> tag. This method is used by proxy.jsp page.
	 * @param url page URL
	 * @param request HTTP request
	 * @return String contents of the HTML head part
	 * */
	public String getHeadContent(final String url, final HttpServletRequest request){
		// first, resolve the request
		initializeParemeters(request);
		String content = getPageContent(url);
		String result;
		int index = content.indexOf("<head>");
		int index2 = content.indexOf("</head>");
		result = content.substring(index+("<head>".length()), index2);
		return result;
	}

	/**
	 * Returns only the contents between <code>body</code> tag. This method is used by proxy.jsp page.
	 * @param url page URL
	 * @param request HTTP request
	 * @return String contents of the HTML body part
	 */
	public String getBodyContent(final String url, final HttpServletRequest request){
		// first, resolve the request
		initializeParemeters(request);
		String content = getPageContent(url);
		int index = content.indexOf("<body");
		String result = content.substring(index);
		int index2 = result.indexOf(">");
		result = result.substring(index2+1);
		int index3 = result.indexOf("</body>");
		result = result.substring(0, index3);
		return result;
	}

	/**
	 * Returns the wiki object
	 * @return WikiEngine the wiki object
	 */
	public static WikiEngine getWiki(){
		return wiki;
	}

	/**
	 * Sends a request to the wiki bot to read the content
	 * of a page with a logged in bot.
	 * @return String HTML content of the page
	 * @throws Exceptin if the connection cannot be established
	 */
	public String read() throws Exception {
		try{
			String result = "";
			String engine = thisWikiAddress.substring(0, thisWikiAddress.lastIndexOf("/"));
			String script = engine+"/bot.php?user="+thisWikiUser+"&pass="+thisWikiPass+"&url="+refererURL+"&engine="+engine;
			System.out.println("Sending request to " + script);
			URL url = new URL(script);
			URLConnection conn = url.openConnection ();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null){
				sb.append(line);
			}
			rd.close();
			result = sb.toString();
			System.out.println("Result: " + result);
			return result;
			} catch (Exception e){
				e.printStackTrace();
			}
			return null;
	}
}
