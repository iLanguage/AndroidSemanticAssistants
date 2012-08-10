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
package info.semanticsoftware.semassist.client.wiki.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import info.semanticsoftware.semassist.client.wiki.broker.ServerResponseHandler;
import info.semanticsoftware.semassist.client.wiki.broker.ServiceInvocationHandler;
import info.semanticsoftware.semassist.client.wiki.servlets.SemAssistServlet;
import info.semanticsoftware.semassist.client.wiki.utils.ConsoleLogger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implements service invocation command.
 * @author Bahar Sateli
 * */
public class InvokeCommand extends Command{

	/** target parameter read from request cookies. */
	private String target = null;

	/** targetName parameter read from request cookies. */
	private String targetName = null;

	/** otherWikiAddress parameter read from request cookies. */
	private String otherWikiAddress = null;

	/** otherWikiUser parameter read from request cookies. */
	private String otherWikiUser = null;

	/** otherWikiPass parameter read from request cookies. */
	private String otherWikiPass = null;

	/** serviceName parameter read from request cookies. */
	public static String serviceName = null;

	/** thisWikiAddress parameter read from request cookies. */
	private String thisWikiAddress = "";

	/** thisWikiUser parameter read from request cookies.*/
	private String thisWikiUser = "";

	/** thisWikiPass parameter read from request cookies. */
	private String thisWikiPass = "";

	/** input-docs parameter read from request cookies. */
	private String input = null;

	/**
	 * Overrides the superclass execute method.
	 * @param request HTTP request object
	 * @param response HTTP response object
	 * */
	@Override
	public void execute(final HttpServletRequest request, HttpServletResponse response) {
		// first, resolve the request
		initializeParemeters(request);
		PrintWriter out = null;

		if(thisWikiAddress.equals("") || thisWikiUser.equals("") || thisWikiPass.equals("")){
			// unknown wiki
			RequestDispatcher dispatcher = request.getRequestDispatcher("settings.jsp");
			try {
				dispatcher.forward(request, response);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				out = response.getWriter();
				response.setContentType("text/html");

				//MWHelper.wikiAddress = thisWikiAddress;
				//MWHelper.wikiUser = thisWikiUser;
				//MWHelper.wikiPass = thisWikiPass;
				SemAssistServlet.getWiki().getHelper().setCredentials(thisWikiAddress, thisWikiUser, thisWikiPass);

				// set the target scope
				ServerResponseHandler.setTarget(target);

				// set the target page name
				ServerResponseHandler.setTargetName(targetName);

				// set the destination wiki address when different from the source
				ServerResponseHandler.setWikiAddress(otherWikiAddress);

				// set the destination wiki credentials - user name
				ServerResponseHandler.setWikiUser(otherWikiUser);

				// set the destination wiki credentials - password
				ServerResponseHandler.setWikiPass(otherWikiPass);

				// set the input
				ServiceInvocationHandler.setStringArray(input);
				getRTParams(request);
				// invoke the service
				ServiceInvocationHandler.invokeService(serviceName);
				//out.println("Execution finished.");
				out.println(ConsoleLogger.getConsoleLogs());
				ConsoleLogger.clearConsole();
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reads servlet parameters from the request query string, cookies and HTTP header.
	 * @param request HTTP request object to read cookies from
	 * */
	private void initializeParemeters(final HttpServletRequest request){
		target = request.getParameter("target");
		targetName = request.getParameter("targetName");
		serviceName = request.getParameter("serviceName");
		input = request.getParameter("input");
		// from cookies
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for(int i=0; i < cookies.length; i++){
				if(cookies[i].getName().equals("thisWikiAddress")){
					try {
						thisWikiAddress = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
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

				if(cookies[i].getName().equals("otherWikiAddress")){
					try {
						otherWikiAddress = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					continue;
				}

				if(cookies[i].getName().equals("otherWikiUser")){
					try {
						otherWikiUser = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					continue;
				}

				if(cookies[i].getName().equals("otherWikiPass")){
					try {
						otherWikiPass = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					continue;
				}
			}
		}
	}

	/**
	 * Reads runtime parameters from the request cookie and assign them to the pipeline.
	 * @param request request to read cookies from
	 * */
	private void getRTParams(final HttpServletRequest request){
		Cookie[] cookies = request.getCookies();
		for(int i=0; i < cookies.length; i++){
			if(cookies[i].getName().equals("RTParams")){
				String entries = cookies[i].getValue();
				try {
					entries = URLDecoder.decode(entries, "UTF-8");
					ServiceInvocationHandler.setRTParams(SemAssistServlet.infos, serviceName ,entries);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
