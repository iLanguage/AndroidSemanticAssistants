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
<%@page import="info.semanticsoftware.semassist.client.wiki.utils.ConsoleLogger"%>
<%@page import="info.semanticsoftware.semassist.client.wiki.servlets.SemAssistServlet"%>
<%@page import="info.semanticsoftware.semassist.client.wiki.command.ProxyCommand"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>Semantic Assistants</title>
<script type="text/javascript" src="jquery-latest.js"></script>
<script type="text/javascript" src="jquery.cookie.js"></script>
<script type="text/javascript" src="semAssist.js"></script>
<script type="text/javascript" src="jquery-ui-1.8.16.custom.min.js"></script>
<script src="jquery.ui.core.js"></script>
<script src="jquery.ui.widget.js"></script>
<script src="jquery.ui.tabs.js"></script>
<link rel="stylesheet" href="semAssist/themes/base/jquery.ui.all.css">	
<link rel="stylesheet" href="semAssist.css">
<%= ProxyCommand.add %>
<!-- script type="text/javascript" src="http://code.jquery.com/jquery-latest.js"></script -->
<!-- content from the referrer page -->
<% SemAssistServlet servlet = new SemAssistServlet();%>
<%= servlet.getHeadContent(SemAssistServlet.refererURL, request) %>
	<script>
	$(function() {
		$( "#tabs" ).tabs();
	});
	</script>
