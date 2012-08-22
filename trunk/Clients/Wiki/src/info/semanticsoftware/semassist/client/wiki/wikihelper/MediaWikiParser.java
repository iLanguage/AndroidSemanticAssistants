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

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import info.semanticsoftware.semassist.csal.result.Annotation;
import info.semanticsoftware.semassist.csal.result.AnnotationVector;
import info.semanticsoftware.semassist.client.wiki.servlets.SemAssistServlet;
import info.semanticsoftware.semassist.client.wiki.utils.Parser;

/**
 * This class is aware of MediaWiki syntax and can transform wiki markup to HTML.
 * @author Bahar Sateli
 */
public class MediaWikiParser extends WikiParser{

	/** Semantic Assistants template start line. */
	private static final String TEMPLATE_SEMASSIST_START = "{{SemAssist-Start";

	/** Semantic Assistants template end line. */
	private static final String TEMPLATE_SEMASSIST_END = "{{SemAssist-End";

	/** 
	 * Updates a Semantic Assistants' template content and returns the result as a string.
	 * @param originalContent the page's original content
	 * @param serviceName the template's service name to match
	 * @param pageURL page's URL
	 * @param results result to be written into the template
	 * @return String updated template content
	 * */
	@Override
	public String updateTemplate(String originalContent, final String serviceName, final String pageURL, final String results) {
		int startIndex = originalContent.indexOf(TEMPLATE_SEMASSIST_START+"|serviceName="+ serviceName + "|doc=" + pageURL);
		if(startIndex == -1){
			return originalContent.concat(results);
		}else{
			String firstPart = originalContent.substring(0, startIndex);
			String endString = TEMPLATE_SEMASSIST_END+"|serviceName=" + serviceName + "|doc=" + pageURL + "}}";
			int endIndex = originalContent.indexOf(endString);
			String secondPart = originalContent.substring(endIndex+endString.length());
			String finale = firstPart.trim() + results + secondPart.trim();
			//return removeTemplates(finale, _serviceName, _pageURL);
			return finale;
		}
	}

	/**
	 * Transforms the annotation results to MediaWiki templates.
	 * @param annotsVector vector of annotations parsed from server XML response
	 * @return String MediaWiki template code containing the annotation
	 * */
	@Override
	public String translateAnnotation(final AnnotationVector annotsVector) {
		StringBuffer contentBuffer = new StringBuffer();
		String type = annotsVector.mType;
		Vector<Annotation> annots = annotsVector.mAnnotationVector;
		if(annots.size() == 0){
			contentBuffer.append("{{SemAssist-TableRow|content= No annotations found |type=" + type + "|start= |end= |features= }}" + System.getProperty("line.separator"));
		}else{
			System.out.println("Creating type page: " + type);
			SemAssistServlet.getWiki().getHelper().createTypePage(type);
			for(int i=0; i < annots.size(); i++){
				String content = annots.get(i).mContent;
				String start = String.valueOf(annots.get(i).mStart);
				String end = String.valueOf(annots.get(i).mEnd);

				Set<String> featureNames = annots.get(i).mFeatures.keySet();
				String features= "";
				for(Iterator<String> it3 = featureNames.iterator(); it3.hasNext();){
					String name = it3.next();
					String value = annots.get(i).mFeatures.get(name);
					if(value.equals("") || value.equals(" ")){
						value = "-";
					}
					//features += name+"="+value+" ";
					features += System.getProperty("line.separator");
					features += "* " + name+": "+value+" ";
				}
				contentBuffer.append("{{SemAssist-TableRow|content=" + content + "|type=" + type + "|start=" + start + "|end=" + end + "|features=" + features +"}}");
				contentBuffer.append(System.getProperty("line.separator"));
			}
		}
		return contentBuffer.toString();
	}

	/**
	 * Transforms the boudnless annotation results to MediaWiki templates.
	 * @param input string content of the boundless annotation
	 * @return String MediaWiki template code containing the boundless annotation
	 * */
	@Override
	public String translateBoundlessAnnotation(final String input) {
		StringBuffer contentBuffer = new StringBuffer();
		contentBuffer.append("{{SemAssist-Block|content=" + input + "}}");
		return contentBuffer.toString();
	}

	/**
	 * Transforms a wiki page raw markup to HTML code.
	 * @param markupContent wiki page raw markup
	 * @return String HTML representation of wiki page
	 * */
	@Override
	public String transformToHTML(final String markupContent) {
		Parser parserLanguage = new Parser();
		MarkupParser markupParser = new MarkupParser();
		markupParser.setMarkupLanguage(parserLanguage.getLanguage());
		String htmlContent = markupParser.parseToHtml(removeAllTemplates(markupContent));
		return htmlContent;
	}

	/**
	 * Removes all Semantic Assistants templates from a wiki page markup.
	 * @param input the wiki page content
	 * @return String cleaned wiki page markup
	 * */
	@Override
	public String removeAllTemplates(final String input) {
		int startIndex = input.indexOf(TEMPLATE_SEMASSIST_START);
		if(startIndex == -1){
			return input;
		}else{
			String firstPart = input.substring(0, startIndex);
			// Since we don't know the service name, it's a bit tricky
			int endIndex = input.indexOf(TEMPLATE_SEMASSIST_END);
			String temp = input.substring(endIndex);
			int closingIndex = temp.indexOf("}}");
			String secondPart = temp.substring(closingIndex+2);
			return removeAllTemplates(firstPart+secondPart);
		}
	}

	/**
	 * Removes a specific Semantic Assistants template from a wiki page markup.
	 * @param input the wiki page content
	 * @param serviceName service name to look for
	 * @param doc document name to look for
	 * @return String cleaned wiki page markup
	 * */
	@Override
	public String removeServiceTemplates(final String input, final String serviceName, final String doc) {
		int startIndex = input.indexOf(TEMPLATE_SEMASSIST_START+"|serviceName="+serviceName + "|doc=" + doc);
		if(startIndex == -1){
			return input;
		}else{
			String firstPart = input.substring(0, startIndex);
			String endString = TEMPLATE_SEMASSIST_END+"|serviceName=" + serviceName + "|doc=" + doc + "}}";
			int endIndex = input.indexOf(endString);
			String secondPart = input.substring(endIndex+endString.length());
			return removeServiceTemplates(firstPart+secondPart, serviceName, doc);
		}
	}
}
