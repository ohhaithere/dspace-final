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

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
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
extraHeadData += "<link rel=\"stylesheet\" href=\"" + request.getContextPath() + "/importlog/importlog.css\" type=\"text/css\" />";
request.setAttribute("dspace.layout.head", extraHeadData);
Date date = (Date) request.getAttribute("date");
DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
%>

<dspace:layout navbar="admin" style="submission" titlekey="jsp.register.edit-profile.title" nocache="true">
	<script type="text/javascript" src="<%= request.getContextPath() %>/importlog/importlog.js"></script>
	<script type="text/javascript">
		var contextPath = '<%= request.getContextPath() %>';
	</script>
	<h1>Журнал импорта ресурсов</h1>
	<form id="importlog_form" method="get">
		<table>
			<tr>
				<td>Перейти к дате:</td>
				<td><input id="import_date" type="text" name="date" value="<%= df.format(date) %>"></td>
			</tr>
			<tr>
				<td colspan="2">
					<button>Перейти</button>
				</td>
			</tr>
		</table>
	</form>
	<br>
	<div id="empty-container">Нет отчетов за выбранную дату</div>
	<br>
	<div id="result-container">
		<p>
			<b>Всего загружено ресурсов: <span id="total"></span></b>
		</p>
		<p>
			<b>Дата начала загрузки: <span id="start_date"></span></b>
		</p>
		
		<table id="results">
			<thead>
				<tr>
					<th>Год<br>издания</th>
					<th>Название</th>
					<th>Автор(ы)</th>
					<th>Ссылка</th>
					<th>Повторная<br>загрузка</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
		
		<table class="result-controls">
			<tr>
				<td><a href="#" id="prevLink">Предыдущий отчет</a></td>
				<td><a href="#" id="loadMore" class="btn btn-default">Загрузить еще</a></td>
				<td><a href="#" id="nextLink">Следующий отчет</a></td>
			</tr>
		</table>
		<br>
	</div>
	<a href="#" id="errorLogLink">Журнал ошибок импорта</a>
</dspace:layout>