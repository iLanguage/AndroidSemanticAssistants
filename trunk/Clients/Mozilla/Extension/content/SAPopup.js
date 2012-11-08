function findAnnotationNode(event){"+
		"    if(event.target.tagName.toLowerCase() == \"annotation\"){" + 
		" return event.target; " +
		"    }else{"+
		"        var parent = event.target.parentNode;"+
		"        while(parent.tagName.toLowerCase() != \"annotation\"){"
		+"            parent = parent.parentNode;}"+
		"        return parent;}}" + 
		" function showAnnotInfo(event, annotTxt){"+
		" var newDiv = $(document.createElement('div'));"+
		" var dialogTitle = \"\"; if(annotTxt.trim().length > 100){ dialogTitle = annotTxt.trim().substring(0,80) + \"...\";}else{dialogTitle = annotTxt;} "+
		" newDiv.attr(\"title\", dialogTitle);"+
		" var annotNode = this.findAnnotationNode(event);"+
		" var featuresTokens = annotNode.getAttribute(\"features\").trim().split(\"|\");"+
		" var feats = \"\" ;"+
		" feats += \"<dl>\" ;"+
		" for(var i=0; i < featuresTokens.length-1; i++){"+
		" var key = featuresTokens[i].substring(0, featuresTokens[i].indexOf(\"=\")).trim(); if(key.toLowerCase() == \"annotation_level\"){continue;}var value = featuresTokens[i].substring(featuresTokens[i].indexOf(\"=\")+1).trim();"+
		" feats += \"<dt style='font-style: italic;'>\" + key + \"</dt>\"; " +
		" if(value.indexOf(\"http\") > -1){feats += \"<dd><a href='\" + value + \"' target=\'_blank\'>\" + value + \"</a></dd>\";}else{feats += \"<dd>\" + value + \"</dd>\";}" +
		" }"+
		" feats += \"</dl>\"; " +
		" var dialogTxt = \"<table border='1px' cellspacing='0' cellpadding='3px'><tr><th style='vertical-align:text-top; text-align: right; background-color: #F0F0F0; font-weight:bold;'>Type</th><td>\" + annotNode.getAttribute('id') + \"</td></tr><tr><th style='vertical-align:text-top; text-align: right; background-color: #F0F0F0;'>Features</th><td>\" + feats + \"</td></table>\";"+
		" if(!$(\".ui-dialog\").is(\":visible\")){"+
		"     conf(newDiv).html(dialogTxt);"+
		"     conf(newDiv).dialog({width: 300,maxHeight: 400, resizable: false});"+
		"     conf(newDiv).dialog(\"option\", \"position\", {my: \"left\", at: \"left\", of: event, offset: \"10 70\"});"+
		"     conf(newDiv).dialog(\"open\");"+
		"}}"+
		"function waitAndPop(event, annotTxt){ var timer; $(event.target).mouseover(function(){ timer = window.setTimeout(function(){showAnnotInfo(event, annotTxt);},700);}); $(event.target).mouseleave(function(){window.clearTimeout(timer)});}";