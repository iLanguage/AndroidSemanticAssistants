/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
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
 * The Original Code is the Semantic Assistants Firefox Extension.
 *
 * The Initial Developer of the Original Code is
 * Semantic Software Lab (http://www.semanticsoftware.info).
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Jason Tan
 *   Kevin Tung
 *   Paola Jimenez
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

const HREF_NO = 'javascript:void(0)';
var initialized = false;

var SAOverlay = {
    onLoad: function() {
        // initialization code
        this.initialized = true;
        this.strings = document.getElementById("test-strings");

        SAOverlayHelper.addToolbarButtonIfFirstTimeRun();
        SAOverlayHelper.doOtherInitializationTasks();

        SAOverlayExtensionLocationHelper.saveExtensionLocation();
    },
    
    getExtensionLocation: function() {
        var pref = Components.classes["@mozilla.org/preferences-service;1"]
                .getService(Components.interfaces.nsIPrefService);
        
        return pref.getCharPref("extensions.semanticAssistants.installLocation");
    }, 

    onMenuItemCommand: function() {
        this.promptService = Components.classes["@mozilla.org/embedcomp/prompt-service;1"]
            .getService(Components.interfaces.nsIPromptService);

        var userSelection = content.getSelection();
        var allText = false;
        
        //if no text was selected, take the whole document
        
        //once text has been selected, the rangeCount will remain 1 even if all text is unselected
        //if (userSelection.rangeCount == 0) {
        if (userSelection == "") {
        
            /*
            //obtains the HTML of the body element's descendants
            userSelection = content.document.body.innerHTML;
            allText = true;
            */
            
            /*
            //obtains the text content of the body element and all of its descendants
            userSelection = content.document.body.textContent;
            allText = true;
            */
            
            goDoCommand("cmd_selectAll");
            userSelection = content.getSelection();
            
        }
        //call java classes
        this.callJava(userSelection, allText);
    },

    callJava: function(userSelection, allText) {
        var extensionPath = SAOverlay.getExtensionLocation();
        
        var SAControllerJarPath = "file:///" + extensionPath + "/java/SAController.jar"; 
        var classLoaderJarPath = "file:///" + extensionPath + "/java/javaFirefoxExtensionUtils.jar";
        var CSALJarPath = "file:///" + extensionPath + "/java/CSAL.jar";
        
        urlArray = []; 
        urlArray[0] = new java.net.URL(SAControllerJarPath); 
        urlArray[1] = new java.net.URL(classLoaderJarPath);  
        urlArray[2] = new java.net.URL(CSALJarPath);  

        var cl = java.net.URLClassLoader.newInstance(urlArray);

        //set security policies with the policyAdd function defined below
        this.policyAdd(cl, urlArray);

        //prepare the arguments that are to be sent to the Java JAR file
        var strClass = java.lang.Class.forName("java.lang.String");
        var intClass = java.lang.Class.forName("java.lang.Integer");
        var parameters = java.lang.reflect.Array.newInstance(strClass,1);

        parameters[0] = userSelection;

        //find the class and the methods
        try {
            var controllerClass = java.lang.Class.forName("info.semanticsoftware.semassist.client.mozilla.SemanticAssistantsController", true, cl);
            var mainMethod = controllerClass.getMethod("startSemanticAssistants", [parameters.getClass()]);
            var invokeServiceAtIndexMethod = controllerClass.getMethod("invokeServiceAtIndex", [intClass]);
            var getResultsMethod = controllerClass.getMethod("getResults",[]);
            var getAvailableServiceNamesArrayMethod = controllerClass.getMethod("getAvailableServiceNamesArray",[]);
            var getAvailableServiceDescriptionsArrayMethod = controllerClass.getMethod("getAvailableServiceDescriptionsArray",[]);
            var setServerHostMethod = controllerClass.getMethod("setServerHost",[strClass]);
            var setServerPortMethod = controllerClass.getMethod("setServerPort",[strClass]);
        }
        catch(e){
            // alert(e);
        }

        //invoke java methods
        try {            
            var prefs = Components.classes["@mozilla.org/preferences-service;1"]
                .getService(Components.interfaces.nsIPrefService);

            //retrieve preference for default server or custom server
            var serverDefaultOrCustom = prefs.getIntPref("extensions.semanticAssistants.serverDefaultOrCustom");

            //if custom server, set the custom server host and port
            if (serverDefaultOrCustom == 1) {
                var serverCustomHost = prefs.getCharPref("extensions.semanticAssistants.serverCustomHost");
                var serverCustomPort = prefs.getCharPref("extensions.semanticAssistants.serverCustomPort");

                setServerHostMethod.invoke(null, [serverCustomHost]);
                setServerPortMethod.invoke(null, [serverCustomPort]);
            }

            //invoke startSemanticAssistants to retrieve the available services
            var mainMethodResults = mainMethod.invoke(null, [parameters]);

            if (mainMethodResults == "") {
                //retrieve the available names
                var availableServiceNamesArray = getAvailableServiceNamesArrayMethod.invoke(null, []);
                
                //retrieve the available names
                var availableServiceDescriptionsArray = getAvailableServiceDescriptionsArrayMethod.invoke(null, []);

                //create a new object that is to be passed into the Available Assistants dialog
                var availableAssistantsObject = new SAAvailableAssistants();

                //open dialog to prompt for the selection of a service
                window.openDialog('chrome://SemanticAssistants/content/SAAvailableAssistants.xul', 
                        '', 'resizable,height=300,width=680,centerscreen,chrome,modal', 
                        availableAssistantsObject, availableServiceNamesArray, availableServiceDescriptionsArray);

                var selectedAssistantIndexInt = parseInt( availableAssistantsObject.getSelectedAssistantIndex() );

                //if a service selection was made
                if ( selectedAssistantIndexInt != -1 ) {
                    //invoke the Java method for the selected service
                    var invokeServiceAtIndexMethodResult = invokeServiceAtIndexMethod.invoke( null , [ selectedAssistantIndexInt ] );
                    
                    if (invokeServiceAtIndexMethodResult == "") {

                        var results = getResultsMethod.invoke(null,[]);
                        var nodeToHighlight; 

                        if (allText === false) {
                            //if some text selection was made, send the first range
                            var range = userSelection.getRangeAt(0);
                            //obtain a document fragment copying the nodes of the range
                            nodeToHighlight = range.cloneContents();
                            //delete the text selection's content from the document (the "replacement" node will be added after processing)
                            range.deleteContents();
                        }
                        else {
                            //if no text selection was made, the whole document is sent for processing
                            nodeToHighlight = content.document.body;
                        }

                        // the results coming from the server
                        var semAssistResult = "";
                        var serviceSelected = "";
                        
                        var openSidebar = true;

                        var browserWindow = SAOverlayHelper.getMostRecentWindow();

                        for (var i = 0; i < results.size(); i++) {
                            var result = results.get(i);
                            serviceSelected = result.getServiceCalled();

                            if (result.getType() == "ANNOTATION") {
                                semAssistResult = result.getResult();
                                var annotationContent = results.get(i).getAnnotationContent();
                                var regexpAC = new RegExp(annotationContent,"g");
                                var annotationType = result.getAnnotationType();
                                var annotationGroupingName = result.getGroupingName();
                                var annotationFeatures = result.getFilledAnnotationFeatures();
                                // process the node "nodeToHighlight" so all the results are hightlighted
                                SAUnderline.underlineText(browserWindow.content.document, 
                                        nodeToHighlight, regexpAC, annotationContent, annotationType, 
                                        annotationFeatures, serviceSelected, annotationGroupingName);
                            }
                            else if (result.getType() == "DOCUMENT") {
                                semAssistResult = result.getResult();
                                SAUnderline.addURLnode(nodeToHighlight, semAssistResult, serviceSelected);
                            }
                            else if (result.getType() == "FILE") {
                                openSidebar = false;
                            }
                        }
                        
                        //once nodeToHighlight has been processed, insert it where the initial text selection was made
                        if (allText === false) {
                            range.insertNode(nodeToHighlight);
                        }
                        
                        if (openSidebar) {
                            window.content.document.body.setAttribute("SAresult", Math.random().toString(16).substring(2));

                            SAOverlayHelper.loadSidebar();
                        }
                        
                    }
                }
            }
            else {
                alert(mainMethodResults);
            }
        }
        catch(e) {
            //alert(e);
        }
    },

    onGlobalSettingsMenuItemCommand: function() {
        window.openDialog('chrome://SemanticAssistants/content/SAGlobalSettings.xul',
            '',
            'resizable,centerscreen,chrome,modal'); // the 3rd argument can also take height=,width= where both must be specified
        return true;
    },

    policyAdd: function(loader, urls) {
        try {
            var str = 'edu.mit.simile.javaFirefoxExtensionUtils.URLSetPolicy';
            var policyClass = java.lang.Class.forName(
                str,
                true,
                loader
                );
            var policy = policyClass.newInstance();
            policy.setOuterPolicy(java.security.Policy.getPolicy());
            java.security.Policy.setPolicy(policy);
            policy.addPermission(new java.security.AllPermission());
            for (var j = 0; j < urls.length; j++) {
                policy.addURL(urls[j]);
            }
        }
        catch(e) {
            alert(e+'::'+e.lineNumber);
        }
    }
};

window.addEventListener("load", function(e) {
    SAOverlay.onLoad(e);
}, false);
