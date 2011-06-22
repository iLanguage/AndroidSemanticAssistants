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
 * The Original Code is ClearForest Gnosis.
 *
 * The Initial Developer of the Original Code is
 * ClearForest, Thomson Reuters, Yoav Karpeles.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Jason Tan
 *   Kevin Tung
 *   Paola Jimenez
 *
 * ***** END LICENSE BLOCK ***** */

SASidebar = {};

SASidebar.progressListener = {
    lastState: null,
    stateIsRequest: false,
    selectedElement: null,
    
    QueryInterface: function(aIID) {
        if (aIID.equals(Components.interfaces.nsIWebProgressListener) || aIID.equals(Components.interfaces.nsISupportsWeakReference) || aIID.equals(Components.interfaces.nsISupports))
            return this;
        throw Components.results.NS_NOINTERFACE;
    },

    onStateChange: function(aProgress,aRequest,aFlag,aStatus) {
        if(aFlag & Components.interfaces.nsIWebProgressListener.STATE_STOP) {
            // refresh only if content has changed and not just css or some other ajax
            
            var browserWindow = SAOverlayHelper.getMostRecentWindow();
            
            var newState = SASidebar.getState(browserWindow.content);
            if (newState != this.lastState) {
                this.lastState = newState;
                SASidebar.searchForCLFLayers();
            }
        }
        return 0;
    },

    onLocationChange: function(aProgress,aRequest,aLocation) {
        
        var browserWindow = SAOverlayHelper.getMostRecentWindow();
        
        this.lastState = SASidebar.getState(browserWindow.content);
        SASidebar.searchForCLFLayers();
        return 0;
    },

    onProgressChange: function(a,b,c,d,e,f) {},

    onStatusChange: function(a,b,c,d) {},

    onSecurityChange: function(a,b,c) {},
    
    onLinkIconAvailable: function(a) {}
};

SASidebar.init = function() {
    document.getElementById("clf-tree").view = SA.treeView;
    this.searchForCLFLayers();
    SAOverlayHelper.doOtherSidebarInitTasks();
    //top.getBrowser().addProgressListener(SASidebar.progressListener, Components.interfaces.nsIWebProgress.NOTIFY_STATE_DOCUMENT);
};

SASidebar.unload = function() {
    SAOverlayHelper.doOtherSidebarUnloadTasks();
    //top.getBrowser().removeProgressListener(SASidebar.progressListener);
};

SASidebar.getState = function(win) {
    var sign;
    if (win.document.body)
        sign = win.document.body.getAttribute("SAresult");
    return sign;
};

SASidebar.searchForCLFLayers = function() {
    this.selectedElement = null;
    var i;
    // Sort functions
    var sort0 = function(a,b){
        var aa = a.toLowerCase();
        var bb = b.toLowerCase();
        if (aa < bb) return -1;
        if (aa > bb) return 1;
        return 0;
    };
    var sort1 = function(a,b){
        var aa = parseInt(a.substr(a.lastIndexOf("(")+1));
        var bb = parseInt(b.substr(b.lastIndexOf("(")+1));
        if (aa < bb) return 1;
        if (aa > bb) return -1;
        return 0;
    };
    var sort2 = function(a,b){
        var aa = parseInt(a.substr(a.lastIndexOf("(")+1));
        var bb = parseInt(b.substr(b.lastIndexOf("(")+1));
        if (aa < bb) return 1;
        if (aa > bb) return -1;
        return 0;
    };
    try {
        SA.treeView.deleteAll();
        
        var browserWindow = SAOverlayHelper.getMostRecentWindow();
        
        var done = {};
        
        this.searchForCLFLayersInFrames(browserWindow.content, done);
        
        /*
        // Adding the topics
        var box = document.getElementById("topics");
        while (box.firstChild)
            box.removeChild(box.firstChild);
        for (i in done._topic) {
            var t = document.createElement("label");
            t.setAttribute("value", done._topic[i]);
            box.appendChild(t);
        }
        */
        
        // Adding the tree childs
        for (i in done) {
            SA.treeView.childData[i] = {};
            
            for (var t in done[i]){
                SA.treeView.childData[i][t] = [];
                SA.treeView.childData[i][t][0] = [];
                SA.treeView.childData[i][t][1] = [];
                SA.treeView.childData[i][t][2] = [];

                for (var j in done[i][t]) {
                    var count = 0;
                    for (var u in done[i][t][j]){
                        count ++;
                    }
                    var nameCount = j;
                    SA.treeView.childData[i][t][0].splice(0, 0, nameCount);
                    SA.treeView.childData[i][t][1].splice(0, 0, nameCount);
                    SA.treeView.childData[i][t][nameCount] = [];
                    for (var u in done[i][t][j]){
                        SA.treeView.childData[i][t][nameCount].splice(0, 0, u);
                    }
                }
                SA.treeView.childData[i][t][0].sort(sort0);
            }
        }

        // Add nodes to tree
        SA.treeView.addVisibleData();
        // Sorting
        SA.treeView.visibleData.sort();
        this.treeSortBy(null);
        //SA.treeView.refresh();
    } catch (ex) {
        //alert("searchForCLFLayers: " + ex);
    }
};