</head>
<body>
<%= servlet.getBodyContent(SemAssistServlet.refererURL, request) %>
<div id="semAssist-main" class="semAssist-main">
  <!-- <div id="semAssist-title" style="background-color: #EEE; height: 17px;">
  	<table width=100% border=0 style="background-color: transparent;">
  		<tr>
  			<td width=90% align=center><a href="http://www.semanticsoftware.info/semantic-assistants-project" target="_blank">Semantic Assistants</a></td>
  			<td align=right>
  				<input id="hide" type="image" style="float: right;" src="images/contract.png" alt="Contract" width="17" height="17"/>
  				<input id="settings" type="image" style="float: right;" src="images/contract.png" alt="Contract" width="17" height="17"/>
  				<input id="help" type="image" style="float: right;" src="images/contract.png" alt="Contract" width="17" height="17"/>
  			</td>
  		</tr>
  	</table>
  </div>title -->
  <div id="tabs" style="min-height:250px; height:auto !important; height:100px; font-size:12px;">
	<ul>
        <li><a href="#tabs-1"><span>Available Assistants</span></a></li>
        <li><a href="#tabs-2"><span>Results Target</span></a></li>
        <li><a href="#tabs-3"><span>Global Settings</span></a></li>
        <li><a href="#tabs-4"><span>Console</span></a></li>
    </ul>
    <div id="tabs-1">
    	<p>
    		<span>Step 1. Select the service your wish to execute on your collection. Once you add this page to your collection, you can continue browsing as your collection is saved.</span>
    	</p>
    <table style="background-color: transparent;">
    	<tr>
			<td style="vertical-align:top; width:50%;">
				<table>
					<tr>
						<td>
							<label for="semAssistServices" id="label" class="SemAssist">Available Assistants</label>
							<%= HTMLGenerator.servicesCombobox(SemAssistServlet.infos) %>
						</td>
					</tr>
					<tr>
						<td>
							<label for="saRTParams" id="label" class="SemAssist">Runtime Parameters&nbsp;</label>
						</td>
					</tr>
					<tr>
						<td>
							<span id="saRTParams"></span>
						</td>
					</tr>
				</table>
			</td>
			<td style="vertical-align:top;">
				<table>
					<tr>
						<td style="vertical-align:top;">
							<label for="semAssistCollection" id="label" class="SemAssist">Collection&nbsp;</label>
						</td>
						<td>
							<select id="semAssistCollection" name="collection" size=5 style="width:  400px;"></select>
						</td>
						<td style="vertical-align:bottom;">
							<input id="btnAdd" type="submit" value="Add" />
							<br>
							<input id="btnClear" type="submit" value="Clear" />
						</td>
					</tr>
				</table>
			</td>
    	</tr>
    </table>
    </div>
    <div id="tabs-2">
    <p>Step 2. Please select the target for the service results from the following options:
    </p>
    <label for="semAssistServices-target" id="label" class="SemAssist">Target&nbsp;</label>
    	<table>
    		<tr>
    			<td style="vertical-align:top; width:50%;">
    			<input type="radio" id="targetSelf" name="targetPage" checked="checked">&nbsp;Same page
    			<span style="font-style:italic; font-size:10px;">(results will be written in the same page as the resource)</span>
    			<br>
	  			<input type="radio" id="targetOther" name="targetPage">&nbsp;Another page
	  			<span style="font-style:italic; font-size:10px;">(results will be written in the specified page)</span>
	  			<br>
	  			<input type="radio" id="targetOtherWiki" name="targetPage">&nbsp;Another wiki
	  			<span style="font-style:italic; font-size:10px;">(results will be written in a separate wiki)</span>
    			</td>
    			
    			<td style="vertical-align:top;">
			    				<div id="targetSelfOptions">
				  		<input type="radio" id="self_body" name="target_self" checked="checked">&nbsp;Body
				  		<br>
				  		<input type="radio" id="self_talk" name="target_self">&nbsp;Talk
				  	</div>
				  	
				  	<div id="targetOtherOptions" style="display: none;">
				  		<table>
				  			<tr>
				  				<td>
				  					<label for="wikiNamespaces" id="label" class="SemAssist">Namespace&nbsp;</label>
				  				</td>
				  				
				  				<td>
				  					<%= HTMLGenerator.namespacesCombobox() %>
				  				</td>
				  			</tr>
				  			<tr>
				  				<td>
				  					<label for="targetName" id="label" class="SemAssist">Title&nbsp;</label>				  			
				  				</td>
				  				
				  				<td>
				  					<input type="text" id="targetName" name="target_other"><br>
				  				</td>				  			
				  			</tr>
				  		</table>
				  	</div>
				  	
				 	<div id="targetOtherWikiOptions" style="display: none;">
				 	<table>
				 	<tr>
				 		<td>
				 			 <label for="supportedWikiEngines" id="label" class="SemAssist">Engine&nbsp;</label>
				 		</td>
				 		<td>
				 			<%= HTMLGenerator.supportedWikisCombobox() %>
				 		</td>
				 	</tr>
				 	
				 	<tr>
				 		<td>
				 			<label for="otherWikiAddress" id="label" class="SemAssist">Address&nbsp;</label>
				 		</td>
				 		<td>
				 			<input type="text" id="otherWikiAddress" name="otherWikiAddress">
				 		</td>
				 	</tr>
				 	
				 	<tr>
				 		<td>
				 			<label for="otherWikiTitle" id="label" class="SemAssist">Title&nbsp;</label>
				 		</td>
				 		<td>
				 			<input type="text" id="otherWikiTitle" name="otherWikiTitle">
				 		</td>
				 	</tr>
				 	
				 	<tr>
				 		<td>
				 			 <label for="otherWikiUser" id="label" class="SemAssist">Username&nbsp;</label>
				 		</td>
				 		<td>
				 			<input type="text" id="otherWikiUser" name="otherWikiUser">
				 		</td>
				 	</tr>
				 	<tr>
				 		<td>
				 			<label for="otherWikiPass" id="label" class="SemAssist">Password&nbsp;</label>
				 		</td>
				 		<td>
				 			<input type="password" id="otherWikiPass" name="otherWikiPass">
				 		</td>
				 	</tr>
				 	</table>
			  	</div>
    			</td>
    			
    			<td style="vertical-align:top;">
    				<input id="btnRun" type="submit" value="Run Service" />
	  				<span id="semAssist-wait"></span>
    			</td>
    		</tr>
    	</table>
    </div>
    <div id="tabs-3">
    <p>
    	   Optionally, you can select the server you would like to connect to. Select a server from the list, press "Connect" and refresh the page using your browser.
    </p> 
		<table>
			<tr>
				<td style="vertical-align:top; width:200px;">
					<input type="radio" id="semAssistPreServer" name="semAssistServers" checked="checked">&nbsp;Predefined Servers
					<br>
					<input type="radio" id="semAssistCusServer" name="semAssistServers">&nbsp;Custom Server
				</td>
				<td style="vertical-align:top;" rowspan="2">
					<div id="semAssistPreServerOptions">
						<table>
							<tr>
								<td>
									<%= HTMLGenerator.preDefinedServers() %>
									&nbsp;
									<input type="submit" id="btnSetServer" value="Connect"/>
								</td>
							</tr>
						</table>
					</div>
					<div id="semAssistCusServerOptions" style="display: none;">
						<table>
							<tr>
								<td>
									<label for="semAssistCusServerHost" id="label" class="SemAssist">Hostname&nbsp;</label>
								</td>
								<td>
									<input type="text" id="semAssistCusServerHost" name="semAssistCusServerName">
								</td>
							</tr>
							
							<tr>
								<td style="vertical-align:top;">
									<label for="semAssistCusServerPort" id="label" class="SemAssist">Port Number&nbsp;</label>
								</td>
								<td>
									<input type="text" id="semAssistCusServerPort" name="semAssistCusServerPort">
									<p>
									<input id="btnAddServer" type="submit" value="Add Server" />
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
		</table>
    </div>
    <div id="tabs-4">
    	<div id="semAssist-console">
    		[<%=ConsoleLogger.getTime()%>] Console is ready!
    		<br>
    	</div>
    </div>
</div>
</div><!--main-->
</body>
</html>