package org.dspace.app.webui.servlet;

import org.apache.log4j.Logger;
import org.dspace.app.webui.util.SoapHelper;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.w3c.dom.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;

/**
 * Created by root on 1/1/16.
 */
public class ImportServlet extends DSpaceServlet {

    /** Logger */
    private static Logger log = Logger.getLogger(EditProfileServlet.class);

    protected void doDSGet(Context context, HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException {

        
        request.setAttribute("community_id", request.getParameter("community_id"));
        request.setAttribute("collection_id", request.getParameter("collection_id"));

        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        request.getRequestDispatcher("/import/import-home.jsp").forward(request, response);

    }

    protected void doDSPost(Context context, HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException{

        SoapHelper sh = new SoapHelper();

        String test = request.getParameter("action");

        if(test == null) {
            String uuid = request.getParameter("uuid_search");
            Document doc = sh.getRecordById(uuid);


            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = null;
            try {
                transformer = tf.newTransformer();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            try {
                transformer.transform(new DOMSource(doc), new StreamResult(writer));
            } catch (TransformerException e) {
                e.printStackTrace();
            }
            String output = writer.getBuffer().toString().replaceAll("\n|\r", "");


            request.setAttribute("community_id", request.getParameter("community_id"));
            request.setAttribute("collection_id", request.getParameter("collection_id"));
            request.setAttribute("uuid_search", uuid);
            request.setAttribute("document", doc);

            request.getRequestDispatcher("/import/import-item.jsp").forward(request, response);
        }
        else {
            String name = request.getParameter("name");
            String title = request.getParameter("title");

            if(name.equals(""))
                name = null;

            if(title.equals(""))
                title = null;

            Document doc = sh.getRecordByName(name, title);


            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = null;
            try {
                transformer = tf.newTransformer();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            try {
                transformer.transform(new DOMSource(doc), new StreamResult(writer));
            } catch (TransformerException e) {
                e.printStackTrace();
            }
            String output = writer.getBuffer().toString().replaceAll("\n|\r", "");


            request.setAttribute("community_id", request.getParameter("community_id"));
            request.setAttribute("collection_id", request.getParameter("collection_id"));
            request.setAttribute("document", doc);

            request.getRequestDispatcher("/import/import-items.jsp").forward(request, response);


        }
    }


}
