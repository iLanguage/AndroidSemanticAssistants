var SAUtils = {
	/* log to console */
	log: function(message) {
		var consoleService = Components.classes["@mozilla.org/consoleservice;1"].getService(Components.interfaces.nsIConsoleService);
		consoleService.logStringMessage("Semantic Assistants: " + message);
	}
};