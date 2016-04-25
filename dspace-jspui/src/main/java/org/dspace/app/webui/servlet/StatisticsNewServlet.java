package org.dspace.app.webui.servlet;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by root on 4/17/16.
 */
public class StatisticsNewServlet extends DSpaceServlet{

    /** Logger */
    private static Logger log = Logger.getLogger(EditProfileServlet.class);

    protected void doDSGet(Context context, HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException {

        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        TableRowIterator itemVisits = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='show_item'");
   //     while(itemVisits.hasNext()) {
     //       TableRow itemVisitsRow = itemVisits.next();
     //   }

        TableRowIterator colVisits = DatabaseManager.queryTable(context, "statistic", "SELECT *FROM statistic WHERE event='show_collection'");
      //  TableRow colVisitsRow  = colVisits.next();

        TableRowIterator comVisits = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='show_community'");
       // TableRow comVisitsRow  = comVisits.next();

        TableRowIterator userLogins = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='user_login'");
       // TableRow userLoginsRow  = userLogins.next();

        TableRowIterator itemsOverall = DatabaseManager.queryTable(context, "item", "SELECT submitter_id FROM item");

        request.setAttribute("itemVisits", itemVisits.toList().size());
        request.setAttribute("colVisitsRow", colVisits.toList().size());
        request.setAttribute("comVisitsRow", comVisits.toList().size());
        request.setAttribute("userLoginsRow", userLogins.toList().size());
        request.setAttribute("itemsOverall", itemsOverall.toList().size());

        request.getRequestDispatcher("/statistics/statistics-home.jsp").forward(request, response);
    }
}
