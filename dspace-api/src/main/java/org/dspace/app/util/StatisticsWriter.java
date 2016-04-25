package org.dspace.app.util;

import org.apache.log4j.Logger;
import org.dspace.content.Item;
import org.dspace.core.Context;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * Created by root on 4/22/16.
 */
public class StatisticsWriter {

    private static Logger log = Logger.getLogger(StatisticsWriter.class);

    public void writeStatistics(Context context, String event, Item item) throws SQLException {
        PreparedStatement statement = null;
        //      ResultSet rs = null;


        if(item != null) {
            statement = context.getDBConnection().prepareStatement("INSERT INTO statistic (event, date_updated, parent_id) VALUES (?,?,?)");
            statement.setInt(3, item.getID());
        } else{
            statement = context.getDBConnection().prepareStatement("INSERT INTO statistic (event, date_updated) VALUES (?,?)");
        }
        statement.setString(1, event);
        statement.setDate(2, new Date(Calendar.getInstance().getTime().getTime()));
        int i = statement.executeUpdate();
        context.getDBConnection().commit();
        statement.close();
    }
}
