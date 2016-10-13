<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Profile editing page
  -
  - Attributes to pass in:
  -
  -   eperson          - the EPerson who's editing their profile
  -   missing.fields   - if a Boolean true, the user hasn't entered enough
  -                      information on the form during a previous attempt
  -   password.problem - if a Boolean true, there's a problem with password
  --%>

<%@page import="org.dspace.content.Metadatum"%>
<%@page import="org.dspace.content.MetadataSchema"%>
<%@page import="org.dspace.content.Item"%>
<%@page import="org.dspace.content.Collection"%>
<%@page import="org.dspace.content.Community"%>
<%@page import="org.dspace.content.DSpaceObject"%>
<%@page import="java.util.Calendar"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
           prefix="fmt" %>


<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>

<%@ page import="org.dspace.eperson.EPerson, org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.core.Utils" %>
<%@ page import="org.dspace.storage.rdbms.TableRowIterator" %>
<%@ page import="org.dspace.storage.rdbms.TableRow" %>
<%
String extraHeadData = (String)request.getAttribute("dspace.layout.head");
if (extraHeadData == null) {
	extraHeadData = "";
}
extraHeadData += "<link rel=\"stylesheet\" href=\"" + request.getContextPath() + "/ipacl/ipacl.css\" type=\"text/css\" />";
request.setAttribute("dspace.layout.head", extraHeadData);
String area = null;
DSpaceObject resource = (DSpaceObject) request.getAttribute("resource");
if (resource == null) {
	area = "сайту";
} else if (resource instanceof Community) {
	area = ((Community)resource).getMetadata("name");
} else if (resource instanceof Collection) {
	area = ((Collection)resource).getMetadata("name");
} else if (resource instanceof Item) {
	Metadatum[] itemTitle = ((Item)resource).getMetadata(MetadataSchema.DC_SCHEMA, "title", null, "ru");
	if (itemTitle.length > 0) {
		area = itemTitle[0].value;
	} else {
		area = ((Item)resource).getMetadata("title");
	}
}

if (resource != null) {
	area = "<a href=\"" + request.getContextPath() + "/handle/" + resource.getHandle() + "\">" + area + "</a>";
}
%>

<dspace:layout navbar="admin" style="submission" title="Фильтрация IP адресов" nocache="true">
	<script type="text/javascript" src="<%= request.getContextPath() %>/ipacl/ipacl.js"></script>
	<script type="text/javascript">
		var contextPath = '<%= request.getContextPath() %>';
		var resourceId = <%= (resource != null ? resource.getID() : "null") %>;
	</script>
	<h1>Фильтрация IP адресов на доступ к <%= area %></h1>
	<table class="ipaccess">
		<tr>
			<td colspan="3" class="access-type">
				<label><input type="radio" name="type" value="ip" checked="checked"> Один адрес</label>
				<label><input type="radio" name="type" value="range"> Диапазон адресов</label>
			</td>
			<td></td>
		</tr>
		<form id="ipform" action="">
		<tr id="simpleip">
			<td colspan="3">
				<label>IP-адрес:</label>
				<input type="text" name="ip" placeholder="10.10.10.10" required="required" pattern="^(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$">
			</td>
			<td></td>
		</tr>
		<tr id="iprange">
			<td colspan="3">
				<label>Диапазон IP-адресов:</label>
				<input type="text" name="ip1" placeholder="10.10.10.10"> - <input type="text" name="ip2" placeholder="10.10.10.10">
			</td>
			<td></td>
		</tr>
		<tr>
			<td colspan="3">
				<button type="submit" value="allow">Разрешить</button>
				<button type="submit" value="deny">Запретить</button>
			</td>
			<td></td>
		</tr>
		</form>
		<tr>
			<td>
				<label>Доступ разрешен:</label>
				<select id="whiteList" multiple="multiple"></select>
			</td>
			<td></td>
			<td>
				<label>Доступ запрещен:</label>
				<select id="blackList" multiple="multiple"></select>
			</td>
			<td class="controls">
				<a id="deletebtn" href="#"><i class="glyphicon glyphicon-remove-sign"></i> Удалить правило</a>
			</td>
		</tr>
	</table>
</dspace:layout>