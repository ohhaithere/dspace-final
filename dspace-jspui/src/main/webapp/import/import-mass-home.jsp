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
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.dspace.content.Collection" %>

<%

    String collection_id = (String) request.getAttribute("collection_id");
    String community_id = (String) request.getAttribute("community_id");

    TableRowIterator name = (TableRowIterator) request.getAttribute("systems");
    Collection[] paths = (Collection[]) request.getAttribute("ids");


%>

<dspace:layout navbar="admin" style="submission" titlekey="jsp.register.edit-profile.title" nocache="true">
    <b>Массовый импорт</b><br>
    <form action="/import-mass" method="post" name="edit_metadata" id="edit_metadata" onkeydown="return disableEnterKey(event);">
  <span class="col-md-5">
    Путь к каталогу:
    <select name="folder_path">
                            <% if(name != null){
                            while(name.hasNext()) {
                                TableRow row = name.next();%>
                            <option value="<%=row.getStringColumn("folder_path")%>"><%=row.getStringColumn("system_name")%></option>
                            <% } } %>
                        </select>
  </span>
  Коллекция для загрузки:
  <select name="collection_id">
                          <% if(paths != null){
                          for(int i = 0; i < paths.length; i++){
                            Integer id = paths[i].getID();  %>
                          <option value="<%=id%>"><%=paths[i].getName()%></option>
                          <% } } %>
                      </select>
        <br>
        <br>
        <input class="btn btn-primary pull-left col-md-3" id="button_spin" type="submit" name="submit" value="Загрузить">
<div id="wow-spinner"></div>
    </form>


</dspace:layout>