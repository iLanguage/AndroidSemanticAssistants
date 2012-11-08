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
 *	Bahar Sateli
 *  Jason Tan
 *  Kevin Tung
 *  Paola Jimenez
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

	var colorMap={};
	colorMap["EnzymeStats"] = "00FFFF";	//0,255,255 	C#11 	Turquoise
	colorMap["Enzyme"] = "00CCFF";	//0,204,255 	C#10 	Light blue
	colorMap["OrganismStats"] = "FF6633";	//255,102,51 	C#10 	Tomato soup
	colorMap["Organism"] = "FFCC66";	//255,204,102	C#11 	Light mustard
	colorMap["SubstrateStats"] = "CCFF00";	//204,255,0 	11 	Chartreuse
	colorMap["SubstrateSpecificity"] = "FFCC00";	//255,204,0 	10 	Saffron / Gold-Orange
	colorMap["Substrate"] = "FFFF66";	//255,255,102 	12 	Light Yellow 
	colorMap["AccessionNumber"] = "FF0000";	//255,0,0 	1 	Red
	colorMap["ActivityAssayConditions"] = "008000";	//0,128,0 	5 	Green - true green
	colorMap["Assay"] = "99CC99";	//153,204,153 	10 	Light olive green / Sea Green
	colorMap["Family"] = "33FFCC";	//51,255,204 	9 	Aqua
	colorMap["Gene"] = "FF9933";	//255,153,51 	1 	Pumpkin
	colorMap["Glycoside_Hydrolase"] = "66FF99";	//102,255,153 	11 	Light dull green
	colorMap["Glycosylation"] = "CC3300";	//204,51,0 	8 	Red-Orange
	colorMap["Host"] = "FF9900";	//255,153,0 	6 	Light Orange
	colorMap["KineticAssayConditions"] = "FF00FF";	//255,0,255 	9 	Pink / Fuscia
	colorMap["Kinetics"] = "CC99CC";	//204,153,204 	12 	Light Plum 
	colorMap["Laccase"] = "66FF66";	//102,255,102 	12 	Light grass green
	colorMap["Lipase"] = "7ADAE1";	//122,218,225 	  	Sky blue color
	colorMap["Peroxidase"] = "CC99FF";	//204,153,255 	11 	Lilac
	colorMap["pH"] = "00FF00";	//0,255,0 	1 	Lime Green - very bright!
	colorMap["PMID"] = "CC3366";	//204,51,102 	7 	Mauve / Wine
	colorMap["ProductAnalysis"] = "FF6600";	//255,102,0 	4 	Orange
	colorMap["Reaction"] = "CC6600";	//204,102,0 	2 	Sienna / Orange - brown
	colorMap["SpecificActivity"] = "990000";	//153,0,0 	2 	Brick Red
	colorMap["Strain"] = "CCFF66";	//204,255,102 	9 	Light Chartreuse
	colorMap["Temperature"] = "6633FF";	//102,51,255 	7 	Periwinkle or lavender blue
	
/**
* @class This class handles the extension user settings.
*/
var SAGlobalSettings = {
    
    /**
	* Loads the user settings from the Firefox preferences and updates the GUI accordingly.
	*/
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

    /**
	* Saves the user settings to the Firefox preferences.
	*/
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

	    /**
	* Disables the custom server information widgets.
	*/
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

    /**
	* Disables the default server information widgets.
	*/
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
    },

	/**
	* Populates the color list in the Appearance tab.
	*/
	populateColorsList: function(){
		var listbox = document.getElementById("lstAnnotTypes");
		var listItem = null;
		if ( listbox.hasChildNodes() ){
			while ( listbox.childNodes.length >= 1 ){
				listbox.removeChild( listbox.firstChild );       
			} 
		}
		for (var key in colorMap) {
			listItem = document.createElement("listitem");
			listItem.setAttribute("value", key);
			listItem.setAttribute("label", key);
			listItem.setAttribute("style", "background-color: #" + colorMap[key] + ";");
			listbox.appendChild(listItem);
		}
	}
};
