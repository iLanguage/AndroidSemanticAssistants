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

SAUnderline = {

    // To underline text we create 2 element around the text
    // The 1st element is Layer with 'SA_Underline' id
    // the 2nd element is Layer with the type name as id
    // only the 2nd element contain color or any style
    // to unhighlight the text simply make it a brother of the 2nd element
    underlineText: function(root, node, regexp, annotationContent, annotationType, annotationFeatures, serviceSelected, annotationGroupingName) {
        
        var markany = false;
        var emptyregexp = new RegExp("[ \n\t\r]+" ,"g");
        var color = this.findColor(annotationType);
        var uniqueCount = this.getUniqueCount(root);
        try {
            // check if node is a text node
            if (node && node.nodeName == "#text") {
                // check if parent is not script
                if (node.parentNode.nodeName != "SCRIPT") {
                    // check if parent node is not a SA_Underline highlight
                    if (node.parentNode.parentNode === null || node.parentNode.parentNode.id != "SA_Underline") {
                        // replace
                        var curNode = node;
                        while (regexp.test(curNode.nodeValue)) {
                            var detectionText = annotationContent;
                            var leftContextLength = RegExp.leftContext.length;
                            var detectionTextNorm = annotationContent.replace(emptyregexp, " ");
                            var textM = curNode.splitText(leftContextLength);
                            var textSuf = textM.splitText(detectionText.length);                            
                            
                            // create the 2nd element and insert the selected text
                            var newElem2 = node.ownerDocument.createElement("layer1");
                            newElem2.setAttribute("id", annotationType );
                            newElem2.setAttribute("originalText", annotationGroupingName);
                            newElem2.setAttribute("style", "border-bottom:" + color +" 2px solid;");
                            newElem2.setAttribute("title", annotationType + " " + annotationFeatures);
                            
                            newElem2.className = detectionTextNorm + "_" + uniqueCount;
                            uniqueCount++;
                                     
                            newElem2.appendChild(textM);
                            
                            // create the 1st element and insert the 2nd element
                            var newElem1 = node.ownerDocument.createElement("layer");
                            newElem1.setAttribute("id", "SA_Underline");
                            newElem1.className = serviceSelected;
                            newElem1.appendChild(newElem2);
                            
                            // insert the 1st element into document and move to the next node
                            textSuf.parentNode.insertBefore(newElem1, textSuf);
                            curNode = newElem1.nextSibling;
                            markany = true;
                            
                            this.saveUniqueCount(root,uniqueCount);
                        }
                    }
                }                
                return markany;
            }
            // recursive call for all child nodes in case the result has not yet been found
            if (node  && !markany){
                for (var i=0; i < node.childNodes.length; i++) {
                    //if the result has been found don't look any further
                    if (!markany) {
                        if (this.underlineText(root, node.childNodes[i], regexp, annotationContent, annotationType, annotationFeatures, serviceSelected, annotationGroupingName)){
                            markany = true;
                        }
                    }
                }
            }
        } catch (ex) {
            //alert("underlineText:\n" + ex);
        }
        return markany;
    },
    
    addURLnode: function(node, semAssistResult,  serviceSelected) {
        // create the 2nd element
        var newElem2 = node.ownerDocument.createElement("layer1");
        newElem2.setAttribute("id", "URL" );
        newElem2.setAttribute("originalText", "" );
        newElem2.className = semAssistResult;
        
        // create the 1st element and insert the 2nd element
        var newElem1 = node.ownerDocument.createElement("layer");
        newElem1.setAttribute("id", "SA_URL");
        newElem1.className = serviceSelected;
        newElem1.appendChild(newElem2);

        //insert the 1st element into the node
        node.appendChild(newElem1);
    },
    
    getUniqueCount: function(root){
        var SA_UNIQUECOUNT = 0;
        if (root.body)
        {        
            var typeElement =  root.body.getElementsByTagName("SA_UNIQUECOUNT");        
            var uniqueCountFound = false;
    
            if (typeElement.length > 0)
            {
                if (typeElement[0].id && typeElement[0].id == "SA_UNIQUECOUNT"){
                    uniqueCountFound = true;            
                    SA_UNIQUECOUNT = typeElement[0].getAttribute('SA_UNIQUECOUNT');            
                }
            }    
            if (!uniqueCountFound){
                var newElem1 = root.createElement("SA_UNIQUECOUNT");
                newElem1.setAttribute("id", "SA_UNIQUECOUNT");
                newElem1.setAttribute("SA_UNIQUECOUNT", 0);
                newElem1.className = SA_UNIQUECOUNT;
             
                // insert the 1st element into document and move to the next node
                root.body.appendChild(newElem1);        
            }    
        }        
        return SA_UNIQUECOUNT;

    },
    
    saveUniqueCount: function(root ,count){
        if (root.body){    
            var typeElement =  root.body.getElementsByTagName("SA_UNIQUECOUNT");
            if (typeElement.length > 0)
            {
                if (typeElement[0].id && typeElement[0].id == "SA_UNIQUECOUNT"){
                    uniqueCountFound = true;
            
                    typeElement[0].setAttribute("SA_UNIQUECOUNT", count);            
                }
            }    
            if (!uniqueCountFound){
                var newElem1 = root.createElement("SA_UNIQUECOUNT");
    
                newElem1.setAttribute("id", "SA_UNIQUECOUNT");
                newElem1.setAttribute("SA_UNIQUECOUNT", count);
                newElem1.className = count;
             
                // insert the 1st element into document and move to the next node
                root.body.appendChild(newElem1);            
            }        
        }    
    },

    findColor: function(annotationType) {
        var color = "black";
        if (annotationType == "Person"){
            color = "red";
        //color = "#FF0000";
        }
        else if (annotationType == "Date"){
            color = "blue";
        //color = "#FFB400";
        }
        else if (annotationType == "Location"){
            color = "green";
        //color = "#AEFE00"
        }
        else if (annotationType == "Organization"){
            color = "gray";
        }
        else if (annotationType == "URL"){
            color = "brown";
        }
        else if (annotationType == "Organism"){
            color = "purple";
        }
        else if (annotationType == "NP"){
            color = "orange";
        }
        return color;
    }
};

