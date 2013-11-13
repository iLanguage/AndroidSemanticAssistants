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
String sa_result = renderRequest.getParameter("sa_result");
String sa_result_type = renderRequest.getParameter("sa_result_type");
String serviceName = renderRequest.getParameter("serviceName");
String portletId = PortalUtil.getPortletId(request);
String portletInfo = PortalUtil.getPortletNamespace(portletId);
String text ="Stephen Hawking is the former Lucasian Professor of Mathematics at the University of Cambridge and author of A Brief History of Time which was an international bestseller. Now Director of Research at the Centre for Theoretical Cosmology at Cambridge, his other books for the general reader include A Briefer History of Time, the essay collection Black Holes and Baby Universe and The Universe in a Nutshell.In 1963, Hawking contracted motor neurone disease and was given two years to live. Yet he went on to Cambridge to become a brilliant researcher and Professorial Fellow at Gonville and Caius College. Since 1979 he has held the post of Lucasian Professor at Cambridge, the chair held by Isaac Newton in 1663. Professor Hawking has over a dozen honorary degrees and was awarded the CBE in 1982. He is a fellow of the Royal Society and a Member of the US National Academy of Science. Stephen Hawking is regarded as one of the most brilliant theoretical physicists since Einstein.";
String textHighlighting="";
%>

<h1>Stephen Hawking</h1>

<p>