SASidebar.RXSmall2Capital = /([a-z])([A-Z])/g;

SASidebar.searchForCLFLayersInFrames = function(win, done) {
    var root = win.document.body;
    var i = 0;
    if (root) {
        var typeElement = root.getElementsByTagName("layer");
        for (i = 0; i < typeElement.length; i++) {
            if (typeElement[i].id && typeElement[i].id == "SA_Underline") {
                
                var innerLayer = typeElement[i].childNodes[0];
                var specificResult = innerLayer.getAttribute('originalText');
                var serviceType = typeElement[i].className;
                var typeName = innerLayer.id.replace(this.RXSmall2Capital, "$1 $2");
                if (!done[serviceType]) {
                    done[serviceType] = {};
                    SA.treeView.visibleData.splice(0, 0, [serviceType, true, false, "", 0, serviceType]);
                }
                var childName = innerLayer.className;
                
                if (!done[serviceType][typeName]) {
                    done[serviceType][typeName] = {};
                
                }
                if (!done[serviceType][typeName][specificResult]) {
                    done[serviceType][typeName][specificResult] = {};
                }
                if (!done[serviceType][typeName][specificResult][childName])
                    done[serviceType][typeName][specificResult][childName] = childName;
            }
            
            //for the URL
            if (typeElement[i].id && typeElement[i].id == "SA_URL") {
                innerLayer = typeElement[i].childNodes[0];
                serviceType = typeElement[i].className;
                typeName = innerLayer.id.replace(this.RXSmall2Capital, "$1 $2");
                if (!done[serviceType]) {
                    done[serviceType] = {};
                    SA.treeView.visibleData.splice(0, 0, [serviceType, true, false, "", 0, serviceType]);
                }
                childName = innerLayer.className;
                if (!done[serviceType][typeName]) {
                    done[serviceType][typeName] = {};
                
                }
                if (!done[serviceType][typeName][childName]) {
                    done[serviceType][typeName][childName] = {};
                }
                if (!done[serviceType][typeName][childName][childName])
                    done[serviceType][typeName][childName][childName] = childName;
            }
        }
    }
    if (win.frames.length > 0)
        for (i = 0; i < win.frames.length; i++)
            this.searchForCLFLayersInFrames(win.frames[i], done);
};

