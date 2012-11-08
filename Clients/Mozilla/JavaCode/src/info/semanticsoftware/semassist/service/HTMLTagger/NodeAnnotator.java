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

package info.semanticsoftware.semassist.service.HTMLTagger;

import info.semanticsoftware.semassist.service.model.AnnotationInstance;
import info.semanticsoftware.semassist.service.presentation.AnnotationPresentation;

import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class NodeAnnotator {
	
	/** Valid HTML tags for scraping. */
	private static String validTags[] = {"div", "span", "a", "p", "h1", "h2", "h3", "b", "i", "td", "th", "li", "annotation"};
	
	/** Counter for the number of annotated instance in the page. */
	private static int counter = 0;
	
	/**
	 * Checks whether an element name should be considered for scraping.
	 * The rational is, we don't want to annotate content of script node content or 
	 * elements such as <script><ul></script> nodes that don't contain textual content.
	 * @param name node name
	 * @return true if the node is a valid node for scraping, false otherwise 
	 * */
	private static boolean isValidNode(String name){
		if(Arrays.asList(validTags).contains(name.toLowerCase())){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Checks whether the given element is hidden in the DOM.
	 * @param element DOM element
	 * @return true if the element is visible, false otherwise
	 * */
	private static boolean isVisibleToUser(Element element){
		//FIXME there is no way for us to know if an element is hidden if it uses an external CSS stylesheet.
		// look for type="hidden" attribute or inline CSS style "display: none;"
		if(element.attr("type").equalsIgnoreCase("hidden") || element.attr("style").toLowerCase().contains("display: none;") || element.attr("style").toLowerCase().contains("display:none;")){
			return false;
		}
		// if the parent is hidden, then this descendant is also hidden
		if(element.parent() != null && isVisibleToUser(element.parent())){
			return false;
		}
		return true;
	}
	
	public static String getAnnotatedHTML(String rawHTML, List<AnnotationInstance> annotsArray){
		//parseTree(rawHTML);
		long startTime = System.currentTimeMillis();		
		counter = 0;

		String html = rawHTML;
		Document doc = Jsoup.parse(html);
		Element body = doc.body();
		
		doTheSentences(body, annotsArray);
		
		Elements allElms = body.getAllElements();
		//Elements allElms = body.children();
		
		for(int k=0; k < annotsArray.size(); k++){	
			//System.out.println("new annotation " + annotsArray.get(k).getContent());
			String annotTxt = annotsArray.get(k).getContent();
			boolean annotFound = false;

			//special case for the body immediate text children
			if(body.ownText().indexOf(annotTxt) > -1){
				containedInSelf(body, annotsArray.get(k), k);
			}
			
			// we start from index 1 because we escaped the body node
			for(int i = 1; i < allElms.size(); i++){
				annotFound = false;
				Element elm = allElms.get(i);

				if(!isValidNode(elm.nodeName())){
					//System.out.println("not processing " + elm.nodeName() + " because it's invalid");
					continue;
				}
				/*if(!isVisibleToUser(elm)){
					//System.out.println("not processing " + elm.nodeName() + " because it's invisible");
					continue;
				}*/
				//System.out.println("processing " + elm.nodeName());
				// if annotTxt is contained in this node or any of its descendants
				if(elm.text().indexOf(annotTxt) > -1){
					//System.out.println(elm.nodeName() + " contains " + annotTxt);
					// if annotTxt is completely contained inside this element's own text value
					if(elm.ownText().indexOf(annotTxt) > -1){
						//containedInSelf(elm, annotTxt);
						/* ****************************************************************************************** EXAMPLE 1: <p> I have ANNOTATION <b>in my content</b>. */
						annotFound = containedInSelf(elm, annotsArray.get(k), k);
						/* ****************************************************************************************** annotFound is false only if the elm is <annotation> */
						if(annotFound)
							break;
					}else{
						//System.out.println("not an easy case");
						// look whether we can match it completely in the element's children
						/* ****************************************************************************************** EXAMPLE 2: <p> I have <b>ANNOTATION in my content</b>. */
						boolean mCase = true;
						for(Element node: elm.getAllElements()){
							//System.out.println("Matching " + node.nodeName());
							if(node.ownText().indexOf(annotTxt) > -1){
								mCase = false;
							}
						}
						if(mCase){
							//System.out.println("mutli case");
						}
					}//else
					
					// ok, we've got a problem here: it's a multi node case!
					/* ****************************************************************************************** EXAMPLE 3: <p> I have ANN<b>OTATION in my content</b>. */
					//if(annotFound == false){
						//System.out.println("Multi node case for " + elm.html().substring(0, 20));
						//containedInMultipleNodes(elm, annotTxt);
					//}
				}else{
					// if not, skip this element in whole, go to the next sibling
					//System.out.println("=========================================> Not found in this node.");
				}
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println(counter + " annotations matched in " + ((endTime - startTime)/1000) + " seconds!");
		return doc.body().html();
	}

	private static void doTheSentences(Element body,
		List<AnnotationInstance> annotsArray) {
		for(AnnotationInstance ann: annotsArray){
			String level = ann.getFeatureMap().getFeaturesMap().get("annotation_level");
			if(level != null){
				if(level.toLowerCase().equals("sentence")){
					Elements allElms = body.getAllElements();
					for(int i = 1; i < allElms.size(); i++){
						Element elm = allElms.get(i);
						if(elm.ownText().indexOf(ann.getContent()) > -1){
							String annotTxt = ann.getContent();
							//containedInSelf(elm, ann, 0);
							List<TextNode> selfTextNodes = elm.textNodes();
							for(int k=0; k < selfTextNodes.size(); k++){
								TextNode txtNode = selfTextNodes.get(k);
								String originalTxt = txtNode.text();
								int start = originalTxt.indexOf(annotTxt);
								if(start > -1){
									int end = originalTxt.indexOf(annotTxt) + annotTxt.length();
									TextNode prvNd = new TextNode(originalTxt.substring(0, start), "");
									TextNode tknNd = new TextNode(annotTxt, "");
									TextNode nxtNd = new TextNode(originalTxt.substring(end), "");
									
									txtNode.replaceWith(prvNd);
									prvNd.after(tknNd);
									tknNd.after(nxtNd);
									
									StringBuffer buffer = new StringBuffer();
									//buffer.append("<annotation style='background-color: yellow;'>");
									buffer.append("<annotation level='sentence' class='" + ann.getContent() + "' style='color: black !important; background-color: #" + AnnotationPresentation.getInstance().findAnnotColor(ann.getType()) + " !important;' originalText='" + ann.getContent() + "' id='" + ann.getType() + "' features='" +ann.getFeatures() + "' onmouseover='waitAndPop(event,\"" + ann.getContent() + "\");'>");
									buffer.append("</annotation>");
									tknNd.wrap(buffer.toString());
									tknNd.parent().wrap("<layer id='SA_Underline' class='" + HTMLTagger.serviceName + "'></layer>");
									//System.out.println("=========================================> Complete match in " + elm.nodeName() + "!");
									counter++;
								}
							}
						}
					}
				}
			}
		}
		
	}

	private static void parseTree(String rawHTML) {
		String html = rawHTML;
		Document doc = Jsoup.parse(html);
		Element body = doc.body();
		Elements bodyKinder = body.children();
		System.out.println("Breadth-first Search:");
		for(Element bodyKind: bodyKinder){
			System.out.println("|" + bodyKind.nodeName());
			getKinderRecursive(bodyKind, 1);
		}
		
		//System.out.println("Depth-first Search:");
		//for(Element bodyKind: bodyKinder){
			//getKinderRecursive(bodyKind);
		//}
	}
	
	private static void getKinderRecursive(Element node, int indention){
		if(node.children().size() == 0)
			return;
		for(Element nodeKind: node.children()){
			for(int i=0; i < indention; i++){
				System.out.print("-");
			}
			System.out.print(isVisibleToUser(nodeKind) + " ");
			System.out.println(nodeKind.nodeName());			
			getKinderRecursive(nodeKind, indention++);
		}
	}

	private static boolean containedInSelf(Element elm, AnnotationInstance annot, int index){
		if(elm.nodeName().equalsIgnoreCase("annotation")){
			//System.out.println("=========================================> " + elm.nodeName() + " is an <annotation> node. Checking whether overlapping is possible...");
			String level = elm.attr("level");
			if(!level.equalsIgnoreCase("sentence")){
				return false;
			}
		}
		String annotTxt = annot.getContent();
		List<TextNode> selfTextNodes = elm.textNodes();
		for(int i=0; i < selfTextNodes.size(); i++){
			TextNode txtNode = selfTextNodes.get(i);
			String originalTxt = txtNode.text();
			int start = originalTxt.indexOf(annotTxt);
			if(start > -1){
				int end = originalTxt.indexOf(annotTxt) + annotTxt.length();
				TextNode prvNd = new TextNode(originalTxt.substring(0, start), "");
				TextNode tknNd = new TextNode(annotTxt, "");
				TextNode nxtNd = new TextNode(originalTxt.substring(end), "");
				
				txtNode.replaceWith(prvNd);
				prvNd.after(tknNd);
				tknNd.after(nxtNd);
				
				StringBuffer buffer = new StringBuffer();
				//buffer.append("<annotation style='background-color: yellow;'>");
				buffer.append("<annotation class='" + annot.getContent() + "_" + (index+1) + "' style='border-bottom:4px solid #" + AnnotationPresentation.getInstance().findAnnotColor(annot.getType()) + ";' originalText='" + annot.getContent() + "' id='" + annot.getType() + "' features='" +annot.getFeatures() + "' onmouseover='waitAndPop(event,\"" + annot.getContent() + "\");'>");
				buffer.append("</annotation>");
				tknNd.wrap(buffer.toString());
				tknNd.parent().wrap("<layer id='SA_Underline' class='" + HTMLTagger.serviceName + "'></layer>");
				//System.out.println("=========================================> Complete match in " + elm.nodeName() + "!");
				counter++;
			}
		}
		return true;
	}
}
