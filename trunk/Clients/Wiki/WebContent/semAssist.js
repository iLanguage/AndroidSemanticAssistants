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
	/*
	 * This file contains JavaScript functions used to manipulate
	 * a reference page attributes.
	 * 
	 * author: Bahar Sateli
	*/

	window.onload = start;
	
	var proxyServer = "http://loompa.cs.concordia.ca:8080/SA-WikiConnector/SemAssistServlet?action=";

	function start(){
		fixRelativePaths();
		attachListeners();
		populateList();
	}
	
	function fixRelativePaths(){
		var regEx = /^semAssist/ ;
				
		// fix all the image resource paths
		findAndFix("img","src");
		
		//fix all the anchor reference paths
	    findAndFix("a","href");
	    
		// Because this one has exceptions, we do it here manually
	    var links = document.getElementsByTagName("link");
	    for(var i=0; i < links.length; i++){
	    	var link = links[i];
	    	var local = link.getAttribute('href');
	    	// Do not change the SA stylesheet because the relative path is correct according to the proxy server
	    	if(regEx.test(local)){
	    		continue;
	    	}
	    	 
	    	if(local.indexOf("http://") > -1){
	    		continue;
	    	}
	    	var total = add.concat(local);
	    	link.href = total;
	    }
	    
	    // For MediaWiki clones only
	    if(document.getElementById("p-logo")){
	    	var mwLogo = document.getElementById("p-logo");
		    var mwLogoAnchor = mwLogo.getElementsByTagName("a")[0];
		    var src = mwLogoAnchor.getAttribute("style");
		    
		    if(src.indexOf("http://") > -1){
	    		// Nada
	    	}else{
	    		var index = src.indexOf("url") + "url".length + 2;
	    	    var firstPart = src.substring(0,index);
	    	    var secondPart = src.substring(index);
	    	    var finalImg = firstPart.concat(add);
	    	    finalImg = finalImg.concat(secondPart);
	    	    mwLogoAnchor.setAttribute("style", finalImg);
	    	}
	    }
	    
	    if(document.getElementById("searchform")){
			var searchBox = document.getElementById("searchform");
			var searchAction = searchBox.getAttribute("action");
			searchAction = add.concat(searchAction);
			searchBox.setAttribute("action", searchAction);
		}
	}

	function findAndFix(tagName, attrName){
		 	var tags = document.getElementsByTagName(tagName);
		    for(var i=0; i < tags.length; i++){
		    	var tag = tags[i];
		    	var local = tag.getAttribute(attrName);
		    	if(local == null){
		    		continue;
		    	}else if(isRelativePath(local)){
		    		var total = add.concat(local);
			    	tag.setAttribute(attrName, total);	
	    		}else{
	    			// if it is an absolute path, e.g., "http://www.example.com". or a bookmark "#", do not change it.
	    			continue;
	    		}
		    }//for
	}
	
	
	function isRelativePath(urlString){
		if(urlString.indexOf("http://") > -1 || urlString.charAt(0) == "#"){
			return false;
		}else{
			return true;
		}
	}
	
	function attachListeners(){
		
		//$(document).bind("mouseup", addSelectedTextToCookie);

		$(document).delegate("#semAssistServices", "change", getParams);
		
		$(document).delegate("#btnAdd", "click", setCookie);
		
		$(document).delegate("#btnRun", "click", sendList);
		
		$(document).delegate("#btnClear", "click", clearList);
		
		$(document).delegate("#targetSelf", "click", targetSelfOptions);
		
		$(document).delegate("#targetOther", "click", targetOtherOptions);
		
		$(document).delegate("#targetOtherWiki", "click", targetOtherWikiOptions);
		
		$(document).delegate("#semAssistPreServer", "click", semAssistPreServerOptions);
		
		$(document).delegate("#semAssistCusServer", "click", semAssistCusServerOptions);
		
		$(document).delegate("#btnAddServer", "click", AddServer);
		
		$(document).delegate("#btnSetServer", "click", SetServer);
		
		$("#hide").click(function(){
		    if ($("#tabs").is(":hidden")) {
		      $("#tabs").slideDown("slow", function(){
			$("#hide").attr("src","images/contract.png");
			$("#hide").attr("alt","Contract");
		    });
		      
		  } else {
		      $("#tabs").slideUp("slow", function(){
			$("#hide").attr("src","images/expand.png");
			$("#hide").attr("alt","Expand");
		      }); 
		  }
		});

	}
	
	function getParams()
	{
		var xmlhttp;
		if (window.XMLHttpRequest)
			{// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp=new XMLHttpRequest();
		}else{
			// code for IE6, IE5
			xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		}
		
		xmlhttp.onreadystatechange=function(){
		  if (xmlhttp.readyState==4 && xmlhttp.status==200){
			  document.getElementById("saRTParams").innerHTML=xmlhttp.responseText;
		    }
		};
		
		var selectedService = $("#semAssistServices option:selected").val();
		if(selectedService != "dummy"){
			var req = proxyServer.concat("params&serviceName=").concat(selectedService);
			xmlhttp.open("GET",req,true);
			xmlhttp.send();
		}else{
			  document.getElementById("saRTParams").innerHTML="";
		}
	}
		
	function sendList(){
		// self, other or otherWiki
		var target="";
		// body, talk, a page name
		var targetName="";
		var requestString="";
		
		// if target is the same as the source article
		if(isChecked("#targetSelf")){
			target = "self";
			// if the target is body or talk page
			if(isChecked("#self_body")){
				targetName = "body";
			}else if(isChecked("#self_talk")){
				targetName = "talk";
			}else{
				alert("Undefined target for self option.");
				return;
			}
			requestString = "target=" + target + "&" + "targetName=" + targetName;
		}else if(isChecked("#targetOther")){
			// if the target is another article
			target = "other";
			var temp = $("#wikiNamespaces option:selected").val();
			
			if(temp != "dummy"){
				//FIXME not a brilliant idea...
				if($("#wikiNamespaces option:selected").val() == "Main"){
					targetName = $("#targetName").val();
				}else{
					targetName = $("#wikiNamespaces option:selected").val() + ":" + $("#targetName").val();
				}
				requestString = "target=" + target + "&" + "targetName=" + targetName;
			}else{
				alert("Please select a namespace for the target page.");
				return;
			}
		}else if(isChecked("#targetOtherWiki")){
			if($("#otherWikiAddress").val() == "" || $("#otherWikiUser").val() == "" || $("#otherWikiPass").val() == "" || $("#otherWikiTitle").val() == ""){
				alert("Please fill out all of the required fields.");
				return;
			}else{
				$.cookie("otherWikiAddress", $("#otherWikiAddress").val());
				$.cookie("otherWikiUser", $("#otherWikiUser").val());
				$.cookie("otherWikiPass", $("#otherWikiPass").val());
				target="otherWiki";
				targetName = $("#otherWikiTitle").val();
				requestString = "target=" + target + "&" + "targetName=" + targetName;
			}
		}
		
		if($("#semAssistCollection").text() == ""){
			alert("There are no pages in your collection.");
			return;
		}else{
			// Save the runtime parameters
			setRTParams();
			
			var xmlhttp;
			if (window.XMLHttpRequest)
				{// code for IE7+, Firefox, Chrome, Opera, Safari
				xmlhttp=new XMLHttpRequest();
			}else{
				// code for IE6, IE5
				xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			}
			
			xmlhttp.onreadystatechange=function()
			  {  if (xmlhttp.readyState==4 && xmlhttp.status==200)
			    {
				  $("#semAssist-wait").html("");
				  
				  $("#semAssist-console").append(xmlhttp.responseText);
				  var tabs = $("#tabs").tabs();
				  tabs.tabs("select", 3);
				}
			  };
			
			var selectedService = $("#semAssistServices option:selected").val();
			if(selectedService != "dummy"){
				$("#semAssist-wait").html("<img src='images/ajaxSpinner.gif'></img>");
				var links = $.cookie("input-docs");
				var req = proxyServer.concat("invoke&serviceName=").concat(selectedService).concat("&input=").concat(links).concat("&").concat(requestString);
				xmlhttp.open("GET",req,true);
				xmlhttp.send();
			}else{
				alert("Please select a service first.");
				return;
			}
		}
	}
	
	function setCookie() {
		$("#semAssistCollection").append("<option title='"+ ref + "' value='" + ref + "'>" + ref + "</option>");
		//$("#semAssistCollection option:last-child").bind("dblclick", removeLink);

		var cookieText = $.cookie("input-docs");
		cookieText = cookieText + ref + "\|";
		$.cookie("input-docs", cookieText);
		//TODO Prevent adding the page again to the collection
		return null;
	}
	
	function removeLink(){
		var answer = confirm("Do you wish to delete this page from the collection?");
		if (answer){
			  var selectBox = document.getElementById("semAssistCollection");
			  $("#semAssistCollection option:selected").remove();
			  //TODO remove from cookie
		}
	}
	
	function clearList(){
		$("#semAssistCollection").empty();
		$.cookie("input-docs", "");
	}
	
	function populateList(){
		var foundInputs = false;
		var all_cookies = document.cookie.split(";");
		for (var i=0; i< all_cookies.length; i++){
			var cookie_parts = all_cookies[i].split("=");
			if(cookie_parts[0].trim() == "input-docs"){
				if(cookie_parts[1] != ""){
					var links = cookie_parts[1].split("%7C");
					for(var j=0; j < links.length-1; j++){
						var url = decodeURIComponent(links[j]);
						$("#semAssistCollection").append("<option value='" + url + "'>" + url + "</option>");
						$("#semAssistCollection option:last-child").bind("dblclick", removeLink);
					}
					foundInputs = true;
				}
			}
		}
		
		if(foundInputs == false){
			$.cookie("input-docs", "");
		}
	}
	
	function targetSelfOptions(){
		var visibility = $("#targetSelfOptions").css("display");
		if(visibility == "none"){
			$("#targetOtherOptions").hide();
			$("#targetOtherWikiOptions").hide();
			$("#targetSelfOptions").show();
		}
	}
	
	function targetOtherOptions(){
		var visibility = $("#targetOtherOptions").css("display");
		if(visibility == "none"){
			$("#targetSelfOptions").hide();
			$("#targetOtherWikiOptions").hide();
			$("#targetOtherOptions").show();
		}
	}
	
	function targetOtherWikiOptions(){
		var visibility = $("#targetOtherWikiOptions").css("display");
		if(visibility == "none"){
			$("#targetSelfOptions").hide();
			$("#targetOtherOptions").hide();
			$("#targetOtherWikiOptions").show();
		}
	}
	
	function semAssistPreServerOptions(){
		var visibility = $("#semAssistPreServerOptions").css("display");
		if(visibility == "none"){
			$("#semAssistCusServerOptions").hide();
			$("#semAssistPreServerOptions").show();
		}
	}
	
	function semAssistCusServerOptions(){
		var visibility = $("#semAssistCusServerOptions").css("display");
		if(visibility == "none"){
			$("#semAssistPreServerOptions").hide();
			$("#semAssistCusServerOptions").show();
		}
	}
	
	function isChecked(objectName){
		var _test2 = $(objectName);
		return (_test2.is(':checked'));
	}
	
	function setRTParams(){
		var $inputs = $("#saRTParams :input");
		$.cookie("RTParams", "");
		$inputs.each(function() {
	    	var cookieText = $.cookie("RTParams");	    
			cookieText = cookieText + this.name + "=" + $(this).val() + "\|";
			$.cookie("RTParams", cookieText);
	    });
	}
	
	function AddServer(){
		var xmlhttp;
		if (window.XMLHttpRequest)
			{// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp=new XMLHttpRequest();
		}else{
			// code for IE6, IE5
			xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		}
		var host = $("#semAssistCusServerHost").val();
		var port = $("#semAssistCusServerPort").val();
		
		xmlhttp.onreadystatechange=function(){
		  if (xmlhttp.readyState==4 && xmlhttp.status==200){
			  $("#preDefinedServers").append("<option value='" + host.concat(":").concat(port) + "'>" + host.concat(":").concat(port) + "</option>");
		    }
		};
		
		
		if(host != "" && port != ""){
			var req = proxyServer.concat("server&host=").concat(host).concat("&port=").concat(port);
			xmlhttp.open("GET",req,true);
			xmlhttp.send();
		}else{
			alert("Please fill out the fields.");
		}
	}
	
	function SetServer(){
		$.cookie("semassist-server", $("#preDefinedServers option:selected").val());
	}
	
	/*function addSelectedTextToCookie(){
		var literalSeparator = "#: ";
		var selection = getSelectedText();
		if(selection!=''){
			var cookieText = $.cookie("input-docs");
			cookieText = cookieText + literalSeparator + selection.toString() + "\|";
			$.cookie("input-docs", cookieText);
			var temp = literalSeparator + selection.toString().substring(0,40).concat("...");
			$("#semAssistCollection").append("<option value='" + temp + "'>" + temp + "</option>");
			$("#semAssistCollection option:last-child").bind("dblclick", removeLink);
		}
		
		// unselect the text
		if (document.selection){
            document.selection.empty();
        }
        else{
            window.getSelection().removeAllRanges();
        }
	}
	
	function getSelectedText(){
		var selectedText = '';
		if(window.getSelection){
			//selectedText = window.getSelection().toString();
			selectedText = window.getSelection();
		}else if(document.getSelection){
			//returns text;
			selectedText = document.getSelection();
		}else if(document.selection){
			selectedText = document.selection.createRange().text;
		}
		return selectedText;
	}*/
