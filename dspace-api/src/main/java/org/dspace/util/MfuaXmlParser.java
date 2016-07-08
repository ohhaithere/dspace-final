package org.dspace.util;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.dspace.app.util.StatisticsWriter;
import org.dspace.content.*;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.handle.HandleManager;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;
import org.dspace.workflow.WorkflowManager;
import org.dspace.xmlworkflow.XmlWorkflowManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public  class MfuaXmlParser {

    public static void createItems(Document doc, Context context, Collection col){
        NodeList records = doc.getElementsByTagName("Records");
        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
                Boolean exists = false;
                Integer itemId = 0;
             try {

            NodeList identifier = record.getElementsByTagName("Identifier");
            //  itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "title", null, "ru", tex.getTextContent());
            for(int k = 0; k < identifier.getLength(); k++){
                Element subjectNode = (Element) identifier.item(k);
                Node textSubject = subjectNode.getElementsByTagName("Value").item(0);
                Node qulSubject = subjectNode.getElementsByTagName("Qualifier").item(0);
                if(qulSubject.getTextContent().toLowerCase().equals("identifier")){
                    TableRowIterator tri = DatabaseManager.queryTable(context, "metadatavalue", "SELECT resource_id, text_value FROM metadatavalue WHERE text_value='"+textSubject.getTextContent()+"'");
                    if(tri.hasNext()){

                        TableRow row = tri.next();

                    }
                    //item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, null, "ru", textSubject.getTextContent());
                    //SoapHelper sh = new SoapHelper();
                    //sh.writeLink(qualifier, "http://dspace.ssau.ru/jspui/handle/"+item.getHandle());
                }
            }
            identifier = null;
        } catch (Exception e) {

        }

        WorkspaceItem wsitem = null;
        Item itemItem = null;

        try{
            wsitem = WorkspaceItem.createMass(context, col, false);
            itemItem = wsitem.getItem();
            //response.getWriter().write("test");

            itemItem.setOwningCollection(col);
        } catch(Exception e){

        }




        try{
            NodeList titleNode = record.getElementsByTagName("Title");
            //  itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "title", null, "ru", tex.getTextContent());
            writeMetaDataToItemLowerCaseTitle(itemItem, "title", titleNode);
            titleNode = null;
        } catch(Exception e){

        }
        

        try {
            NodeList identifier = record.getElementsByTagName("Identifier");
            //  itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "title", null, "ru", tex.getTextContent());
            writeMetaDataToItemLowerCaseIdentifier(itemItem, "identifier", identifier);
            identifier = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }


        try {
            Node author = record.getElementsByTagName("Creator").item(0);
            String authorName = author.getTextContent();
            if(!authorName.equals("|||") && (authorName != null) && (!authorName.equals(""))) {
                itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "contributor", "author", "ru", author.getTextContent());
            }
            author = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

        try {
            Node contrib = record.getElementsByTagName("Contributor").item(0);
            String authorName = contrib.getTextContent();
            if(!authorName.equals("|||") && (authorName != null) && (!authorName.equals(""))) {
                    itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "contributor", "author", "ru", authorName);
                //itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "creator", null, "ru", author.getTextContent());
            }
            contrib = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

        try {
            NodeList descrs = record.getElementsByTagName("Description");
            writeMetaDataToItemLowerCase(itemItem, "description", descrs);
            descrs = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

        try {
            Node date = record.getElementsByTagName("Date").item(0);

            itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "date", "issued", "ru", date.getTextContent());
            date = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

        try {
            Node publisher = record.getElementsByTagName("Publisher").item(0);

            itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "publisher", null, "ru", publisher.getTextContent());
            publisher = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

        try {
            Node type = record.getElementsByTagName("Type").item(0);

            itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "type", null, "ru", type.getTextContent());
            type = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

        try {
            Node source = record.getElementsByTagName("Source").item(0);

            itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "source", null, "ru", source.getTextContent());
            source = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

        try {
            Node rights = record.getElementsByTagName("Rights").item(0);

            itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "rights", null, "ru", rights.getTextContent());
            rights = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

        try {
            NodeList formats = record.getElementsByTagName("Format");
            writeMetaDataToItemLowerCase(itemItem, "format", formats);
            formats = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

        try {
            NodeList languages = record.getElementsByTagName("Language");
            writeMetaDataToItemLowerCase(itemItem, "language", languages);
            languages = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

        try {
            NodeList relations = record.getElementsByTagName("Relation");
            writeMetaDataToItemLowerCase(itemItem, "relation", relations);
            relations = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

        try {
            NodeList relations = record.getElementsByTagName("Subject");
            writeMetaDataToItemLowerCase(itemItem, "subject", relations);
            relations = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

      try{
            NodeList coverages = record.getElementsByTagName("Coverage");
            writeMetaDataToItemLowerCase(itemItem, "subject", coverages);
            coverages = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }
        

        try {

            NodeList citation = record.getElementsByTagName("Citation");
            writeMetaDataToItemLowerCase(itemItem, "citation", citation);
            citation = null;
        } catch (Exception e) {
            //response.getWriter().write(e.getMessage());
        }

        DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String dateNow = df.format(today);

        try {
            itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "date", "accessioned", "ru", dateNow);
        } catch (Exception e1) {

        }
        try {
            itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "date", "available", "ru", dateNow);
        } catch (Exception e2) {

        }

        itemItem.setDiscoverable(true);

        //itemItem.update();


        



        try{
            HandleManager.createHandle(context, itemItem);
            Metadatum[] dcorevalues2 = itemItem.getMetadata("dc", "identifier", null,
                    Item.ANY);

           // Metadatum tit = dcorevalues2[0];


            // Group groups = Group.findByName(context, "Anonymous");
            TableRow row = DatabaseManager.row("collection2item");

            PreparedStatement statement = null;
       //         ResultSet rs = null;
            statement = context.getDBConnection().prepareStatement("DELETE FROM workspaceitem WHERE workspace_item_id=" + wsitem.getID());
            int ij = statement.executeUpdate();
             row.setColumn("collection_id", col.getID());
             row.setColumn("item_id", itemItem.getID());
             DatabaseManager.insert(context, row);


            itemItem.inheritCollectionDefaultPolicies(col);

            itemItem.setArchived(true);

            StatisticsWriter sw = new StatisticsWriter();
            sw.writeStatistics(context, "item_added", null);



        if(ConfigurationManager.getProperty("workflow","workflow.framework").equals("xmlworkflow")){
            try{
                XmlWorkflowManager.start(context, wsitem);
            }catch (Exception e){
         //       log.error(LogManager.getHeader(context, "Error while starting xml workflow", "Item id: "), e);
                try {
                    throw new ServletException(e);
                } catch (ServletException e1) {
                    e1.printStackTrace();
                }
            }
        }else{
            try {
                WorkflowManager.start(context, wsitem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        
        itemItem.update();
        context.commit();
        }
        catch(Exception e){

        }

        }
    }

    private static void writeMetaDataToItemLowerCase(Item item,  String qualifier, NodeList nodes){
        for(int j = 0; j < nodes.getLength(); j++){
            Element subjectNode = (Element) nodes.item(j);
            Node textSubject = subjectNode.getElementsByTagName("Value").item(0);
            Node qulSubject = subjectNode.getElementsByTagName("Qualifier").item(0);
            item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, qulSubject.getTextContent().toLowerCase().replace(" ", ""), "ru", textSubject.getTextContent());
        }
    }

    private static void writeMetaDataToItemLowerCaseSubject(Item item,  String qualifier, NodeList nodes){
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

    private static void writeMetaDataToItemLowerCaseIdentifier(Item item,  String qualifier, NodeList nodes){
        for(int j = 0; j < nodes.getLength(); j++){
            Element subjectNode = (Element) nodes.item(j);
            Node textSubject = subjectNode.getElementsByTagName("Value").item(0);
            Node qulSubject = subjectNode.getElementsByTagName("Qualifier").item(0);
            if(qulSubject.getTextContent().toLowerCase().equals("identifier")){
                item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, null, "ru", textSubject.getTextContent());
                // sh.writeLink(textSubject.getTextContent(), HandleManager.getCanonicalForm(item.getHandle()));
            }else {
                if(qulSubject.getTextContent().toLowerCase().equals("doi")){
                    item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, "uri", "ru", textSubject.getTextContent());
                } else {
                    item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, qulSubject.getTextContent().toLowerCase(), "ru", textSubject.getTextContent());
                }
            }
        }
    }

    private static void writeMetaDataToItemLowerCaseTitle(Item item,  String qualifier, NodeList nodes){
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

    public static void writeMetaDataToItemLowerCaseWithoutQ(Item item, String qualifier, NodeList nodes){
        for(int j = 0; j < nodes.getLength(); j++){
            Element subjectNode = (Element) nodes.item(j);
            Node textSubject = subjectNode.getElementsByTagName("Value").item(0);
            Node qulSubject = subjectNode.getElementsByTagName("Qualifier").item(0);
            String qualtext = qulSubject.getTextContent().toLowerCase();

            item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, null, "ru", textSubject.getTextContent());

        }
    }

}