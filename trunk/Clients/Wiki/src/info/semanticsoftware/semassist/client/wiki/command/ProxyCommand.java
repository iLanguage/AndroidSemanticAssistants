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
import info.semanticsoftware.semassist.client.wiki.servlets.SemAssistServlet;
import info.semanticsoftware.semassist.client.wiki.utils.ConsoleLogger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Proxy command acts as a proxy server. It reads the referrer URL content and serves it via Semantic Assistants Wiki-NLP Integration servlet.
 * @author Bahar Sateli
 * */
public class ProxyCommand extends Command{

	/** URL of the wiki web server to be passed to JavaScript code. */
	public static String add ="";

	/**
	 * Overrides the superclass execute method.
	 * @param request HTTP request object
	 * @param response HTTP response object
	 * */
	@Override
	public void execute(final HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			ConsoleLogger.log("Proxy page requested for " + SemAssistServlet.refererURL);
			String hostURL = SemAssistServlet.refererURL.substring("http://".length());
			hostURL = hostURL.substring(0, hostURL.indexOf("/"));
			add = "<script type=\"text/javascript\">var add = \"http://"+ hostURL + "\"; var ref = \""+ SemAssistServlet.refererURL + "\";" + "</script>";
			// redirect the user to the proxy page
			RequestDispatcher dispatcher = request.getRequestDispatcher("proxy.jsp");
			dispatcher.forward(request, response);
		} catch (IOException e) {
			// could not get response writer stream
			e.printStackTrace();
		} catch (ServletException e) {
			// could not forward
			e.printStackTrace();
		}finally{
			System.out.println(ConsoleLogger.getConsoleLogs());
			ConsoleLogger.clearConsole();
		}
	}
}
