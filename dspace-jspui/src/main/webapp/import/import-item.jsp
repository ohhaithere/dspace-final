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
  String uuid = (String) request.getAttribute("uuid_search");
  Document doc = (Document) request.getAttribute("document");

  XPathFactory xpathFactory = XPathFactory.newInstance();

  // Create XPath object
  XPath xpath = xpathFactory.newXPath();


  //Node nodeValue = nodeTitle.getChildNodes().item(3);

  XPathExpression expr =
          null;


  try {
    expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Title']/*[local-name()='Value']");
  } catch (XPathExpressionException e) {
    e.printStackTrace();
  }

  String titles = "";

  NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

  for(int i = 0; i <nodes.getLength(); i++){
    titles+=nodes.item(i).getTextContent();
    if(i == nodes.getLength() - 1){

    }else
      titles+=",";
  }

  try {
    expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Subject']");
  } catch (XPathExpressionException e) {
    e.printStackTrace();
  }

  String tagsString = "";

  NodeList tags = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

  for(int i = 0; i <tags.getLength(); i++){
    tagsString+=tags.item(i).getTextContent();
    if(i == tags.getLength() - 1){

    }else
      tagsString+=",";
  }

  try {
    expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Date']");
  } catch (XPathExpressionException e) {
    e.printStackTrace();
  }

  String dateString = "";

  NodeList dates = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

  String date = dates.item(0).getTextContent();

  try {
    expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Language']");
  } catch (XPathExpressionException e) {
    e.printStackTrace();
  }

  String languages = "";

  NodeList language = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

  for(int i = 0; i <language.getLength(); i++){
    languages+=language.item(i).getTextContent();
    if(i == language.getLength() - 1){

    }else
      languages+=",";
  }

  try {
    expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Coverage']/*[local-name()='Value']");
  } catch (XPathExpressionException e) {
    e.printStackTrace();
  }

  String coverages = "";

  NodeList coverage = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

  for(int i = 0; i <coverage.getLength(); i++){
    coverages+=coverage.item(i).getTextContent();
    if(i == coverage.getLength() - 1){

    }else
      coverages+=",";
  }

  try {
    expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Creator']");
  } catch (XPathExpressionException e) {
    e.printStackTrace();
  }

  String author = "";

  NodeList authors = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

  if(authors.getLength() > 0){
    author = authors.item(0).getTextContent();
  }
  //evaluate expression result on XML document

%>

<dspace:layout style="submission" titlekey="jsp.register.edit-profile.title" nocache="true">
  <b>Автор: </b><%= author%><br>
  <b>Наименование: </b> <%=titles%> <br>
  <b>Ключевые слова: </b> <%=coverages %> <br>
  <b>Дата: </b><%=date%> <br>
  <b>Языки: </b><%=languages%><br>

  <form action="/jspui/submit" method="post" name="edit_metadata" id="edit_metadata" onkeydown="return disableEnterKey(event);">
    <input type="hidden" name="collection" value="<%=collection_id %>" />
    <input type="hidden" name="community_id" value="<%=community_id %>" />
    <input type="hidden" name="titles" value="<%=titles %>" />
    <input type="hidden" name="tags" value="<%=tagsString %>" />
    <input type="hidden" name="date" value="<%=date %>" />
    <input type="hidden" name="languages" value="<%=languages %>" />
    <input type="hidden" name="coverages" value="<%=coverages %>" />
    <input type="hidden" name="author" value="<%=author %>" />
    <input type="hidden" name="identifier" value="<%=uuid %>" />
    <input class="btn btn-primary pull-left col-md-3" type="submit" name="submit" value="Найти">
  </form>


</dspace:layout>
