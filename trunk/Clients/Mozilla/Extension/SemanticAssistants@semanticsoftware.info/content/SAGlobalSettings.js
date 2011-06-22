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

var SAGlobalSettings = {
    
    loadPreferences : function() {
        var pref = Components.classes["@mozilla.org/preferences-service;1"]
            .getService(Components.interfaces.nsIPrefBranch);
        
        var serverDefaultOrCustomPref = pref.getIntPref("extensions.semanticAssistants.serverDefaultOrCustom");
        var serverCustomHostPref = pref.getCharPref("extensions.semanticAssistants.serverCustomHost");
        var serverCustomPortPref = pref.getCharPref("extensions.semanticAssistants.serverCustomPort");
        
        var maxChromeScriptRunTimePref = pref.getIntPref("dom.max_chrome_script_run_time");

        this.serverDefaultOrCustomRadiogroup = document.getElementById('serverDefaultOrCustom');
        
        this.customServerHostLabel = document.getElementById('customServerHostLabel');
        this.customServerHostTextbox = document.getElementById('customServerHostTextbox');
        this.customServerPortLabel = document.getElementById('customServerPortLabel');
        this.customServerPortTextbox = document.getElementById('customServerPortTextbox');

        this.maxChromeScriptRunTimeTextbox = document.getElementById('maxChromeScriptRunTimeTextbox');

        if (serverDefaultOrCustomPref == 0) {
            this.serverDefaultOrCustomRadiogroup.value = 0;

            this.customServerHostLabel.disabled = true;
            this.customServerHostTextbox.disabled = true;
            this.customServerPortLabel.disabled = true;
            this.customServerPortTextbox.disabled = true;
        }
        else if (serverDefaultOrCustomPref == 1) {
            this.serverDefaultOrCustomRadiogroup.value = 1;
        }
        else { //if the preference is neither 0 nor 1, resets the preference to 0
             pref.setIntPref( "extensions.semanticAssistants.serverDefaultOrCustom" , 0 );
        }

        this.customServerHostTextbox.value = serverCustomHostPref;
        this.customServerPortTextbox.value = serverCustomPortPref;
        
        this.maxChromeScriptRunTimeTextbox.value = maxChromeScriptRunTimePref;
    },

    savePreferences : function() {
        var pref = Components.classes["@mozilla.org/preferences-service;1"]
                .getService(Components.interfaces.nsIPrefBranch);
        
        this.serverDefaultOrCustomRadiogroup = document.getElementById('serverDefaultOrCustom');    
        
        this.customServerHostTextbox = document.getElementById('customServerHostTextbox');
        this.customServerPortTextbox = document.getElementById('customServerPortTextbox');

        this.maxChromeScriptRunTimeTextbox = document.getElementById('maxChromeScriptRunTimeTextbox');

        if (this.serverDefaultOrCustomRadiogroup.value == 0) {
            pref.setIntPref( "extensions.semanticAssistants.serverDefaultOrCustom" , 0 );
        }
        else if (this.serverDefaultOrCustomRadiogroup.value == 1) {
            pref.setIntPref( "extensions.semanticAssistants.serverDefaultOrCustom" , 1 );
        }

        pref.setCharPref( "extensions.semanticAssistants.serverCustomHost" , this.customServerHostTextbox.value );
        pref.setCharPref( "extensions.semanticAssistants.serverCustomPort" , this.customServerPortTextbox.value );
        
        pref.setIntPref( "dom.max_chrome_script_run_time" , this.maxChromeScriptRunTimeTextbox.value );

        return true;
    },

    onCommandDefaultServerRadio : function() {
        this.customServerHostLabel = document.getElementById('customServerHostLabel');
        this.customServerHostTextbox = document.getElementById('customServerHostTextbox');
        this.customServerPortLabel = document.getElementById('customServerPortLabel');
        this.customServerPortTextbox = document.getElementById('customServerPortTextbox');

        this.customServerHostLabel.disabled = true;
        this.customServerHostTextbox.disabled = true;
        this.customServerPortLabel.disabled = true;
        this.customServerPortTextbox.disabled = true;

        return true;
    },

    onCommandCustomServerRadio : function() {
        this.customServerHostLabel = document.getElementById('customServerHostLabel');
        this.customServerHostTextbox = document.getElementById('customServerHostTextbox');
        this.customServerPortLabel = document.getElementById('customServerPortLabel');
        this.customServerPortTextbox = document.getElementById('customServerPortTextbox');
        
        this.customServerHostLabel.disabled = false;
        this.customServerHostTextbox.disabled = false;
        this.customServerPortLabel.disabled = false;
        this.customServerPortTextbox.disabled = false;

        return true;
    }

};
