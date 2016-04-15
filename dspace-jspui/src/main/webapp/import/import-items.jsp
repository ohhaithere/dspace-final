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
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="org.w3c.dom.Node" %>

<%@ page import="org.w3c.dom.NodeList" %>

<%@ page import="javax.xml.xpath.*" %>
<%@ page import="org.w3c.dom.Element" %>

<%

  String collection_id = (String) request.getAttribute("collection_id");
  String community_id = (String) request.getAttribute("community_id");
  Document doc = (Document) request.getAttribute("document");

  NodeList nodes = doc.getElementsByTagName("m:BiblRecords");
  //evaluate expression result on XML document

%>

<dspace:layout style="submission" titlekey="jsp.register.edit-profile.title" nocache="true">
 <% for(int i = 1; i < nodes.getLength(); i++){
      Element node = (Element) nodes.item(i);
      String author = node.getElementsByTagName("m:Author").item(0).getTextContent();
      String title = node.getElementsByTagName("m:Title").item(0).getTextContent();
      String item_id = node.getElementsByTagName("m:Id").item(0).getTextContent();%>
 <br>
  <form action="/jspui/import-item" method="post" name="edit_metadata" id="edit_metadata" onkeydown="return disableEnterKey(event);">

    <%="<b>Автор:</b> " + author + "<br> <b>Наименование:</b>  " + title + "<br>"%>
    <input type="hidden" name="import_item" value="<%=item_id %>" />
    <input type="hidden" name="action" value="write_name"/>
    <input type="hidden" name="collection_id" value="<%=collection_id %>" />
    <input type="hidden" name="community_id" value="<%=community_id %>" />
    <input class="btn btn-primary pull-left col-md-3" id="button_spin" type="submit" name="submit" value="Импортировать">
  </form>
  <br>




 <% } %>


</dspace:layout>
