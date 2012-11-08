/** Exposing the SAModule namespace. */
var EXPORTED_SYMBOLS = ["SAModule"];

/** Shortcut to the Firefox components classes. */
const Cc = Components.classes;

/**
 * @class SAModule namespace.
 * @author Bahar Sateli
 */
if ("undefined" == typeof(SAModule)) {
  var SAModule = {
	mainMethod: null,
	
	/**
	* Returns the extension installation location from the Firefox prefernces.
	*/
	getExtensionLocation: function(){
		var pref= Cc["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService);
		var prefBranch= pref.getBranch("extensions.");
		return prefBranch.getCharPref("semanticAssistants.installLocation");
	},
	
	/**
	* Sets the extension installation location in the Firefox prefernces.
	*/
	setExtensionLocation: function(){
	    Components.utils.import("resource://gre/modules/AddonManager.jsm");
		AddonManager.getAddonByID("SemanticAssistants@semanticsoftware.info", function(addon) {
            var pref = Cc["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService);
			var prefBranch= pref.getBranch("extensions.");
			prefBranch.setCharPref("semanticAssistants.installLocation",addon.getResourceURI("").QueryInterface(Components.interfaces.nsIFileURL).file.path.replace(/\\/g,"/"));
        } );
		return;
	},
	
	/**
	* Returns the SAController.jar file location.
	* NOTE: to be removed on the next iteration.
	*/
	getSAControllerJarPath: function(){
		return "file:///" + this.getExtensionLocation() + "/java/SAController.jar";
	},
	
	/**
	* Returns the Java loader .jar file location.
	* NOTE: to be removed on the next iteration.
	*/
	getClassLoaderJarPath: function(){
		return "file:///" + this.getExtensionLocation() + "/java/javaFirefoxExtensionUtils.jar";
	},
	
	/**
	* Returns the CSAL.jar file location.
	* NOTE: to be removed on the next iteration.
	*/
	getCSALJarPath: function(){
		return "file:///" + this.getExtensionLocation() + "/java/CSAL.jar";
	},
	
	/**
	* Initializes all the variables for Firefox Java functionality.
	* NOTE: to be removed on the next iteration
	*/
	initJavaClasses: function(userSelection){
		try {
			var strClass = java.lang.Class.forName("java.lang.String");
			var intClass = java.lang.Class.forName("java.lang.Integer");
			var parameters = java.lang.reflect.Array.newInstance(strClass,1);

			parameters[0] = userSelection;
            var controllerClass = java.lang.Class.forName("info.semanticsoftware.semassist.client.mozilla.SemanticAssistantsController", true, cl);
            this.mainMethod = controllerClass.getMethod("startSemanticAssistants", [parameters.getClass()]);
            var invokeServiceAtIndexMethod = controllerClass.getMethod("invokeServiceAtIndex", [intClass]);
            var getResultsMethod = controllerClass.getMethod("getResults",[]);
            var getAvailableServiceNamesArrayMethod = controllerClass.getMethod("getAvailableServiceNamesArray",[]);
            var getAvailableServiceDescriptionsArrayMethod = controllerClass.getMethod("getAvailableServiceDescriptionsArray",[]);
            var setServerHostMethod = controllerClass.getMethod("setServerHost",[strClass]);
            var setServerPortMethod = controllerClass.getMethod("setServerPort",[strClass]);
        }
        catch(e){
            alert(e);
        }
	},
	
	/**
	* Returns the SAController startSemanticAssistants method.
	*/
	getMainMethod: function(){
		return this.mainMethod;
	},
	
		
	/**
	* Returns name of the last called service from the Firefox preferences.
	*/
	getLastCalledServiceName: function(){
		var pref= Cc["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService);
		var prefBranch= pref.getBranch("extensions.");
		return prefBranch.getCharPref("semanticAssistants.lastCalledService");
	},
	
	/**
	* Sets the given value for the specified key in the Firefox preferences.
	* @param key property name
	* @param value property string value
	*/
	setProperty: function(key, value){
		var pref= Cc["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService);
		var prefBranch= pref.getBranch("extensions.");
		prefBranch.setCharPref("semanticAssistants."+key, value);
	},
	
  };
};

//See https://developer-new.mozilla.org/en-US/docs/XUL_School/JavaScript_Object_Management