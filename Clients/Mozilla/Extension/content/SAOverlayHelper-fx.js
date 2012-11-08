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
 *   Bahar Sateli
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

 /**
 * @class This class helps with attaching the extension GUI widgets to the Firefox overlay.
 */
var SAOverlayHelper = {
    
    /** 
	* Adds the toolbar buttons if the extension is being run for the first time.
	*/
	addToolbarButtonIfFirstTimeRun: function() {
        var pref = Components.classes["@mozilla.org/preferences-service;1"]
                .getService(Components.interfaces.nsIPrefService);
        var firstTimeRun = pref.getBoolPref("extensions.semanticAssistants.firstTimeRun");
        
        //if first time launching the application with the extension installed
        //add the toolbar button to the end of the toolbar
        if (firstTimeRun) {
            var currentset = document.getElementById("nav-bar").currentSet;
            currentset = currentset + ",availableAssistants-menu-button";
            document.getElementById("nav-bar").setAttribute("currentset" , currentset);
            document.getElementById("nav-bar").currentSet = currentset;
            document.persist("nav-bar", "currentset");
            pref.setBoolPref("extensions.semanticAssistants.firstTimeRun" , false);
			pref.setIntPref("extensions.semanticAssistants.lastCalledServiceIndex" , -1);
        }
    },
    
    /**
	* Returns a handler to the Firefox browser window.
	*/
	getMostRecentWindow: function() {
        var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
                .getService(Components.interfaces.nsIWindowMediator);
        return wm.getMostRecentWindow("navigator:browser");
    }, 
    
    /** 
	* Loads the sidebar to the Firefox overlay.
	*/
	loadSidebar: function() {
        //if sidebar was already open, reload it
        if (document.getElementById('SASidebarBroadcaster').hasAttribute('checked')) {
            document.getElementById("sidebar").reload();
        }
        //if the sidebar was not open, open it
        else {
            var browserWindow = this.getMostRecentWindow();
            browserWindow.toggleSidebar('SASidebarBroadcaster', true);
        }
    }, 
    
    /**
	* Attaches a progress listener to the sidebar.
	*/
	doOtherSidebarInitTasks: function() {
		top.getBrowser().addProgressListener(SASidebar.progressListener);
    }, 
    
    /**
	* Removes the sidebar progress listener.
	*/   
	doOtherSidebarUnloadTasks: function() {
        top.getBrowser().removeProgressListener(SASidebar.progressListener);
    }, 
    
    /**
	* Opens the given URL in a new tab.
	* @param url address to open
	*/
	openUrl: function(url) {
        //open in a new window
        //window.open(url);
        
        //open in a new tab
        var mainWindow = window.QueryInterface(Components.interfaces.nsIInterfaceRequestor)
                .getInterface(Components.interfaces.nsIWebNavigation)
                .QueryInterface(Components.interfaces.nsIDocShellTreeItem)
                .rootTreeItem
                .QueryInterface(Components.interfaces.nsIInterfaceRequestor)
                .getInterface(Components.interfaces.nsIDOMWindow);
        mainWindow.gBrowser.addTab(url);
    }   
}
