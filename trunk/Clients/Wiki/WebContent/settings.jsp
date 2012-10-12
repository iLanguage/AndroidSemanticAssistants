<!--
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2012, 2013 Semantic Software Lab, http://www.semanticsoftware.info
Rene Witte
Bahar Sateli

The Semantic Assistants architecture is free software: you can
redistribute and/or modify it under the terms of the GNU Affero General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->
<%@page import="info.semanticsoftware.semassist.client.wiki.html.HTMLGenerator"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Semantic Assistants Wiki-NLP Integration - Settings</title>
<script type="text/javascript" src="jquery-latest.js"></script>
<script type="text/javascript" src="jquery.cookie.js"></script>
<!-- default stylesheet -->
<link rel="stylesheet" type="text/css" href="one-column.css" media="screen, tv, projection" title="Default" />

<script>
	window.onload = function start(){
		$(document).delegate("#btnSetCookies", "click", setCookie);
		// fill the fields
		var address = document.referrer;
		var index = address.lastIndexOf("index.php")+"index.php".length;
		address = address.substring(0,index);
		$("#thisWikiAddress").val(address);
	};
	
	function setCookie(){
		$.cookie("thisWikiAddress", $("#thisWikiAddress").val());
		$.cookie("thisWikiUser", $("#thisWikiUser").val());
		$.cookie("thisWikiPass", $("#thisWikiPass").val());
		$.cookie("thisWikiEngine", $("#supportedWikiEngines option:selected").val());
		$.cookie("semassist-server", $("#preDefinedServers option:selected").val());
	    parent.window.back();
		return false;	
	}
</script>
</head>
<body>
<div id="logo" class="page">

        <a href="http://www.semanticsoftware.info" title="Semantic Assistants"><span id="logo_black">Semantic Assistants</span></a>
</div>

<div id="contentbox" class="page">
	<div id="padding">
	<form>
	<table>
		<tr>
			<td colspan=2>
				<p>
					<span>Before you proceed, you have to specify the followings: (all fields are required)</span>
				</p>
			</td>
		</tr>
		<tr>
		<td>
			<label for="#supportedWikiEngines">Wiki Engine</label>
		</td>
		<td>
			<%= HTMLGenerator.supportedWikisCombobox() %>
			<script type="text/javascript">
			$('option[value="MediaWiki-1.16"]').attr('selected','selected');
			</script>
		</td>
		</tr>
		<tr>
			<td>
				<label for="#thisWikiAddress">Wiki Address</label>
			</td>
			<td>
				<input id="thisWikiAddress" type="text" style="width:400px;"/>		
			</td>
		</tr>
		<tr>
			<td>
				<label for="#thisWikiUser">Username</label>
			</td>
			<td>
				<input id="thisWikiUser" type="text" style="width:250px;"/>		
			</td>
		</tr>
		<tr>
			<td>
				<label for="#thisWikiPass">Password</label>
			</td>
			<td>
				<input id="thisWikiPass" type="password" style="width:250px;"/>		
			</td>
		</tr>
		<tr>
			<td>
				<label for="#preDefinedServers">Semantic Assistants Server</label>
			</td>
			<td>
				<%= HTMLGenerator.preDefinedServers() %>
			</td>
		</tr>
		<tr>
			<td style="text-align:right;" colspan=2>
				<input id="btnSetCookies" type="submit" value="Save"/>	
			</td>
		</tr>
	</table>
</form>
	</div>

    </div>

    <div id="menu" class="page">
        <a href="http://www.semanticsoftware.info" target="_blank">Semantic Software Lab</a>
    </div>
</body>
</html>
