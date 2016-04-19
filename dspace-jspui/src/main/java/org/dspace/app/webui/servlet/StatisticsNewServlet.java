package org.dspace.app.webui.servlet;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRowIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by root on 4/17/16.
 */
public class StatisticsNewServlet {

    /** Logger */
    private static Logger log = Logger.getLogger(EditProfileServlet.class);

    protected void doDSGet(Context context, HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException {

        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        TableRowIterator tri = DatabaseManager.queryTable(context, "item", "SELECT COUNT(*) FROM item INNER JOIN ");
    }
}