SASidebar.selectChange = function() {
    this.selectedElement = null;
    // calculating selected elements
    var selectedElements = null;
    var selectedRows = SA.treeView.getSelectedRows();
    for (var i=0; i<selectedRows.length; i++) {
        var v = selectedRows[i];
        var serviceName;
        var childName = "*";
        var typeName = "*";
        var bgcolor;
        var originalText = "*";
        if (SA.treeView.isContainer(v)) {
            var sIdx = SA.treeView.getlayerParentIndex(v,0);
            serviceName = SA.treeView.visibleData[sIdx][5];
            var pIdx = SA.treeView.getlayerParentIndex(v, 1);
            if (pIdx > -1){
                typeName = SA.treeView.visibleData[pIdx][5];
            }
            
            var currentIdx = SA.treeView.getLevel(v);
            if (currentIdx == 2) {
                originalText = SA.treeView.visibleData[v][0];
                originalText = originalText.substr(0, originalText.lastIndexOf("(") - 1);
            }
            bgcolor = SA.treeView.visibleData[v][3];

        } else {    
            bgcolor = SA.treeView.visibleData[v][3];
            if (bgcolor != "URL") {
                sIdx = SA.treeView.getlayerParentIndex(v,0);
                serviceName = SA.treeView.visibleData[sIdx][5];
                pIdx = SA.treeView.getlayerParentIndex(v, 1);
                typeName = SA.treeView.visibleData[pIdx][5];

                var origIdx = SA.treeView.getlayerParentIndex(v, 2);
                if (origIdx > -1 && v != origIdx ) {
                    originalText = SA.treeView.visibleData[origIdx][0];
                    originalText = originalText.substr(0, originalText.lastIndexOf("(") - 1);
                }
                else {
                    originalText = SA.treeView.visibleData[v][0];
                }
                childName = SA.treeView.visibleData[v][5].replace(/ \([^\(]+\)$/, "");
            }
            else {
                
                //open the URL
                
                SAOverlayHelper.openUrl(SA.treeView.visibleData[v][5]);
                
            }
        }
        typeName = typeName.replace(/ /g, "");
        if (!selectedElements)
            selectedElements = {};
        if (!selectedElements[serviceName])
            selectedElements[serviceName]= {};
        if (!selectedElements[serviceName][typeName])
            selectedElements[serviceName][typeName] = {};
        if (!selectedElements[serviceName][typeName][originalText])
            selectedElements[serviceName][typeName][originalText]= {};
            
        selectedElements[serviceName][typeName][originalText][childName.toLowerCase()] = bgcolor;
    }
    this.highlightTree(selectedElements);
};

SASidebar.highlightTree = function(selectedElements) {
    // highlight selected elements
    
    var browserWindow = SAOverlayHelper.getMostRecentWindow();
    
    var topElement = this.highlightTreeInFrames(browserWindow.content, selectedElements);
    if (topElement)
        this.ensureVisible(topElement);
};

SASidebar.highlightTreeInFrames = function(win, selectedElements) {
    var root = win.document.body;
    var i = 0;
    var topElement = null;
    var offsetTopElement = null;
    if (root) {
        var typeElement = root.getElementsByTagName("layer");
        for (i = 0; i < typeElement.length; i++) {
            if (typeElement[i].id && typeElement[i].id == "SA_Underline") {
                var innerLayer = typeElement[i].childNodes[0];
                var typeName = innerLayer.id;
                var childName = innerLayer.className.toLowerCase();
                var serviceName = typeElement[i].className;
                var originalText = innerLayer.getAttribute('originalText');
                var highlight = false;
                if (!selectedElements) // nothing is selected highlight all
                    this.highlightText(typeElement[i]);
                else {
                    if (selectedElements[serviceName]){
                        if(selectedElements[serviceName]["*"]) {
                            highlight = true;
                        }
                        else if (selectedElements[serviceName][typeName]) {
                            if(selectedElements[serviceName][typeName]["*"]) {
                                highlight = true;
                            }
                            else if (selectedElements[serviceName][typeName][originalText]) {
                                if (selectedElements[serviceName][typeName][originalText]["*"] || selectedElements[serviceName][typeName][originalText][childName]) {
                                    highlight = true;
                                }
                            }
                        }
                    }
                    if (highlight) {
                        this.highlightText(typeElement[i]);
                        var oTop = this.trueOffset(typeElement[i])[0];
                        if (oTop !== 0 && (offsetTopElement === null || offsetTopElement > oTop)) {
                            offsetTopElement = oTop;
                            topElement = typeElement[i];
                        }
                    }
                    else {
                        this.unhighlightText(typeElement[i]);
                    }
                }
            }
        }
    }
    if (win.frames.length > 0){
        for (i = 0; i<win.frames.length; i++) {
            var frmTopElem = this.highlightTreeInFrames(win.frames[i], selectedElements);
            if (frmTopElem)
                if (topElement === null || offsetTopElement < this.trueOffset(frmTopElem)[0])
                    topElement = frmTopElem;
        }
    }
    return topElement;
};

SASidebar.ensureVisible = function(node) {
    // ensure visible
    var win = this.getNodeWindow(node);
    var o = this.trueOffset(node);
    var oTop = o[0];
    var oLeft = o[1];
    if (oTop + oLeft === 0) return false;
    if (oLeft < win.scrollX + win.innerWidth / 3 || oLeft > win.scrollX + win.innerWidth - 32)
        oLeft -= 2 * (win.innerWidth / 3);
    else
        oLeft = win.scrollX;
    if (oTop < win.scrollY + 4 || oTop > win.scrollY + win.innerHeight - 32)
        oTop -= (win.innerHeight / 3);
    else
        oTop = win.scrollY;
    win.scrollTo(oLeft, oTop);
    return true;
};

SASidebar.trueOffset = function(element) {
    var valueT = 0, valueL = 0;
    do {
        valueT += element.offsetTop  || 0;
        valueL += element.offsetLeft || 0;
        element = element.offsetParent;
    } while (element);
    return [valueT, valueL];
};

SASidebar.getNodeWindow = function(element) {
    var elemParent = element.ownerDocument;
    
    var browserWindow = SAOverlayHelper.getMostRecentWindow();
    
    var wind = this.searchForBody(elemParent, browserWindow.content);
    return wind;
};

SASidebar.searchForBody = function(docElem, win) {
    if (win.document == docElem)
        return win;
    if (win.frames.length > 0)
        for (var i=0; i<win.frames.length; i++) {
            var ret = this.searchForBody(docElem, win.frames[i]);
            if (ret)
                return ret;
        }
    return null;
};

SASidebar.highlightText = function(layer) {
    if (layer.childNodes.length == 1)
        return;
    layer.childNodes[0].appendChild(layer.childNodes[1]);
};

SASidebar.unhighlightText = function(layer) {
    if (layer.childNodes.length == 2)
        return;
    layer.appendChild(layer.childNodes[0].childNodes[0]);
};

SASidebar.treeCollapseAll = function() {
    // good for 2 level tree only
    var count = SA.treeView.rowCount()-1;
    for (var i=count; i>=0; i--) {
        if (SA.treeView.isContainer(i))
            if (SA.treeView.isContainerOpen(i))
                SA.treeView.toggleOpenState(i);
    }
};

SASidebar.treeExpandAll = function() {
    // good for 2 level tree only
    var count = SA.treeView.rowCount()-1;
    for (var i=count; i>=0; i--) {
        if (SA.treeView.isContainer(i))
            if (!SA.treeView.isContainerOpen(i))
                SA.treeView.toggleOpenState(i);
    }
};

SASidebar.treeUnderlineAll = function() {
    SA.treeView.selection.clearSelection();
    this.highlightTree(null);
};

SASidebar.treeUnderlineNone = function() {
    SA.treeView.selection.clearSelection();
    var emptyHash = {};
    this.highlightTree(emptyHash);
};

SASidebar.treeFindNext = function() {
    this.treeFind(true);
};

SASidebar.treeFindPrev = function() {
    this.treeFind(false);
};

SASidebar.treeSortBy = function(val) {
    if (val === null)
        val = 0;
    else {
        if (val < 0) {
            val = 0 + 1;
            if (val > 2)
                val = 0;
            var treeSortRadios = document.getElementById("treeSortRadio").childNodes;
            for (var i = 0; i < treeSortRadios.length; i++)
                treeSortRadios[i].setAttribute("checked", i == val);
        }
    }
    SA.treeView.setSortIndex(0);
};

SASidebar.treeFind = function(fDirection) {
    var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"].getService(Components.interfaces.nsIWindowMediator);
    
    var browserWindow = SAOverlayHelper.getMostRecentWindow();
    
    var takeNext = false;
    if (this.selectedElement === null)
        takeNext = true;
    var foundElement = this.findNextLayer(browserWindow.content, fDirection, takeNext)[0];
    if (!foundElement && !takeNext)
        this.findNextLayer(browserWindow.content, fDirection, true);
};

SASidebar.findNextLayer = function(win, fDirection, takeNext) {
    var docBody = win.document.body;
    var i;
    if (docBody) {
        var typeElement = docBody.getElementsByTagName("layer");
        var foundElement = null;
        i = (fDirection)?0:typeElement.length - 1;
        for (; i !=- 1 && i != typeElement.length; (fDirection)?i++:i--) {
            if (typeElement[i].id && typeElement[i].id == "SA_Underline") {
                if (typeElement[i].childNodes.length == 1) {
                    if (takeNext) {
                        foundElement = typeElement[i];
                        this.selectedElement = foundElement;
                        win.focus();
                        win.getSelection().selectAllChildren(foundElement);
                        if (this.ensureVisible(foundElement))
                            break;
                        else
                            foundElement = null;
                    }
                    if (typeElement[i] == this.selectedElement)
                        takeNext = true;
                }
            }
        }
        if (foundElement)
            return [foundElement, takeNext];
    }
    if (win.frames.length > 0) {
        i = (fDirection)?0:win.frames.length - 1;
        for (; i !=- 1 && i != win.frames.length; (fDirection)?i++:i--) {
            var fetn = this.findNextLayer(win.frames[i], fDirection, takeNext);
            if (fetn[0])
                return fetn;
            if (fetn[1])
                takeNext = fetn[1];
        }
    }
    return [null, takeNext];
};