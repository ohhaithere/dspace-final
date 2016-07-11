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
extraHeadData += "<link rel=\"stylesheet\" href=\"" + request.getContextPath() + "/folders/folders.css\" type=\"text/css\" />";
request.setAttribute("dspace.layout.head", extraHeadData);
%>

<dspace:layout navbar="admin" style="submission" titlekey="jsp.register.edit-profile.title" nocache="true">
	<script type="text/javascript" src="<%= request.getContextPath() %>/folders/folders.js"></script>
	<script type="text/javascript">
		var contextPath = '<%= request.getContextPath() %>';
	</script>
	<!-- Форма добавления/редактирования -->
	<form id="folder_form" method="post" action="">
		<input type="hidden" name="id">
		<table>
			<tr>
				<td>Время импорта</td>
				<td>Число</td>
				<td>День</td>
				<td>Месяц</td>
				<td>Год</td>
				<td>Путь импорта</td>
			</tr>
			<tr>
				<td>
					<input type="number" name="hour" placeholder="чч" max="23" min="00"> :
					<input type="number" name="minute" placeholder="мм" max="59" min="00">
				</td>
				<td>
					<select name="date">
						<option value="" selected="selected">Все</option>
						<% for (int i = 1; i <= 31; i++) { %>
						<option value="<%=i%>"><%=i%></option>
						<% } %>
					</select>
				</td>
				<td>
					<select name="day">
						<option value="" selected="selected">Все</option>
						<option value="1">Понедельник</option>
						<option value="2">Вторник</option>
						<option value="3">Среда</option>
						<option value="4">Четверг</option>
						<option value="5">Пятница</option>
						<option value="6">Суббота</option>
						<option value="0">Воскресенье</option>
					</select>
				</td>
				<td>
					<select name="month">
						<option value="" selected="selected">Все</option>
						<option value="1">Январь</option>
						<option value="2">Февраль</option>
						<option value="3">Март</option>
						<option value="4">Апрель</option>
						<option value="5">Май</option>
						<option value="6">Июнь</option>
						<option value="7">Июль</option>
						<option value="8">Август</option>
						<option value="9">Сентябрь</option>
						<option value="10">Октябрь</option>
						<option value="11">Ноябрь</option>
						<option value="12">Декабрь</option>
					</select>
				</td>
				<td>
					<select name="year">
						<option value="" selected="selected">Все</option>
						<%
							Calendar cal = Calendar.getInstance();
							for (int i = cal.get(Calendar.YEAR); i <= (cal.get(Calendar.YEAR) + 10); i++) {
						%>
						<option value="<%=i%>"><%=i%></option>
						<% } %>
					</select>
				</td>
				<td>
					<input type="text" name="path" placeholder="укажите подкаталог" required="required">
					<div id="error" class="error"></div>
				</td>
			</tr>
		</table>
		<br>
		<button id="savebtn" type="submit">Создать</button>
		<button id="backbtn" type="button">Назад</button>
	</form>
	<br><br>
	
	<!-- Список каталогов -->
	Список папок для импорта<br>
	<table class="folders-table">
		<tr>
			<td id="folder_list"></td>
			<td class="folder-actions">
				<a id="deletebtn" href="#"><i class="glyphicon glyphicon-remove-sign"></i> Удалить импорт</a>
				<a id="runbtn" href="#"><i class="glyphicon glyphicon-play-circle"></i> Запустить импорт</a>
			</td>
		</tr>
	</table>
</dspace:layout>