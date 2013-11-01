/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2013, 2014 Semantic Software Lab, http://www.semanticsoftware.info

The Semantic Assistants architecture is free software: you can
redistribute and/or modify it under the terms of the GNU Affero General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see http://www.gnu.org/licenses/.
*/

package com.semanticassistants.client.portal.liferay;

import java.io.IOException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Semantic Assistants related functionalities are implemented in this class.
 * 
 * @author Felicitas Loeffler
 * @author Bahar Sateli
 */
public class SemanticAssistantsPortlet extends MVCPortlet {
	/**
	 * overrides the default behavior of the view method it is executed before
	 * the view.jsp file is rendered
	 */
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		renderResponse.setContentType("text/html");
		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/jsp/view.jsp");
		dispatcher.include(renderRequest, renderResponse);
		// get the parameters
		PortletPreferences prefs = renderRequest.getPreferences();
		/*
		 * //try to get the parameter id1 from Content-portlet String text =
		 * renderRequest.getParameter("id1"); if(text!=null) { //give the result
		 * back prefs.setValue("text", text); prefs.store(); }
		 */
		prefs.setValue("greeting", "Hello World! This is the SA portlet!");
		prefs.store();
		// super.doView(renderRequest, renderResponse);
	}

	@Override
	/**
	 * Receives the AJAX call for changing the Semantic Assistants server in the combobox.
	 * @param request the AJAX call
	 * @param response the AJAX call response 
	 */
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		String server = request.getParameter("server");
		response.setContentType("json");
		response.resetBuffer();
		String serviceNames = SemanticAssistantsUtils.getInstance().getAvailableServices(server);
		response.getWriter().print(serviceNames);
		response.flushBuffer();
	}

	public void runAssistant(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		String content = request.getParameter("sa_text");
		String serviceName = request.getParameter("serviceName");
		String endpointURL = request.getParameter("endpoint");
		String paramsQuery = request.getParameter("paramsQuery");
		//System.out.println("params query: " + paramsQuery);
		String results = SemanticAssistantsUtils.getInstance().invokeAssistant(endpointURL, serviceName, paramsQuery, content);
		String output = SemanticAssistantsUtils.getInstance().XMLtoJSON(results);
		String output_type = SemanticAssistantsUtils.getInstance().getResultType();
		response.setRenderParameter("sa_result", output);
		response.setRenderParameter("sa_result_type", output_type);
	}
}
