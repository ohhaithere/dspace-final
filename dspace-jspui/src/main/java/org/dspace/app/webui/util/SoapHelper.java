package org.dspace.app.webui.util;

import org.apache.log4j.Logger;
import org.dspace.app.webui.servlet.admin.EditCommunitiesServlet;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by root on 1/1/16.
 */
public class SoapHelper {

    private static Logger log = Logger.getLogger(EditCommunitiesServlet.class);

    public Document getRecordByCode(String id){
        HttpURLConnection connection = null;
        URL url = null;

        try {
            url = new URL("http://doc.ssau.ru/ssau_biblioteka/ws/BiblRecords.1cws");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            connection = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setRequestProperty("Authorization", "Basic d2Vic2VydmljZTp3ZWJzZXJ2aWNl");
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        connection.setDoOutput(true);

        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(
                    connection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Charset charset = Charset.forName("UTF-8");
        try {

            wr.write(new String("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:bibl=\"http://www.omega-spb.ru/bibl\">\n" +
                    "   <soap:Header/>\n" +
                    "   <soap:Body>\n" +
                    "      <bibl:GetRecordList>\n" +
                    "         <bibl:CodeName>ID</bibl:CodeName>\n" +
                    "         <bibl:CodesList>" + id + "</bibl:CodesList>\n" +
                    "         <bibl:Separator>,</bibl:Separator>\n" +
                    "         <bibl:needRUSMARC>false</bibl:needRUSMARC>\n" +
                    "         <bibl:needKatalogCard>false</bibl:needKatalogCard>\n" +
                    "         <bibl:needCopies>false</bibl:needCopies>\n" +
                    "         <bibl:needSumInfo>false</bibl:needSumInfo>\n" +
                    "         <bibl:needlemma>false</bibl:needlemma>\n" +
                    "      </bibl:GetRecordList>\n" +
                    "   </soap:Body>\n" +
                    "</soap:Envelope>").getBytes(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream is = null;
        try {
            is = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
        String line;
        String responseString = "";
        try {
            while((line = rd.readLine()) != null) {
                responseString+=line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DocumentBuilder db = null;

        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        InputSource is2 = new InputSource();
        is2.setCharacterStream(new StringReader(responseString));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.info("WOW " + e.getMessage());
        }

        try {
            return builder.parse(new ByteArrayInputStream(responseString.getBytes()));
        } catch (SAXException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public Document getRecordByName(String name, String title, HttpServletRequest request){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.info("WOW " + e.getMessage());
        }

        String bothStr = "";

        if((name != null) && (title != null)){
            bothStr = "%20and%20";
        }

        

        Document doc = null;
        try {
            if(name != null){
            name = "dc.title=\""+URLEncoder.encode(name, "UTF-8").replace("+", "%20") + "$\"";
        } else{
            name = "";
        }

        if(title != null){
            title = "dc.creator=\""+URLEncoder.encode(title, "UTF-8").replace("+", "%20") + "$\"";
        }else {
            title = "";
        }
            request.setAttribute("url", ("http://irbisnew.msal.local:210/book2?version=1.1&operation=searchRetrieve&maximumRecords=20&recordSchema=user&query=(" + name + bothStr + title + ")").replace("\"", "&quot;"));
            log.info("WTF " + "http://irbisnew.msal.local:210/book2?version=1.1&operation=searchRetrieve&maximumRecords=20&recordSchema=user&query=(" + name + bothStr + title + ")");
            doc = db.parse(new URL("http://irbisnew.msal.local:210/book2?version=1.1&operation=searchRetrieve&maximumRecords=20&recordSchema=user&query=(" + name + bothStr + title + ")").openStream());
        } catch (SAXException e) {
            log.info("wtf " + e.getMessage());
        } catch (IOException e) {
            log.info("damn " + e.getMessage());;
        }
        return doc;
    }

    public Document getRecordByUrl(String name, String title, HttpServletRequest request){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.info("WOW " + e.getMessage());
        }

       

        Document doc = null;
        try {

            log.info("WTF 2" + name);
            doc = db.parse(new URL(name).openStream());
        } catch (SAXException e) {
            log.info("wtf " + e.getMessage());
        } catch (IOException e) {
            log.info("damn " + e.getMessage());;
        }
        return doc;
    }

    public Document getRecordById(String id, HttpServletRequest request){
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.info("WOW " + e.getMessage());
        }

        String bothStr = "";

        

        Document doc = null;
        try {
            if(id != null){
            id = "dc.resourceIdentifier=\""+URLEncoder.encode(id, "UTF-8").replace("+", "%20") + "$\"";
        } else{
            id = "";
        }
            request.setAttribute("url", ("http://irbisnew.msal.local:210/book2?version=1.1&operation=searchRetrieve&maximumRecords=20&recordSchema=user&query=("  + id  + ")").replace("\"", "&quot;"));
            log.info("WTF " + "http://irbisnew.msal.local:210/book2?version=1.1&operation=searchRetrieve&maximumRecords=20&recordSchema=user&query=(" + id  + ")");
            doc = db.parse(new URL("http://irbisnew.msal.local:210/book2?version=1.1&operation=searchRetrieve&maximumRecords=20&recordSchema=user&query=(" + id + ")").openStream());
        } catch (SAXException e) {
            log.info("wtf " + e.getMessage());
        } catch (IOException e) {
            log.info("damn " + e.getMessage());;
        }
        return doc;
    }

    public void writeLink(String iden, String link){
        HttpURLConnection connection = null;
        URL url = null;
        try {
            url = new URL("http://doc.ssau.ru/ssau_biblioteka/ws/DspaceIntegration.1cws");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            connection = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setRequestProperty("Authorization", "Basic d2Vic2VydmljZTp3ZWJzZXJ2aWNl");
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        connection.setDoOutput(true);

        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(
                    connection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Charset charset = Charset.forName("UTF-8");
        try {

            wr.write(new String("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:imc=\"http://imc.parus-s.ru\" xmlns:imc1=\"http://www.imc-dspace.org\">\n" +
                    "   <soap:Header/>\n" +
                    "   <soap:Body>\n" +
                    "      <imc:PutRecordsInfo>\n" +
                    "         <imc:Records>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "            <imc1:Records>\n" +
                    "               <imc1:Identifier>\n" +
                    "                  <imc1:Qualifier>Identifier</imc1:Qualifier>\n" +
                    "                  <imc1:Value>"+iden+"</imc1:Value>\n" +
                    "               </imc1:Identifier>\n" +
                    "               <imc1:Link>"+link+"</imc1:Link>\n" +
                    "            </imc1:Records>\n" +
                    "         </imc:Records>\n" +
                    "      </imc:PutRecordsInfo>\n" +
                    "   </soap:Body>\n" +
                    "</soap:Envelope>").getBytes(charset));
        } catch (IOException e) {
            log.info("smth is wrong 1");
        }
        try {
            wr.close();
        } catch (IOException e) {
            log.info("smth is wrong 2");
        }
        InputStream is = null;
        try {
            is = connection.getInputStream();
        } catch (IOException e) {
            log.info("smth is wrong 3");
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
        String line;
        String responseString = "";
        try {
            while((line = rd.readLine()) != null) {
                responseString+=line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("LINKTOWRITE: "+link);
        log.info("RESPONSSTRING: "+responseString);
    }



}
