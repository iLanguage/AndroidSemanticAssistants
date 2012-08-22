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

import info.semanticsoftware.semassist.client.wiki.html.HTMLGenerator;
import info.semanticsoftware.semassist.client.wiki.servlets.SemAssistServlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implements getting runtime parameters for a service command.
 * @author Bahar Sateli
 * */
public class ParamsCommand extends Command{

	/** serviceName parameter read from request cookies. */
	public static String serviceName = null;

	/**
	 * Overrides the superclass execute method.
	 * @param request HTTP request object
	 * @param response HTTP response object
	 * */
	@Override
	public void execute(final HttpServletRequest request, HttpServletResponse response) {
		// first, resolve the request
		initializeParemeters(request);

		// prepare the response output writer
		PrintWriter out = null;
		try {
			out = response.getWriter();
			response.setContentType("text/html");

			if(request.getParameter("serviceName") == null || request.getParameter("serviceName") == ""){
				out.println("Error: Service name is empty.");
			}else{
				// print out the HTML code
				out.println(HTMLGenerator.serviceRTP(SemAssistServlet.infos, serviceName));
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads servlet parameters from the request query string, cookies and HTTP header.
	 * @param request HTTP request object to read cookies from
	 * */
	private void initializeParemeters(final HttpServletRequest request){
		serviceName = request.getParameter("serviceName");
	}

}
