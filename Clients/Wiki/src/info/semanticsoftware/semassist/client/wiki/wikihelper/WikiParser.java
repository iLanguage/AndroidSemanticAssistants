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
/**
 * An abstract class for wiki parsers.
 * @author Bahar Sateli
 * */
public abstract class WikiParser {
	/** Removes Semantic Assistants templates from wiki markup. 
	 * @param input string to remove templates from
	 * @return filtered input
	 */
	public abstract String removeAllTemplates(String input);
	/** Removes a specific Semantic Assistants service templates from wiki markup.
	 * @param inpt input document string
	 * @param serviceName NLP service name to find
	 * @param doc document URL
	 * @return filtered string without the service results markup 
	 */
	public abstract String removeServiceTemplates(String input, String serviceName, String doc);
	/** Updates a service template with new results. 
	 * @param originalContent the original wiki markup
	 * @param serviceName NLP service name to find
	 * @param pageURL page URL address to find
	 * @param results the results to replace
	 * @return updated wiki page markup
	 */
	public abstract String updateTemplate(String originalContent, final String serviceName, final String pageURL, final String results);
	/** Transforms annotation results to templates. 
	 * @param annotsVector annotation vector to be translated to wiki markup
	 * @return string representation of the annotations
	 * */
	public abstract String translateAnnotation(AnnotationVector annotsVector);
	/** Transforms boundless annotation results to templates.
	 * @param input a boundless annotation string content
	 * @return the boundless annotation wiki markup
	 */
	public abstract String translateBoundlessAnnotation(String input);
	/** Transforms wiki markup to HTML code. 
	 * @param markupContent wiki markup to be tranformed to HTML
	 * @return HTML representation of the wiki markup
	 * */
	public abstract String transformToHTML(String markupContent);
}