<%
// in case we have result from the Semantic Assistants Server
if(sa_result != null && sa_result.length() > 0){
	if(sa_result_type.equalsIgnoreCase("annotation")){
		try{
			JSONArray array = JSONFactoryUtil.createJSONArray(sa_result);
			//then parse the result and create a table with all anntotations
			%>
				<table id="contentTable" class="saTable">
				<tr class="saTableTitle">
					<td class="saTableTitle">Content</td><td class="saTableTitle">Type</td><td class="saTableTitle">Start</td><td class="saTableTitle">End</td><td class="saTableTitle">Features</td>
				</tr>
			<%
			if(array != null && array.length() > 0){
			//for the highlighting we have to collect all annotations in map <mStart, JSONObject>
			Set<Integer> mStartSet = new HashSet<Integer>();
			Map<Integer,JSONObject> map = new HashMap<Integer,JSONObject>();
			
			for(int i=0; i < array.length(); i++){
				for(int j=0; j < array.getJSONArray(i).length(); j++){
			
						//get the AnnotationObject
						JSONObject obj = array.getJSONArray(i).getJSONObject(j);
						String content = obj.getString("mContent");
						String type = obj.getString("mType");
						String start = obj.getString("mStart");
						String end = obj.getString("mEnd");
						
						/* Features of an annotation is parsed as a JSONObject that has arbitrary number of <key,value> pairs */
						JSONObject featObjects = obj.getJSONObject("mFeatures");
						String featList="";
						
						Iterator<String> keys = featObjects.keys();
					    while(keys.hasNext()){
					        String key = keys.next();
					    	String value = featObjects.getString(key);
					    	if(value.trim().equals("")){
					    		System.out.println("----- [WARNING]: key \"" + key + "\" does not have any value assigned.");
					    	}else{
					    		featList += key + ":" + value + " ";
					    	}
					  	}
						
						//add the start value to the mStartSet - for sorting
						mStartSet.add(new Integer(start).intValue());
						
						//put the AnnotationObject to the map
						map.put(new Integer(start).intValue(), obj);
						
						%>
							<tr><td class="saTableCell"><%= content%></td><td class="saTableCell"><%= type%></td><td class="saTableCell"><%= start%></td><td class="saTableCell"><%= end%></td><td class="saTableCell"><%=featList %></td></tr>
						<%
				}
			}
			
			%>
				</table>
			<%
			
			//create an array list with the size of the mStartSet
			int[] list = new int[mStartSet.size()];
			int j = 0;
			
			//now we have a list like this:
			//e.g: [0] 0, [1] 416, [2] 692, [3] 714, [4] 887, [5] 240, [6] 508, [7] 663
			for(Iterator<Integer> it = mStartSet.iterator(); it.hasNext();){
				list[j++] = it.next();
			}
			
			//sort the list	
			Arrays.sort(list);
			
			//result now:
			//e.g: [0] 0, [5] 240, [1] 416, [6] 508, [7] 663, [2] 692, [3] 714, [4] 887	
			
			String markup1 = "";
			String markup2 = text;
			
			//now we can go through the text and replace all annotations with a colorful span
			//we iterate over the list with the start information
			for (int i = 0; i < list.length; i++) {
				
				//get the JSONObject in the map
				JSONObject obj = map.get(list[i]);
				String content = map.get(list[i]).getString("mContent");
				String type = map.get(list[i]).getString("type");
				
				//if the text contains the annotation
				if(markup2.contains(content)){
					//get the first occurence of the content
					int start = markup2.indexOf(content);
					int end = start + content.length();
					String style = "";
					String color = "yellow";
					style = " style=\"background-color: " + color + "\"";
					
					//replace the string with the highlighted span
					String replace = "<span " + style + ">" + content + "</span>";
					
					//create a new textString with the highlighted text
					markup1 = markup1 + markup2.substring(0,start) + replace;
					markup2 = markup2.substring(end);
				}
			}
			
			markup1 += markup2;
			textHighlighting=markup1;
			}
		}
		catch(JSONException e){
			e.printStackTrace(); 
		}
	}else if(sa_result_type.equalsIgnoreCase("boundlessAnnotation")){
		try{
			JSONArray array = JSONFactoryUtil.createJSONArray(sa_result);
			String type = "";
			String content = "";
			String featList="";
			if(array != null && array.length() > 0){
				for(int i=0; i < array.length(); i++){
					for(int j=0; j < array.getJSONArray(i).length(); j++){
						type = array.getJSONArray(i).getJSONObject(j).getString("mType");	
						content = array.getJSONArray(i).getJSONObject(j).getString("mContent");
						
						/* Features of an annotation is parsed as a JSONObject that has arbitrary number of <key,value> pairs */
						String feats = array.getJSONArray(i).getJSONObject(j).getString("mFeatures");
						JSONObject featObjects = JSONFactoryUtil.createJSONObject(feats);
						
						
						Iterator<String> keys = featObjects.keys();
					    while(keys.hasNext()){
					        String key = keys.next();
					    	String value = featObjects.getString(key);
					    	if(value.trim().equals("")){
					    		System.out.println("----- [WARNING]: key \"" + key + "\" does not have any value assigned.");
					    	}else{
					    		featList += key + ":" + value + " ";
					    	}
					  	}
					}
				}
				%>
				<table id="boundlessAnnotationTable" class="saTable">
				<tr class="saTableTitle">
					<td class="saTableTitle"><%=type %></td>
				</tr>
				<tr>
					<td class="saTableCell"><b>Content: </b>
					<p><%= content%></p>
					<hr>
					<b>Features: </b>
					<p><%= featList %></p>
					</td>
				</tr>
				</table>
			<%
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
	}else if(sa_result_type.equalsIgnoreCase("outputFile")){
		String realpath = portletConfig.getPortletContext().getRealPath("/");
		File tempFile = new File(realpath + "sa-temp.html");
		if(tempFile.exists()){tempFile.delete();}
		try {
	        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
	                writer.write(sa_result);
		            writer.flush();
	                writer.close();
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
		
		System.out.println(realpath + "sa-temp.html");
				%>
	<div id="dialog" title="Semantic Assistants Results"><p>ERROR: Cannot read the temporary result file.</p></div>
			 <script>
				$(function() {
					$("#dialog").load("/SASampleContent-portlet/sa-temp.html").dialog({height: 500, width:500});
				});
			</script>	
				<%
	}
}

%>

<p>

<%
	//if a highlighted text is available - use it
	if(textHighlighting!=""){
%>
		<%=textHighlighting %>
<%
	}else{
%>
	<%=text%>
<%	
	} 
%>

</p>

<portlet:actionURL name="sendText" var="sendTextURL" />
<portlet:actionURL name="refresh" var="refreshURL" />

<div class="sendTextButton">
<form action="<%=sendTextURL %>" method="post">
	<input type="submit" value="Send for Analysis"/>
	<input type="hidden" name="sa_text" value="<%=text %>"/>
</form>
</div>

<div class="refreshButton">
<form class="refresh" action="<%=refreshURL %>" method="post">
	<input type="submit" value="Clear Results"/>
</form>
</div>

