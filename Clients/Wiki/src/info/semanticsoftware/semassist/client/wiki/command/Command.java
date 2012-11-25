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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Command abstract class to be extended by concrete instances.
 * @author Bahar Sateli
 * */
public abstract class Command {
	/** This method should be implemented by children of this class.
	 * @param request HTTP request object
	 * @param response HTTP response object
	 */
	public abstract void execute(HttpServletRequest request, HttpServletResponse response);
}