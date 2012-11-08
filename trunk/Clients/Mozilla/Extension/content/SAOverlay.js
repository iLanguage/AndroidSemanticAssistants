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
 *	 Bahar Sateli
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

/** Imports the SAModule module to this class. */
Components.utils.import("resource://SemanticAssistants/sa_module.js");
var initialized = false;

/**
* @class This is the main extension class.
* @authro Bahar Sateli
* This script is included in SAOverlay-common.xul file. 
*/
var SAOverlay = {

	/**
	* Loads the extension initialization code.
	*/
    onLoad: function() {
        // initialization code
        this.initialized = true;
        
		//FIXME remove this?
		this.strings = document.getElementById("test-strings");

        // if it's the first time the extension is running, add the buttons to the user interface
		SAOverlayHelper.addToolbarButtonIfFirstTimeRun();
		
        // I've refactored all preferences to SAModule file. It should be safe to delete the SAOverlayExtensionLocationHelper.js file from the project.
		//SAOverlayExtensionLocationHelper.saveExtensionLocation();
		SAModule.setExtensionLocation();
    },

    /**
	* Verifies the status of user selection in the page and ivokes the callJava method.
	* Note: we still have to decide how to handle general cases with this function.
	* The allText variable is designed to save the status of the user selection.
	*/
	onMenuItemCommand: function() {
		// assume nothing is selected in the beginning
		var allText = false;

		// check whether user has selected anything on the page (for partial analysis cases)
		var userSelection = content.getSelection();

		// if not, take the whole text visible in the browser window
        if (userSelection == "") {
            goDoCommand("cmd_selectAll");
            userSelection = content.getSelection();
			allText = true;
        }
        //call java classes
        this.callJava(userSelection, allText, false);
    },
	
	/**
	* Delegates a headless service invocation to the {@link{SAUnderline}} class.
	* @param userSelection string containing the user selected text
	* @param allText boolean variable to show if all the page text has been selected
	*/
	headlessCall: function(userSelection, allText) {
		SAUnderline.annotate(window.content.document, null);
	},
	
	/**
	* Presents the Available Assistants dialog to the user and delegates
	* the invocation to the {@link{SAUnderline}} class.
	* @param userSelection string containing the user selected text
	* @param allText boolean variable to show if all the page text has been selected
	*/
	callJava: function(userSelection, allText) {
        
        var urlArray = []; 
		urlArray[0] = new java.net.URL(SAModule.getSAControllerJarPath()); 
        urlArray[1] = new java.net.URL(SAModule.getClassLoaderJarPath());  
        urlArray[2] = new java.net.URL(SAModule.getCSALJarPath());

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

        /* **************
		* BS: we still need to use the window java object
		* to retrieve the list of services. We have to replace this
		* once we have the RESTful SA up and running.
		* The java support will be soon removed from Firefox.
		* see https://bugzilla.mozilla.org/show_bug.cgi?id=748343
		************** */
		
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
                
                //retrieve the available descriptions
                var availableServiceDescriptionsArray = getAvailableServiceDescriptionsArrayMethod.invoke(null, []);

                //create a new object that is to be passed into the Available Assistants dialog
                var availableAssistantsObject = new SAAvailableAssistants();
				
				var selectedAssistantIndexInt = -1;
				
				//open dialog to prompt for the selection of a service
				this.promptService = Components.classes["@mozilla.org/embedcomp/prompt-service;1"].getService(Components.interfaces.nsIPromptService);
				window.openDialog('chrome://SemanticAssistants/content/SAAvailableAssistants.xul', '', 'resizable,height=300,width=680,centerscreen,chrome,modal', availableAssistantsObject, availableServiceNamesArray, availableServiceDescriptionsArray);
				
				selectedAssistantIndexInt = parseInt( availableAssistantsObject.getSelectedAssistantIndex() );
				
                //if a service selection was made
                if ( selectedAssistantIndexInt != -1 ) {
					// store the service name for later headless calls					
					var serviceSelected = availableServiceNamesArray[selectedAssistantIndexInt];		
					SAModule.setProperty("lastCalledService",serviceSelected)

					/* **************
					* BS: for now I removed checking the type of the service result 
					* (e.g., annotation, boundless annotation, etc.)
					* Once we have the RESTful SA up and running, 
					* each service description will present its output type.
					* NOTE that this code assumes we're always getting annotations back.
					************** */
		
					// let SAUnderline class invoke and annotate
					SAUnderline.annotate(window.content.document, serviceSelected);
                }else{
					alert("No service was selected.");
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

    /**
	* Opens the Global Settings prompt dialog.
	*/
	onGlobalSettingsMenuItemCommand: function() {
        window.openDialog('chrome://SemanticAssistants/content/SAGlobalSettings.xul',
            '',
            'resizable,centerscreen,chrome,modal'); // the 3rd argument can also take height=,width= where both must be specified
        return true;
    },

    /**
	* Sets the permission to access and run Java in Firefox.
	* NOTE: To be removed in the next iteration.
	* @param loader
	* @param urls
	*/
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
    },

	/**
	* Prints a log message on the Firefox console.
	* @param message the string to be logged on the console
	*/
	log: function(message) {
		var consoleService = Components.classes["@mozilla.org/consoleservice;1"].getService(Components.interfaces.nsIConsoleService);
		consoleService.logStringMessage("[Semantic Assistants] " + message);
	}
};

window.addEventListener("load", function(e) {
    SAOverlay.onLoad(e);
}, false);

// Annotation helper class
function Annotation(iContent, iType, iStart, iEnd, iFeatures, iGrouping){
	this.content = iContent,
	this.type = iType;
	this.start = iStart;
	this.end = iEnd;
	this.features = iFeatures;
	this.grouping = iGrouping;
}