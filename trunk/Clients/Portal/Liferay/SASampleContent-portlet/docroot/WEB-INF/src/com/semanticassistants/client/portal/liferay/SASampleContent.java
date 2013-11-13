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
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Content portlet class - contains the text, highlights and summarizes the annotations
 * @author Felicitas Loeffler
 * @author Fedor Bakalov
 *
 */
public class SASampleContent extends MVCPortlet{

	/**
	 * renders the ContentPortlet
	 * @param request
	 * @param response
	 */
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		String sa_result = request.getParameter("sa_result");
		PortletPreferences prefs = request.getPreferences();
		prefs.setValue("sa_result", sa_result);
		prefs.store();

		super.render(request, response);
	}


	/**
	 * sends the text to the SemanticAssistantsPortlet
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws PortletException
	 */
	public void sendText(ActionRequest  request, ActionResponse response) throws IOException, PortletException  {

		String sa_text = (String)request.getParameter("sa_text");
        response.setRenderParameter("sa_text", sa_text);
    }

	/**
	 * refreshs the renderParameters, sa_text=null, sa_result=null
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws PortletException
	 */
	public void refresh(ActionRequest  request, ActionResponse response) throws IOException, PortletException  {
        response.setRenderParameter("sa_text", "");
        response.setRenderParameter("sa_result", "");
        response.setRenderParameter("sa_result_type", "");
    }

}