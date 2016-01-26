package org.dspace.app.webui.servlet;

import org.apache.log4j.Logger;
import org.dspace.app.webui.util.SoapHelper;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.MetadataSchema;
import org.dspace.content.Metadatum;
import org.dspace.core.Context;
import org.dspace.handle.HandleManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by root on 1/25/16.
 */
public class ReimportServlet extends DSpaceServlet {

    /** Logger */
    private static Logger log = Logger.getLogger(EditProfileServlet.class);

    protected void doDSGet(Context context, HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException {

    }


    protected void doDSPost(Context context, HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException {

        Item ti = Item.find(context, Integer.parseInt(request.getParameter("item_id")));

        Metadatum[] specs = ti.getDC("identifier", null, Item.ANY);
        String iden = specs[0].value;

        SoapHelper sh = new SoapHelper();

        Document docMeta = null;
        docMeta = sh.getRecordById(iden);

        XPathFactory xpathFactory = XPathFactory.newInstance();

        // Create XPath object
        XPath xpath = xpathFactory.newXPath();


        //Node nodeValue = nodeTitle.getChildNodes().item(3);

        XPathExpression expr =
                null;

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Subject']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList subject = null;
        try {
            subject = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        ti.clearMetadata(MetadataSchema.DC_SCHEMA, "subject", Item.ANY, Item.ANY);
        writeMetaDataToItemLowerCaseSubject(ti, "subject", subject);


        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Creator']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        NodeList creators = null;
        try {
            creators = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        try {
            Node creator = creators.item(0);

            ti.clearMetadata(MetadataSchema.DC_SCHEMA, "creator", Item.ANY, Item.ANY);
            String creatorsString = creator.getTextContent();
            ti.addMetadata(MetadataSchema.DC_SCHEMA, "creator", null, "ru", creatorsString);
            ti.clearMetadata(MetadataSchema.DC_SCHEMA, "contributor", Item.ANY, Item.ANY);
            String[] creatorsAr = creatorsString.split(",");
            for(int i = 0; i<creatorsAr.length; i++){
                ti.addMetadata(MetadataSchema.DC_SCHEMA, "contributor", "author", "ru", creatorsAr[i]);
            }
        } catch (Exception e){

        }

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Date']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList dates = null;
        try {
            dates = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        try {
            ti.clearMetadata(MetadataSchema.DC_SCHEMA, "date", "issued", Item.ANY);
            ti.addMetadata(MetadataSchema.DC_SCHEMA, "date", "issued", "ru", dates.item(0).getTextContent());
        } catch(Exception e){

        }

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Identifier']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList idents = null;
        try {
            idents = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        ti.clearMetadata(MetadataSchema.DC_SCHEMA, "identifier", Item.ANY, Item.ANY);
        writeMetaDataToItemLowerCaseIdentifier(ti, "identifier", idents, request);

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Title']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList nodes = null;
        try {
            nodes = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        try {
            ti.clearMetadata(MetadataSchema.DC_SCHEMA, "title", Item.ANY, Item.ANY);
            writeMetaDataToItemLowerCaseTitle(ti, "title", nodes);
        } catch(Exception e){

        }

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Description']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList descrs = null;
        try {
            descrs = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }


        ti.clearMetadata(MetadataSchema.DC_SCHEMA, "description", Item.ANY, Item.ANY);
        writeMetaDataToItemLowerCaseDescr(ti, "description", descrs);

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Language']");
        } catch (XPathExpressionException e) {
            log.error("lang error:", e);
        }



        NodeList langs = null;
        try {
            langs = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            log.error("lang error:", e);
        }
        ti.clearMetadata(MetadataSchema.DC_SCHEMA, "language", Item.ANY, Item.ANY);
        writeMetaDataToItemLowerCaseLang(ti, "language", langs, request);



        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Coverage']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList cover = null;
        try {
            cover = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        ti.clearMetadata(MetadataSchema.DC_SCHEMA, "coverage", Item.ANY, Item.ANY);
        writeMetaDataToItemLowerCase(ti, "coverage", cover);


        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Source']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList sources = null;
        try {
            sources = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        try {
            ti.clearMetadata(MetadataSchema.DC_SCHEMA, "source", Item.ANY, Item.ANY);
            ti.addMetadata(MetadataSchema.DC_SCHEMA, "source", null, "ru", sources.item(0).getTextContent());
        } catch(Exception e){

        }

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Type']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList type = null;
        try {
            type = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        try {
            ti.clearMetadata(MetadataSchema.DC_SCHEMA, "type", Item.ANY, Item.ANY);
            ti.addMetadata(MetadataSchema.DC_SCHEMA, "type", null, "ru", type.item(0).getTextContent());
        } catch(Exception e){

        }

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Rights']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList rughts = null;
        try {
            rughts = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        try {
            ti.clearMetadata(MetadataSchema.DC_SCHEMA, "rights", Item.ANY, Item.ANY);
            ti.addMetadata(MetadataSchema.DC_SCHEMA, "rights", null, "ru", rughts.item(0).getTextContent());
        } catch(Exception e){

        }

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Publisher']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList publisher = null;
        try {
            publisher = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        try {
            ti.clearMetadata(MetadataSchema.DC_SCHEMA, "publisher", Item.ANY, Item.ANY);
            ti.addMetadata(MetadataSchema.DC_SCHEMA, "publisher", null, "ru", publisher.item(0).getTextContent());
        } catch(Exception e){

        }

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Contributor']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList contributor = null;
        try {
            contributor = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        try {
            String creatorsString = contributor.item(0).getTextContent();
            String[] creatorsAr = creatorsString.split(",");

            for(int i = 0; i<creatorsAr.length; i++){
                ti.addMetadata(MetadataSchema.DC_SCHEMA, "contributor", "author", "ru", creatorsAr[i]);
            }
        } catch(Exception e){

        }

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Citation']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList citation = null;
        try {
            citation = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        ti.clearMetadata(MetadataSchema.DC_SCHEMA, "citation", Item.ANY, Item.ANY);
        writeMetaDataToItemLowerCase(ti, "citation", citation);

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Format']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList format = null;
        try {
            format = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        ti.clearMetadata(MetadataSchema.DC_SCHEMA, "format", Item.ANY, Item.ANY);
        writeMetaDataToItemLowerCase(ti, "format", format);

        try {
            expr = xpath.compile("/*/*/*/*/*[local-name()='Records']/*[local-name()='Relation']");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }



        NodeList relation = null;
        try {
            relation = (NodeList) expr.evaluate(docMeta, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        ti.clearMetadata(MetadataSchema.DC_SCHEMA, "relation", Item.ANY, Item.ANY);
        writeMetaDataToItemLowerCase(ti, "relation", relation);



        ti.update();
        context.commit();



        request.setAttribute("link", HandleManager.getCanonicalForm(ti.getHandle()));

        request.getRequestDispatcher("/import/reimport-item.jsp").forward(request, response);
    }

    public void writeMetaDataToItemLowerCase(Item item, String qualifier, NodeList nodes){
        for(int j = 0; j < nodes.getLength(); j++){
            Element subjectNode = (Element) nodes.item(j);
            Node textSubject = subjectNode.getElementsByTagName("Value").item(0);
            Node qulSubject = subjectNode.getElementsByTagName("Qualifier").item(0);
            String qualtext = qulSubject.getTextContent().toLowerCase();

            item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, qulSubject.getTextContent().toLowerCase(), "ru", textSubject.getTextContent());

        }
    }

    public void writeMetaDataToItemLowerCaseSubject(Item item,  String qualifier, NodeList nodes){
        for(int j = 0; j < nodes.getLength(); j++){
            Element subjectNode = (Element) nodes.item(j);
            Node textSubject = subjectNode.getElementsByTagName("Value").item(0);
            Node qulSubject = subjectNode.getElementsByTagName("Qualifier").item(0);
            if(qulSubject.getTextContent().toLowerCase().equals("subject")){
                item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, null, "ru", textSubject.getTextContent());
            }else {
                item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, qulSubject.getTextContent().toLowerCase(), "ru", textSubject.getTextContent());
            }
        }
    }

    public void writeMetaDataToItemLowerCaseIdentifier(Item item,  String qualifier, NodeList nodes, HttpServletRequest request){
        for(int j = 0; j < nodes.getLength(); j++){
            Element subjectNode = (Element) nodes.item(j);
            Node textSubject = subjectNode.getElementsByTagName("Value").item(0);
            Node qulSubject = subjectNode.getElementsByTagName("Qualifier").item(0);
            if(qulSubject.getTextContent().toLowerCase().equals("identifier")){
                // request.setAttribute("identifier", textSubject.getTextContent());
                //item.addMetadata(MetadataSchema.DC_SCHEMA, "subject", "lcc", "ru", textSubject.getTextContent());
                item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, null, "ru", textSubject.getTextContent());
            }else {
                item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, qulSubject.getTextContent().toLowerCase(), "ru", textSubject.getTextContent());
            }
        }
    }

    public void writeMetaDataToItemLowerCaseTitle(Item item,  String qualifier, NodeList nodes){
        for(int j = 0; j < nodes.getLength(); j++){
            Element subjectNode = (Element) nodes.item(j);
            Node textSubject = subjectNode.getElementsByTagName("Value").item(0);
            Node qulSubject = subjectNode.getElementsByTagName("Qualifier").item(0);
            if(qulSubject.getTextContent().toLowerCase().equals("title")){
                item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, null, "ru", textSubject.getTextContent());
            }else {
                item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, qulSubject.getTextContent().toLowerCase(), "ru", textSubject.getTextContent());
            }
        }
    }

    public void writeMetaDataToItemLowerCaseDescr(Item item,  String qualifier, NodeList nodes){
        for(int j = 0; j < nodes.getLength(); j++){
            Element subjectNode = (Element) nodes.item(j);
            Node textSubject = subjectNode.getElementsByTagName("Value").item(0);
            Node qulSubject = subjectNode.getElementsByTagName("Qualifier").item(0);
            if(qulSubject.getTextContent().toLowerCase().equals("abstract")){
                item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, "abstract", "ru", textSubject.getTextContent());
            }
        }
    }

    public void writeMetaDataToItemLowerCaseLang(Item item,  String qualifier, NodeList nodes, HttpServletRequest request){
        for(int j = 0; j < nodes.getLength(); j++){
            Element subjectNode = (Element) nodes.item(j);
            Node textSubject = subjectNode.getElementsByTagName("Value").item(0);
            Node qulSubject = subjectNode.getElementsByTagName("Qualifier").item(0);

            request.setAttribute("wtf_lang", textSubject.getTextContent());
            item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, qulSubject.getTextContent().toLowerCase(), "ru", textSubject.getTextContent());
            //item.addMetadata(MetadataSchema.DC_SCHEMA, "subject", "lcsh", "ru", textSubject.getTextContent());
        }
    }
}
