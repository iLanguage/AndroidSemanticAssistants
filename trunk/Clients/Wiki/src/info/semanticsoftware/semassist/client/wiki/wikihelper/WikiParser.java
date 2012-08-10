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

import info.semanticsoftware.semassist.csal.result.AnnotationVector;

public abstract class WikiParser {
	/** Removes Semantic Assistants templates from wiki markup */
	abstract public String removeAllTemplates(String input);
	/** Removes a specific Semantic Assistants service templates from wiki markup */
	abstract public String removeServiceTemplates(String input, String serviceName, String doc);
	/** Updates a service template with new results */
	abstract public String updateTemplate(String originalContent, final String serviceName, final String pageURL, final String results);
	/** Transforms annotation results to templates */
	abstract public String translateAnnotation(AnnotationVector annotsVector);
	/** Transforms boundless annotation results to templates */
	abstract public String translateBoundlessAnnotation(String input);
	/** Transforms wiki markup to HTML code */
	abstract public String transformToHTML(String markupContent);
}
