<%--
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2013, 2014 Semantic Software Lab, http://www.semanticsoftware.info

The Semantic Assistants architecture is free software: you can
redistribute and/or modify it under the terms of the GNU Affero General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see http://www.gnu.org/licenses/.
--%>
<%@include file="DefineObjects.jsp" %>

<%
PortletPreferences prefs = renderRequest.getPreferences();
String greeting = prefs.getValue("greeting", "empty");
String text = renderRequest.getParameter("sa_text");

String contextPath = request.getContextPath();

List<String> servers = new ArrayList<String>();
servers.add("http://minion.cs.concordia.ca:8879");
servers.add("http://minion.cs.concordia.ca:8879");
%>

<b>Server:</b><br />

<portlet:actionURL name="runAssistant" var="runAssistantURL"/>
<portlet:resourceURL var="ajaxResourceUrl"/>

<portlet:resourceURL var="resourceUrl"/>
 
 <liferay-portlet:actionURL var="linkURL" portletName="Content_WAR_Contentportlet"  portletMode="view" windowState="<%= WindowState.MAXIMIZED.toString()%>" name="sendSAResult"/>

 <script type="text/javascript">

 	function  serverChanged(serverMenu) {
		var selectedServer = serverMenu.value;
    	var params = {"server":selectedServer};
    	var url = '<%= ajaxResourceUrl %>';

    	$.ajax({
    	        url: url,
    	        data: params,
    	        type: 'post',
    	        dataType: 'json',
    	        cache:false,
    	        async: true,
    	        success: function(data){

    	        	var generatedOutput = "<div class='sa-list' id='sa-list-portletIdNormalized'>";
    	        	$.each(data, function(i, data){
    	        		generatedOutput += "<p class='sa-heading'>" + data.serviceName + "</p>";
    	        		generatedOutput += "<div class='sa-content'>" + data.serviceDescription;
    	        		generatedOutput += "<fieldset class='sa'><legend>Runtime Parameters</legend><table>";
    	        		
    	        		var params = data.params;

    	        		if(typeof params != 'undefined'){
        	        		   for (var i = 0; i < params.length; i++) { 
        	        			   var name = params[i].paramName;
        	        			   generatedOutput += "<tr><td>" + name +"</td>";
        	        			   generatedOutput += "<td><input type='text' size='10' value='" + params[i].defaultValueString + "'/></td></tr>";
								}
    	        		}else{
 	        			   generatedOutput += "<tr><td>No runtime parameters.</td></tr>";
    	        		}
    	        		generatedOutput += '</table></fieldset></div>';
    	        	});
	        		$('#serviceName').html(generatedOutput + "<div id='divRunAssistant' align='right'><input type='button' id='btnRunAssistant' value='Run Assistant' onclick='javascript:runAssistant()'/></div></div>");

    	        },
    	        error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert('error');
    	        }
    	     });

    	var server = document.getElementById("serverSelection");
    	server.options[server.options.selectedIndex].selected = "selected";
     }
     
     function runAssistant(){
    	 if($(".sa-content").is(":visible")){
    			$("#errorRun").remove();
    			var serviceName = $(".sa-content:visible").prev().text();
    			var params = $(".sa-content:visible td");
    			var paramsQuery = "";
    			if(params.size() > 1){
    				for(var i=0; i < params.size(); i++){
    					if(i % 2 == 0){
    						//it's the param name
    						paramsQuery += params.eq(i).text() + "=";
    					}else{
    						//it's the corresponding value
    						paramsQuery += params.eq(i).find('input').val() + "&";
    					}
    				}
    			}
    			paramsQuery = paramsQuery.substring(0,paramsQuery.length-1);
    			doRunAssistant(serviceName, paramsQuery)
    		}else{
    			$("#divRunAssistant").append("");
    			$("<span id='errorRun' style='color: red;'>Please choose an assistant first by clicking on the service name.</span>").insertBefore('#btnRunAssistant');
    		}
     }
     
     function doRunAssistant(serviceName, paramsQuery){
    	var selectedServer = $("#serverCombo option:selected").text();
    	var params = {"serviceName":serviceName, "paramsQuery":paramsQuery, "endpoint":selectedServer};
     	var url = '<%= runAssistantURL %>';
  
     	$.ajax({
     	        url: url,
     	        data: params,
     	        type: 'post',
     	        dataType: 'text',
     	        cache:false,
     	        async: true,
     	        success: function(data){
     	        	console.log('ran successfully');
     	        	Liferay.Portlet.refresh('#p_p_id_SASampleContent_WAR_SASampleContentportlet_'); 
     	        },
     	        error : function(XMLHttpRequest, textStatus, errorThrown) {
 					alert('error running assistant');
     	        }
     	     });
     }
   
 </script>
 
    <select id="serverCombo" name="serverSelection" onchange='serverChanged(this);'>
    <% 
		for(String server : servers) { %>
         <option value="<%=server  %>"> <liferay-ui:message key="<%=server%>" />    </option> 
    <% } %>
    </select>

<br/></br>
<b>Available Assistants:</b> <br />

<div class="sa-list" id="sa-list-portletIdNormalized">

<div id="serviceName">Select a server to view the list of services.</div>

</div>