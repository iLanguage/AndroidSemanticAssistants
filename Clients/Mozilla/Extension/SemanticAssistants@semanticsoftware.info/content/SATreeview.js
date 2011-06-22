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

if (!SA) {
    var SA={};
}

SA.treeView = {
    childData: {}, // all the DATA for the tree
    visibleData: [], // a subset of childData that will be visable
    treeBox: null,
    selection: null,
    sortIndex: 0,

    rowCount: function() {
        return this.visibleData.length;
    },

    setTree: function(treeBox) {
        this.treeBox = treeBox;
    },

    getCellText: function(idx) {
        return this.visibleData[idx][0];
    },

    isContainer: function(idx) {
        return this.visibleData[idx][1];
    },

    isContainerOpen: function(idx) {
        return this.visibleData[idx][2];
    },

    isContainerEmpty: function() {
        return false;
    },

    isSeparator: function() {
        return false;
    },

    isSorted: function() {
        return false;
    },
    
    isEditable: function() {
        return false;
    },
    
    getlayerParentIndex:function(idx, layer) {
        var currentLevel = this.getLevel(idx);
        if (currentLevel < layer) {
            return -1;
        }

        for (var t = idx; t >= 0 ; t--) {
            if (this.getLevel(t) == layer) return t;
        }
        return -1;
    },
    
    getParentIndex: function(idx) {
        var currentLevel = this.getLevel(idx);
        if (currentLevel == 0) {
            return -1;
        }
        
        for (var t = idx - 1; t >= 0 ; t--) {
            if (this.getLevel(t) < currentLevel) return t;
        }
        return -1;
    },
    
    getLevel: function(idx) {
        if (this.visibleData[idx]) {
            return this.visibleData[idx][4];
        }
    },
    
    hasNextSibling: function(idx) {
        var thisLevel = this.getLevel(idx);
        for (var t = idx + 1; t < this.visibleData.length; t++) {
            var nextLevel = this.getLevel(t);
            if (nextLevel == thisLevel)
                return true;
            else if (nextLevel < thisLevel)
                return false;
        }
        return false;
    },

    toggleOpenState: function(idx) {
        var item = this.visibleData[idx];
        if (!item[1]) {
            return;
        }
        if (item[2]) {
            item[2] = false;
            var thisLevel = this.getLevel(idx);
            var deletecount = 0;
            for (var t = idx + 1; t < this.visibleData.length; t++) {
                if (this.getLevel(t) > thisLevel)
                    deletecount++;
                else break;
            }
            if (deletecount) {
                this.visibleData.splice(idx + 1, deletecount);
                this.treeBox.rowCountChanged(idx + 1, -deletecount);
            }
        }
        else { 
            item[2] = true;
            var label = this.visibleData[idx][5];
            
            //level 0
            if (this.getLevel(idx) == 0) {
                var count = 0;
                for (var t in this.childData[label]){
                    this.visibleData.splice(idx + count + 1, 0, [t, true, false, t, 1, t]);
                    count++;
                }
                
                this.treeBox.rowCountChanged(idx + 1, count);
            }
            //level 1
            else if (this.getLevel(idx) == 1) {
                var parentIndex = this.getParentIndex(idx);
                
                if (parentIndex < 0 ) {
                    return;
                }
                var parentLabel = this.visibleData[parentIndex][5];

                if( this.childData[parentLabel][label]) {
                    var toinsert = this.childData[parentLabel][label][0];

                    for (var i = 0; i < toinsert.length; i++) {
                        var check = this.childData[parentLabel][label][toinsert[i]];
                        if (check.length > 1){
                            this.visibleData.splice(idx + i + 1, 0, [toinsert[i] + " (" + check.length + ")", true, false, label, 2, toinsert[i]]);
                        }
                        else
                        {
                            if (parentLabel == "OrganismTagger"){
                                this.visibleData.splice(idx + i + 1, 0, [toinsert[i] + " (" + check.length + ")", true, false, label, 2, toinsert[i]]);
                            }
                            else {
                                this.visibleData.splice(idx + i + 1, 0, [toinsert[i], false, "", label, 2, check[0]]);
                            }
                        }
                    }
                    
                    this.treeBox.rowCountChanged(idx + 1, toinsert.length);
                }
            }
            //level 2
            else {
                parentIndex = this.getParentIndex(idx);
                var parentparentIndex = this.getParentIndex(parentIndex);
                if (parentIndex < 0 || parentparentIndex < 0) {
                    return;
                }
                parentLabel = this.visibleData[parentIndex][5];
                var parentparentLabel =  this.visibleData[parentparentIndex][5];

                if ( this.childData[parentparentLabel][parentLabel][label] ) {
                    toinsert = this.childData[parentparentLabel][parentLabel][label];
                
                    for (i = 0; i < toinsert.length; i++) {
                        var thisName = toinsert[i].substr(0,  toinsert[i].lastIndexOf("_"))
                        this.visibleData.splice(idx + i + 1, 0, [thisName, false,  false, parentLabel, 3, toinsert[i]]);
                    }
                    
                    this.treeBox.rowCountChanged(idx + 1, toinsert.length);
                }
            }
        }
    },

    getImageSrc: function(idx, column) {},

    getProgressMode : function(idx,column) {},

    getCellValue: function(idx, column) {},

    cycleHeader: function(col, elem) {},

    selectionChanged: function() {},

    cycleCell: function(idx, column) {},

    performAction: function(action) {},

    performActionOnCell: function(action, index, column) {},

    getColumnProperties: function(column, element, props) {},

    getCellProperties: function(row, column, props) {},

    getRowProperties: function(row, props) {
        if (this.getLevel(row) == 1) {
            var aserv=Components.classes["@mozilla.org/atom-service;1"].getService(Components.interfaces.nsIAtomService);      
            props.AppendElement(aserv.getAtom(this.visibleData[row][3]));
        }
    },
    
    getSelectedRows: function() {
        var start = {};
        var end = {};
        var numRanges = this.selection.getRangeCount();
        var selectedElements = [];
        for (var t=0; t<numRanges; t++) {
            this.selection.getRangeAt(t,start,end);
            for (var v=start.value; v<=end.value; v++) {
                selectedElements.push(v);
            }
        }
        return selectedElements;
    },

    addVisibleData: function() {
        this.treeBox.rowCountChanged(0, this.visibleData.length );
    },
    
    refresh: function() {
        this.treeBox.invalidate();
    },
    
    setSortIndex: function(idx) {
        if (idx == this.sortIndex)
            return;
        this.sortIndex = idx;
        var label = "";
        var innerCount = 0;
        // replace all childs in visible data to the new sort index
        for (var i = 0; i < this.visibleData.length; i++) {
        
            if (!this.visibleData[i][1]) {
                this.visibleData[i][0] = this.childData[label][this.sortIndex][innerCount++];
                this.treeBox.invalidateRow(i);
            } else {
                label = this.visibleData[i][0];
                innerCount = 0;
            }
        }
    },

    deleteAll: function() {
        this.treeBox.rowCountChanged(0, -this.visibleData.length );
        this.childData = {};
        this.visibleData = [];
    }
    
};
