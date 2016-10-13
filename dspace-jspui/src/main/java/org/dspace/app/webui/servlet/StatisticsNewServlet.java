package org.dspace.app.webui.servlet;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by root on 4/17/16.
 */
public class StatisticsNewServlet extends DSpaceServlet{

    /** Logger */
    private static Logger log = Logger.getLogger(EditProfileServlet.class);

    protected void doDSGet(Context context, HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException {

        EPerson person = context.getCurrentUser();
        if(person == null){
            RequestDispatcher dispatch = getServletContext().getNamedDispatcher("password-login");
            dispatch.forward(request, response);
        }

        String dateStart = null;
        String dateEnd = null;

        dateStart = request.getParameter("dateStart");
        dateEnd = request.getParameter("dateEnd");
        if(dateStart == null) {



            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");

            TableRowIterator itemVisits = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='show_item'");
            //     while(itemVisits.hasNext()) {
            //       TableRow itemVisitsRow = itemVisits.next();
            //   }

            TableRowIterator colVisits = DatabaseManager.queryTable(context, "statistic", "SELECT *FROM statistic WHERE event='show_collection'");

            TableRowIterator itemsUploaded = DatabaseManager.queryTable(context, "statistic", "SELECT *FROM statistic WHERE event='item_added'");

            TableRowIterator searchesDone = DatabaseManager.queryTable(context, "statistic", "SELECT *FROM statistic WHERE event='search_done'");
            //  TableRow colVisitsRow  = colVisits.next();

            TableRowIterator comVisits = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='show_community'");
            // TableRow comVisitsRow  = comVisits.next();

            TableRowIterator userLogins = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='user_login'");
            // TableRow userLoginsRow  = userLogins.next();

            TableRowIterator itemsOverall = DatabaseManager.queryTable(context, "item", "SELECT submitter_id FROM item WHERE in_archive=true AND owning_collection IS NOT NULL");


            request.setAttribute("itemVisits", itemVisits.toList().size());
            request.setAttribute("colVisitsRow", colVisits.toList().size());
            request.setAttribute("comVisitsRow", comVisits.toList().size());
            request.setAttribute("userLoginsRow", userLogins.toList().size());
            request.setAttribute("searchesDone", searchesDone.toList().size());
            request.setAttribute("itemsUploaded", itemsUploaded.toList().size());
            request.setAttribute("itemsOverall", itemsOverall.toList().size());
        } else {
                String[] checkStart = dateStart.split("-");
                String[] checkEnd = dateEnd.split("-");
                Integer month = Integer.parseInt(checkStart[1]);

                log.info("fuck: " + dateStart);

                TableRowIterator itemVisits = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='show_item' AND date_updated >= '" + dateStart + "' AND date_updated <= '" + dateEnd + "'");
                //     while(itemVisits.hasNext()) {
                //       TableRow itemVisitsRow = itemVisits.next();
                //   }

                TableRowIterator colVisits = DatabaseManager.queryTable(context, "statistic", "SELECT *FROM statistic WHERE event='show_collection' AND date_updated >= '" + dateStart + "' AND date_updated <= '" + dateEnd + "'");
                //  TableRow colVisitsRow  = colVisits.next();

                TableRowIterator comVisits = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='show_community' AND date_updated >= '" + dateStart + "' AND date_updated <= '" + dateEnd + "'");

                TableRowIterator itemsUploaded = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='item_added' AND date_updated >= '" + dateStart + "' AND date_updated <= '" + dateEnd + "'");

                TableRowIterator searchesDone = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='search_done' AND date_updated >= '" + dateStart + "' AND date_updated <= '" + dateEnd + "'");

                // TableRow comVisitsRow  = comVisits.next();

                TableRowIterator userLogins = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='user_login' AND date_updated >= '" + dateStart + "' AND date_updated <= '" + dateEnd + "'");
                // TableRow userLoginsRow  = userLogins.next();

                TableRowIterator itemsOverall = DatabaseManager.queryTable(context, "item", "SELECT submitter_id FROM item WHERE in_archive=true AND owning_collection IS NOT NULL");

                String dates = "<b>Отчет с " + dateStart + " по " + dateEnd + "</b>";

                request.setAttribute("itemVisits", itemVisits.toList().size());
                request.setAttribute("colVisitsRow", colVisits.toList().size());
                request.setAttribute("comVisitsRow", comVisits.toList().size());
                request.setAttribute("userLoginsRow", userLogins.toList().size());
                request.setAttribute("itemsOverall", itemsOverall.toList().size());
                request.setAttribute("searchesDone", searchesDone.toList().size());
                request.setAttribute("itemsUploaded", itemsUploaded.toList().size());
                request.setAttribute("dates", dates);

        }

        request.getRequestDispatcher("/statistics/statistics-home.jsp").forward(request, response);
    }

    protected void doDSPost(Context context, HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException {

        try {
            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");

            String dateStart = request.getParameter("dateStart");
            String dateEnd = request.getParameter("dateEnd");

            String[] checkStart = dateStart.split("-");
            String[] checkEnd = dateEnd.split("-");
            Integer month = Integer.parseInt(checkStart[1]);
            if(month > 12)
                throw new Exception();

            month = Integer.parseInt(checkEnd[1]);
            if(month > 12)
                throw new Exception();

            Integer day = Integer.parseInt(checkStart[2]);
            if(day > 31)
                throw new Exception();

            day = Integer.parseInt(checkEnd[2]);
            if(day > 31)
                throw new Exception();

            log.info("fuck: " + dateStart);

            TableRowIterator itemVisits = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='show_item' AND date_updated >= '" + dateStart + "' AND date_updated <= '" + dateEnd + "'");
            //     while(itemVisits.hasNext()) {
            //       TableRow itemVisitsRow = itemVisits.next();
            //   }

            TableRowIterator colVisits = DatabaseManager.queryTable(context, "statistic", "SELECT *FROM statistic WHERE event='show_collection' AND date_updated >= '" + dateStart + "' AND date_updated <= '" + dateEnd + "'");
            //  TableRow colVisitsRow  = colVisits.next();

            TableRowIterator comVisits = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='show_community' AND date_updated >= '" + dateStart + "' AND date_updated <= '" + dateEnd + "'");

            TableRowIterator itemsUploaded = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='item_added' AND date_updated >= '" + dateStart + "' AND date_updated <= '" + dateEnd + "'");

            TableRowIterator searchesDone = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='search_done' AND date_updated >= '" + dateStart + "' AND date_updated <= '" + dateEnd + "'");

            // TableRow comVisitsRow  = comVisits.next();

            TableRowIterator userLogins = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='user_login' AND date_updated >= '" + dateStart + "' AND date_updated <= '" + dateEnd + "'");
            // TableRow userLoginsRow  = userLogins.next();

            TableRowIterator itemsOverall = DatabaseManager.queryTable(context, "item", "SELECT submitter_id FROM item WHERE in_archive=true AND owning_collection IS NOT NULL");

            String dates = "<b>Отчет с " + dateStart + " по " + dateEnd + "</b>";

            request.setAttribute("itemVisits", itemVisits.toList().size());
            request.setAttribute("colVisitsRow", colVisits.toList().size());
            request.setAttribute("comVisitsRow", comVisits.toList().size());
            request.setAttribute("userLoginsRow", userLogins.toList().size());
            request.setAttribute("itemsOverall", itemsOverall.toList().size());
            request.setAttribute("searchesDone", searchesDone.toList().size());
            request.setAttribute("itemsUploaded", itemsUploaded.toList().size());
            request.setAttribute("dates", dates);

            request.getRequestDispatcher("/statistics/statistics-home.jsp").forward(request, response);
        } catch(Exception e){
            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");

            TableRowIterator itemVisits = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='show_item'");
            //     while(itemVisits.hasNext()) {
            //       TableRow itemVisitsRow = itemVisits.next();
            //   }

            TableRowIterator colVisits = DatabaseManager.queryTable(context, "statistic", "SELECT *FROM statistic WHERE event='show_collection'");

            TableRowIterator itemsUploaded = DatabaseManager.queryTable(context, "statistic", "SELECT *FROM statistic WHERE event='item_added'");

            TableRowIterator searchesDone = DatabaseManager.queryTable(context, "statistic", "SELECT *FROM statistic WHERE event='search_done'");
            //  TableRow colVisitsRow  = colVisits.next();

            TableRowIterator comVisits = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='show_community'");
            // TableRow comVisitsRow  = comVisits.next();

            TableRowIterator userLogins = DatabaseManager.queryTable(context, "statistic", "SELECT * FROM statistic WHERE event='user_login'");
            // TableRow userLoginsRow  = userLogins.next();

            TableRowIterator itemsOverall = DatabaseManager.queryTable(context, "item", "SELECT submitter_id FROM item WHERE in_archive=true AND owning_collection IS NOT NULL");


            request.setAttribute("itemVisits", itemVisits.toList().size());
            request.setAttribute("colVisitsRow", colVisits.toList().size());
            request.setAttribute("comVisitsRow", comVisits.toList().size());
            request.setAttribute("userLoginsRow", userLogins.toList().size());
            request.setAttribute("searchesDone", searchesDone.toList().size());
            request.setAttribute("itemsUploaded", itemsUploaded.toList().size());
            request.setAttribute("itemsOverall", itemsOverall.toList().size());
            request.setAttribute("error", "Даты были введены неверно");

            request.getRequestDispatcher("/statistics/statistics-home.jsp").forward(request, response);
        }


    }
}
