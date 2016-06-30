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
            try {
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
                                exists = true;
                                TableRow row = tri.next();
                                itemId = row.getIntColumn("resource_id");
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

                if(exists == false) {
                    wsitem = WorkspaceItem.createMass(context, col, false);
                    itemItem = wsitem.getItem();
                    //response.getWriter().write("test");

                    itemItem.setOwningCollection(col);
                }
                else{
                    itemItem = Item.find(context,itemId);
                    itemItem.clearDC(Item.ANY, Item.ANY, Item.ANY);
                    itemItem.update();
                }



                try {
                    NodeList titleNode = record.getElementsByTagName("Title");
                    //  itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "title", null, "ru", tex.getTextContent());
                    writeMetaDataToItemLowerCaseTitle(itemItem, "title", titleNode);
                    titleNode = null;
                } catch (Exception e) {
                    //response.getWriter().write(e.getMessage());
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
                        String contribs[] = authorName.split(",");
                        for (int l = 0; l < contribs.length; l++) {
                            itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "contributor", "author", "ru", contribs[l]);
                        }
                        itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "creator", null, "ru", author.getTextContent());
                    }
                    author = null;
                } catch (Exception e) {
                    //response.getWriter().write(e.getMessage());
                }

                try {
                    Node contrib = record.getElementsByTagName("Contributor").item(0);
                    String authorName = contrib.getTextContent();
                    if(!authorName.equals("|||") && (authorName != null) && (!authorName.equals(""))) {
                        String contribs[] = authorName.split(",");
                        for (int l = 0; l < contribs.length; l++) {
                            itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "contributor", "author", "ru", contribs[l]);
                        }
                        //itemItem.addMetadata(MetadataSchema.DC_SCHEMA, "creator", null, "ru", author.getTextContent());
                    }
                    contrib = null;
                } catch (Exception e) {
                    //response.getWriter().write(e.getMessage());
                }


                try {
                    NodeList subjects = record.getElementsByTagName("Subject");
                    writeMetaDataToItemLowerCaseSubject(itemItem, "subject", subjects);
                    subjects = null;
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

                    NodeList coverages = record.getElementsByTagName("Coverage");
                    writeMetaDataToItemLowerCase(itemItem, "coverage", coverages);
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


                try {
                    Node link = record.getElementsByTagName("Link").item(0);

                    String firstUrl = "http://lib.ssau.ru/download?fname=";

                    String linkEncode = URLEncoder.encode(link.getTextContent(), "UTF-8");

                    String filenamelel = link.getTextContent().substring(link.getTextContent().lastIndexOf('\\') + 1);

                    InputStream iss = new URL(firstUrl + linkEncode).openStream();

                    InputStream issforPdf = new URL(firstUrl + linkEncode).openStream();


                    try {
                        PDFTextStripper pdfStripper = null;
                        PDDocument docum = null;
                        PDFParser parser = new PDFParser(issforPdf);
                        COSDocument cosDoc = null;

                        parser.parse();
                        cosDoc = parser.getDocument();
                        pdfStripper = new PDFTextStripper();
                        docum = new PDDocument(cosDoc);
                        //pdfStripper.getText(docum);
                        String parsedText = pdfStripper.getText(docum);
                        //log.info(parsedText);
                        Integer fifty = (Integer) Math.round(parsedText.length() / 2);
                        if(fifty < 0){
                            fifty = fifty *(-1);
                        }
                        Integer toCut = 500;
                        if ((parsedText.length() - fifty) < 500) {
                            toCut = parsedText.length();
                        }
                        String subText = parsedText.substring(fifty, fifty + toCut - 1);
                        try {
                            subText = subText.substring(subText.indexOf(".") + 1);
                        } catch(Exception e){

                        }
                        itemItem.addMetadata("dc", "textpart", null, null, subText + "...");
                    } catch(Exception e){

                    }

                    if(exists == false) {
                        itemItem.createBundle("ORIGINAL");
                        Bitstream b = itemItem.getBundles("ORIGINAL")[0].createBitstream(iss);
                        b.setName(filenamelel);
                        b.setDescription("from 1C");
                        b.setSource("1C");

                        itemItem.getBundles("ORIGINAL")[0].setPrimaryBitstreamID(b.getID());


                        BitstreamFormat bf = null;

                        bf = FormatIdentifier.guessFormat(context, b);
                        b.setFormat(bf);

                        b.update();
                    }
                    itemItem.update();


                    iss.close();
                } catch (Exception e) {

                }


                if(exists == false) {
                    HandleManager.createHandle(context, itemItem);
                    Metadatum[] dcorevalues2 = itemItem.getMetadata("dc", "identifier", null,
                            Item.ANY);

                    Metadatum tit = dcorevalues2[0];


                    // Group groups = Group.findByName(context, "Anonymous");
                    TableRow row = DatabaseManager.row("collection2item");

                    PreparedStatement statement = null;
                    //      ResultSet rs = null;
                    statement = context.getDBConnection().prepareStatement("DELETE FROM workspaceitem WHERE workspace_item_id=" + wsitem.getID());
                    int ij = statement.executeUpdate();
                    // row.setColumn("collection_id", col.getID());
                    // row.setColumn("item_id", itemItem.getID());
                    // DatabaseManager.insert(context, row);


                    itemItem.inheritCollectionDefaultPolicies(col);

                    itemItem.setArchived(true);

                    StatisticsWriter sw = new StatisticsWriter();
                    sw.writeStatistics(context, "item_added", null);
                } else{

                }









                if(ConfigurationManager.getProperty("workflow","workflow.framework").equals("xmlworkflow")){
                    try{
                        XmlWorkflowManager.start(context, wsitem);
                    }catch (Exception e){

                        throw new ServletException(e);
                    }
                }else{
                    WorkflowManager.start(context, wsitem);
                }

              ;
                itemItem.update();
                context.commit();


                //break;
            } catch (Exception e) {
            }

        }
    }

    private static void writeMetaDataToItemLowerCase(Item item,  String qualifier, NodeList nodes){
        for(int j = 0; j < nodes.getLength(); j++){
            Element subjectNode = (Element) nodes.item(j);
            Node textSubject = subjectNode.getElementsByTagName("Value").item(0);
            Node qulSubject = subjectNode.getElementsByTagName("Qualifier").item(0);
            item.addMetadata(MetadataSchema.DC_SCHEMA, qualifier, qulSubject.getTextContent().toLowerCase(), "ru", textSubject.getTextContent());
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

}
