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

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
           prefix="fmt" %>


<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>

<%@ page import="org.dspace.eperson.EPerson, org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.core.Utils" %>
<%@ page import="org.dspace.storage.rdbms.TableRowIterator" %>
<%@ page import="org.dspace.storage.rdbms.TableRow" %>

<dspace:layout navbar="admin"  style="submission" titlekey="jsp.register.edit-profile.title" nocache="true">
    <table>

    <tr>
        <th id="t1" class="oddRowEvenCol">Наименование</th><th id="t3" class="oddRowEvenCol">Путь</th><th id="t4" class="oddRowEvenCol"></th><th id="t5" class="oddRowEvenCol"></th></tr>
    <%
        TableRowIterator name = (TableRowIterator) request.getAttribute("systems");
        while(name.hasNext()){
        TableRow row = name.next();
        Integer i = row.getIntColumn("id");%>
    <tr>
    <th class="oddRowEvenCol"><%=row.getStringColumn("system_name") %></th><th class="oddRowEvenCol"><%=row.getStringColumn("folder_path") %></th><th class="oddRowEvenCol"><a href="systems?action=edit&id=<%=i%>">Редактировать</a></th><th class="oddRowEvenCol"><a class="deleteText" href="systems?action=delete&id=<%=i%>">Удалить</a></th></tr>

    <% } %>
    </table>
<a href="/jspui/systems?action=add"> <input class="btn btn-primary pull-left col-md-3" type="submit" name="submit" value="Добавить"></a>


</dspace:layout>