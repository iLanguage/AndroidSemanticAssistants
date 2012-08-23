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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.io.*" %>
<% response.setStatus(500); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Semantic Assistants - Error page</title>
  <style type="text/css">
  body {
    font-family: arial;
    }
  .header{
  	font-weight: bold;
  }
  </style>
</head>
<body>
<center>
<h1>Semantic Assistants Servlet Error Report</h1>
<table width=600 border=1 cellpadding="5">
<tr>
	<td class="header">
	Time
	</td>
	<td>
	<%
		Date date = new Date();
		out.println(date.toString());
	%>
	</td>
</tr>

<tr>
	<td class="header">
	Type
	</td>
	<td>
	<%= exception.getClass().getName() %>
	</td>
</tr>

<tr>
	<td class="header">
	Message
	</td>
	<td>
	<%= exception.getMessage() %>
	</td>
</tr>

<tr>
	<td class="header">
	Stacktrace
	</td>
	<td>
	<pre>
	<%
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		String stacktrace = sw.toString();
	%>
	</pre>
	</td>
</tr>
</table>
</center>
</body>
</html>