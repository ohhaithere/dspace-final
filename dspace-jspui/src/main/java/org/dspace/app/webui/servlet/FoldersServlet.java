package org.dspace.app.webui.servlet;

import org.apache.log4j.Logger;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by lalka on 12/23/2015.
 */
public class FoldersServlet extends DSpaceServlet{

    /** Logger */
    private static Logger log = Logger.getLogger(EditProfileServlet.class);

    protected void doDSGet(Context context, HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException{

        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");


        PrintWriter out = response.getWriter();
        //   out.write(tri.next().getStringColumn("name"));
        //     out.close();
        String ifAdd = request.getParameter("action");
        if(ifAdd == null) {
            TableRowIterator tri = DatabaseManager.queryTable(context, "systems", "SELECT * FROM folders");
            request.setAttribute("systems", tri);
            request.getRequestDispatcher("/folders/folder-home.jsp").forward(request, response);
            request.getRequestDispatcher("/folders/folder-home.jsp").forward(request, response);
        }
        if(ifAdd.equals("add")){
            request.getRequestDispatcher("/folders/folder-add.jsp").forward(request, response);
        }
        if(ifAdd.equals("delete")){
            String id = request.getParameter("id");
            PreparedStatement statement = null;
            //      ResultSet rs = null;
            statement = context.getDBConnection().prepareStatement("DELETE FROM folders WHERE id="+id);
            int i = statement.executeUpdate();
            context.getDBConnection().commit();
            statement.close();
            TableRowIterator tri = DatabaseManager.queryTable(context, "systems", "SELECT * FROM folders");
            request.setAttribute("systems", tri);
            request.getRequestDispatcher("/folders/folder-home.jsp").forward(request, response);
        }
        if(ifAdd.equals("edit")){
            String id = request.getParameter("id");
            TableRowIterator tri = DatabaseManager.queryTable(context, "folders", "SELECT * FROM folders");
            while(tri.hasNext()) {

                TableRow row = tri.next();
                Integer i = row.getIntColumn("id");
                if(i.toString().equals(id)) {
                    request.setAttribute("system_name", row.getStringColumn("system_name"));
                    request.setAttribute("folder_path", row.getStringColumn("folder_path"));
                    request.setAttribute("id", row.getIntColumn("id"));
                    break;
                }
            }

            request.getRequestDispatcher("/folders/folder-add.jsp").forward(request, response);
        }
    }

    protected void doDSPost(Context context, HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException{

        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        String ee = "";
 /*     try {
          DatabaseManager.queryTable(context, "systems", "INSERT INTO public.systems (name, path) VALUES ('" + request.getParameter("system_name") + "','" + request.getParameter("system_path") + "')");
      } catch(Exception e){
          ee = e.getMessage();
          response.getWriter().write(ee);
      }

        try {
            DatabaseManager.query(context, "INSERT INTO public.systems (name, path) VALUES ('" + request.getParameter("system_name") + "','" + request.getParameter("system_path") + "')");
            final ArrayList<String> epersonColumns = new ArrayList<String>();
            epersonColumns.add("name");
            epersonColumns.add("path");
            epersonColumns.add("id");



            TableRow system = new TableRow("systems", epersonColumns);
            system.setColumn("name", request.getParameter("system_name"));
            system.setColumn("path", request.getParameter("system_path"));


            DatabaseManager.insert(context, system);
        }catch(Exception e){
            ee = e.getMessage();
            response.getWriter().write(ee);
        }*/
        try {
            if (request.getParameter("submit").equals("Создать")) {
                PreparedStatement statement = null;
                //      ResultSet rs = null;
                statement = context.getDBConnection().prepareStatement("INSERT INTO folders (system_name, folder_path) VALUES (?,?)");
                statement.setString(1, request.getParameter("system_name"));
                statement.setString(2, request.getParameter("system_path"));
                int i = statement.executeUpdate();
                context.getDBConnection().commit();
                statement.close();
            } else {
                PreparedStatement statement = null;
                //      ResultSet rs = null;
                Integer di = Integer.parseInt(request.getParameter("id"));
                statement = context.getDBConnection().prepareStatement("UPDATE folders SET system_name=?, folder_path=? WHERE id=?");
                statement.setString(1, request.getParameter("system_name"));
                statement.setString(2, request.getParameter("system_path"));
                statement.setInt(3, di);
                int i = statement.executeUpdate();
                context.getDBConnection().commit();
                statement.close();

            }
        } catch(Exception e){
            response.getWriter().write(e.getMessage());
        }
        //  rs = statement.executeQuery();
        //rs.close();


        // response.getWriter().write(ee);



        //TableRowIterator tri = DatabaseManager.queryTable(context, "systems", "SELECT * FROM systems");
        // request.setAttribute("systems", tri);
        //request.getRequestDispatcher("/system/system-home.jsp").forward(request, response);
        // response.getWriter().write(ee);
        TableRowIterator tri = DatabaseManager.queryTable(context, "folders", "SELECT * FROM folders");
        request.setAttribute("systems", tri);

        request.getRequestDispatcher("/folders/folder-home.jsp").forward(request, response);
    }
}