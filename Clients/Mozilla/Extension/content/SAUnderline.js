/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Contributor(s):
 *	 Bahar Sateli
 *   Jason Tan
 *   Kevin Tung
 *   Paola Jimenez
 *
 * ***** END LICENSE BLOCK ***** */

/** Imports the SAModule module to this class. */
Components.utils.import("resource://SemanticAssistants/sa_module.js");

 /**
 * @class This class provides annotating utilities.
 * The {@link SAOverlay} class delegates service calls to this class for annotation.
 * @author Bahar Sateli
 */
var SAUnderline = {

	highlight: function(document, annotArray, serviceSelected){
		Application.console.log("SAUnderline highlight");
	},
    
	/**
	* Sends an AJAX call to the configured SA server and replaces the body content with the response text.
	* @param document the window document object
	* @param serviceName NLP service name to execute
	*/
	annotate: function(document, serviceName){
	
		var TAGGER_SERVICE_HOST = "132.205.237.32"; //132.205.237.32 SASVM
		var TAGGER_SERVICE_PORT = "7129";

		if(serviceName == null || serviceName == ""){
			//Application.console.log("Last called service is: " + SAModule.getLastCalledServiceName());
			serviceName = SAModule.getLastCalledServiceName();
		}

		var SAresult = document.head.getAttribute("SAresult");
		
		if(SAresult == null || SAresult == "undefined"){
			// first inject the SA scripts to head
			this.injectHeaders(document);
		}

		// clear any user selection
		window.content.getSelection().removeAllRanges();
		
		//FIXME do we need to keep this?
		//var uniqueCount = this.getUniqueCount(window.content);
		
		// remember which page requested the processing. We need it to find the right window later.
		var windowURL = window.content.document.URL;
		
		/*
			var request;
			if(window.XMLHttpRequest){
				// IE7+, Firefox, Chrome, Opera, Safari
				request = new XMLHttpRequest();
			}else{
				// IE6, IE5
				request = new ActiveXObject("Microsoft.XMLHTTP");
			}
		*/
		// Firefox only request! For other browsers do a cross-browser check like above commented code.
		var request = new XMLHttpRequest();
		
		// attach the status change listener
		request.onreadystatechange=function(){
			// if the request is done and it's successful
			if(request.readyState == 4 && request.status == 200){
				
				// find the window that requested the content change. This is necessary because the user might have switched to another tab.
				var found = false;
				var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"].getService(Components.interfaces.nsIWindowMediator);
				var browserEnumerator = wm.getEnumerator("navigator:browser");
				while (!found && browserEnumerator.hasMoreElements()) {
					// find the current activce browser
					var browserWin = browserEnumerator.getNext();
					// find the tabs of this browser
					var tabbrowser = browserWin.gBrowser;

					for (var index = 0; index < tabbrowser.browsers.length; index++) {
						 var currentBrowser = tabbrowser.getBrowserAtIndex(index);
						 // check if the tab URI is the same as the requesting page
						 if(currentBrowser.currentURI.spec == windowURL){
								
								// select the tab
								tabbrowser.selectedTab = tabbrowser.tabContainer.childNodes[index];
								
								// focus on the browser window
								browserWin.focus();
								
								// now replace its body content with the annotated response text
								window.content.document.body.innerHTML = request.responseText;
								
								// open the sidebar
								var openSidebar = true;

								if (openSidebar){
									//TODO: what's the reason to keep this?
									window.content.document.head.setAttribute("SAresult", Math.random().toString(16).substring(2));
									SAOverlayHelper.loadSidebar();
								}
								
								// done! :)
								break;
						 }
					}
				}	
			}// HTTP 200
		}//listener

		// prepare the POST request parameters (i.e., serviceName, serverHost, serverPort, doc (URL), content)
		var params = null;
		params = "serviceName=" + serviceName + "&serverHost=132.205.237.32&serverPort=8879&doc=" + windowURL;
		params += "&content="+ encodeURIComponent(window.content.document.body.innerHTML);
		
		// send it away!
		request.open("POST", "http://" + TAGGER_SERVICE_HOST + ":" + TAGGER_SERVICE_PORT +"/tag", true);
		request.send(params);
	},
	
	/**
	* Injects the SA scripts to the document head node.
	* @param document window document object
	* Note: if we can later host these scripts on the SASVM machine, we can replace this verbose literal injection with another AJAX call.
	*/
	injectHeaders: function(document){
		var jQ = document.createElement("script");
		jQ.setAttribute("type","text/javascript");
		jQ.setAttribute("src","http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.js");
		document.getElementsByTagName('head')[0].appendChild(jQ);	
		
		var conflict = document.createElement("script");
		conflict.setAttribute("type","text/javascript");
		var noConfTxt = "var conf = jQuery.noConflict();";
		var noConfNode = document.createTextNode(noConfTxt);
		conflict.appendChild(noConfNode);
		document.getElementsByTagName('head')[0].appendChild(conflict);

		var jQueryUI = document.createElement("script");
		jQueryUI.setAttribute("type","text/javascript");
		jQueryUI.setAttribute("src","http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.10/jquery-ui.js");
		document.getElementsByTagName('head')[0].appendChild(jQueryUI);
	
		var css = document.createElement("link");
		css.setAttribute("rel","stylesheet");
		css.setAttribute("type","text/css");
		css.setAttribute("href","http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.10/themes/redmond/jquery-ui.css");
		document.getElementsByTagName('head')[0].appendChild(css);

		var script = document.createElement("script");
		script.setAttribute("type","text/javascript");

		var literal = "function findAnnotationNode(event){"+
		"    if(event.target.tagName.toLowerCase() == \"annotation\"){" + 
		" return event.target; " +
		"    }else{"+
		"        var parent = event.target.parentNode;"+
		"        while(parent.tagName.toLowerCase() != \"annotation\"){"
		+"            parent = parent.parentNode;}"+
		"        return parent;}}" + 
		" function showAnnotInfo(event, annotTxt){"+
		" var newDiv = $(document.createElement('div'));"+
		" var dialogTitle = \"\"; if(annotTxt.trim().length > 100){ dialogTitle = annotTxt.trim().substring(0,80) + \"...\";}else{dialogTitle = annotTxt;} "+
		" newDiv.attr(\"title\", dialogTitle);"+
		" var annotNode = this.findAnnotationNode(event);"+
		" var featuresTokens = annotNode.getAttribute(\"features\").trim().split(\"|\");"+
		" var feats = \"\" ;"+
		" feats += \"<dl>\" ;"+
		" for(var i=0; i < featuresTokens.length-1; i++){"+
		" var key = featuresTokens[i].substring(0, featuresTokens[i].indexOf(\"=\")).trim(); if(key.toLowerCase() == \"annotation_level\"){continue;}var value = featuresTokens[i].substring(featuresTokens[i].indexOf(\"=\")+1).trim();"+
		" feats += \"<dt style='font-style: italic;'>\" + key + \"</dt>\"; " +
		" if(value.indexOf(\"http\") > -1){feats += \"<dd><a href='\" + value + \"' target=\'_blank\'>\" + value + \"</a></dd>\";}else{feats += \"<dd>\" + value + \"</dd>\";}" +
		" }"+
		" feats += \"</dl>\"; " +
		" var dialogTxt = \"<table border='1px' cellspacing='0' cellpadding='3px'><tr><th style='vertical-align:text-top; text-align: right; background-color: #F0F0F0; font-weight:bold;'>Type</th><td>\" + annotNode.getAttribute('id') + \"</td></tr><tr><th style='vertical-align:text-top; text-align: right; background-color: #F0F0F0; font-weight:bold;'>Features</th><td>\" + feats + \"</td></table>\";"+
		" if(!$(\".ui-dialog\").is(\":visible\")){"+
		"     conf(newDiv).html(dialogTxt);"+
		"     conf(newDiv).dialog({width: 300,maxHeight: 400, resizable: false});"+
		"     conf(newDiv).dialog(\"option\", \"position\", {my: \"left\", at: \"left\", of: event, offset: \"10 70\"});"+
		"     conf(newDiv).dialog(\"open\");"+
		"}}"+
		"function waitAndPop(event, annotTxt){ var timer; $(event.target).mouseover(function(){ timer = window.setTimeout(function(){showAnnotInfo(event, annotTxt);},700);}); $(event.target).mouseleave(function(){window.clearTimeout(timer)});}";

		var scriptText = document.createTextNode(literal);
		script.appendChild(scriptText);

		document.getElementsByTagName('head')[0].appendChild(script);
	},
	
	/**
	* Sends a part of the document for annotation. TBD.
	* @param node starting DOM node
	* @param serviceName NLP service name to execute
	*/
	highlightPartial: function(node, serviceName){
		// TO BE DONE
	}

};

