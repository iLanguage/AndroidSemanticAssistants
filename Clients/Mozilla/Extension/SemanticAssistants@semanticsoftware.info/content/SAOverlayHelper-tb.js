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

var SAOverlayHelper = {
    
    addToolbarButtonIfFirstTimeRun: function() {
        var pref = Components.classes["@mozilla.org/preferences-service;1"]
                .getService(Components.interfaces.nsIPrefService);
        var firstTimeRun = pref.getBoolPref("extensions.semanticAssistants.firstTimeRun");
        
        //if first time launching the application with the extension installed
        //add the toolbar button to the end of the toolbar
        if (firstTimeRun) {
            var currentset = document.getElementById("mail-bar3").currentSet;
            currentset = currentset + ",availableAssistants-menu-button";
            document.getElementById("mail-bar3").setAttribute("currentset" , currentset);
            document.getElementById("mail-bar3").currentSet = currentset;
            document.persist("mail-bar3", "currentset");
            pref.setBoolPref("extensions.semanticAssistants.firstTimeRun" , false);
        }
    }, 
    
    doOtherInitializationTasks: function() {
        //enables plugins in Thunderbird, which are disabled by default
        pref.setBoolPref("plugin.disable" , false);
    }, 
    
    getMostRecentWindow: function() {
        var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
                .getService(Components.interfaces.nsIWindowMediator);
        return wm.getMostRecentWindow("mail:3pane");
    }, 
    
    //this function is unique to the Thunderbird file due to the custom sidebar
    toggleSidebar: function() {
        
        var sidebarSplitter = top.document.getElementById("sidebarSplitter");
        var sidebarBox = top.document.getElementById("sidebarBox");
        var SASidebarBroadcaster = top.document.getElementById("SASidebarBroadcaster");
        
        if (sidebarBox.hidden) {
            sidebarSplitter.hidden = false;
            sidebarBox.hidden = false;
            
            SASidebarBroadcaster.setAttribute("checked", "true");
            
            var sidebar = top.document.getElementById("sidebar");
            
            // reconstructs the tree if there are results in the message
            sidebar.reload();
        }
        else {
            sidebarSplitter.hidden = true;
            sidebarBox.hidden = true;
            
            SASidebarBroadcaster.setAttribute("checked", "false");
        }
    
    }, 
    
    loadSidebar: function() {
        /*
        //opens an always-on-top window with the results
        window.open('chrome://semanticassistants/content/SASidebarXUL_thunderbird.xul', 
                '',
                'resizable,height=560,width=240,chrome,alwaysRaised');
        */
        
        var sidebarBox = document.getElementById("sidebarBox");
        if (sidebarBox.hidden) {
            var sidebarSplitter = top.document.getElementById("sidebarSplitter");
            sidebarSplitter.hidden = false;
            sidebarBox.hidden = false;
            
            var SASidebarBroadcaster = top.document.getElementById("SASidebarBroadcaster");
            SASidebarBroadcaster.setAttribute("checked", "true");
        }
        document.getElementById("sidebar").reload();
    }, 
    
    doOtherSidebarInitTasks: function() {
        //causes a "tab is undefined" error in the Error Console
        //however, if it is removed, the sidebar does not update when switching message or switching tab (clear itself)
        //the error does not seem to affect anything, so this was opted to be left it for the time being
        top.getBrowser().addProgressListener(SASidebar.progressListener, Components.interfaces.nsIWebProgress.NOTIFY_STATE_DOCUMENT);
    }, 
    
    doOtherSidebarUnloadTasks: function() {
        top.getBrowser().removeProgressListener(SASidebar.progressListener);
    }, 
    
    openUrl: function(url) {
        var messenger = Components.classes['@mozilla.org/messenger;1'].createInstance();
        messenger = messenger.QueryInterface(Components.interfaces.nsIMessenger);
        messenger.launchExternalURL(url);
    }
    
}
