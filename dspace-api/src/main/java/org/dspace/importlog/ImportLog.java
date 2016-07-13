package org.dspace.importlog;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class ImportLog extends DSpaceObject {

	private static final Logger log = Logger.getLogger(ImportLog.class);
	private final TableRow myRow;
	private static final String tableName = "import_log";

	public ImportLog(Context context, TableRow row) throws SQLException {
		super(context);

		// Ensure that my TableRow is typed.
		if (null == row.getTable())
			row.setTable(tableName);

		myRow = row;

		clearDetails();
	}

	/**
	 * Creates new folder
	 * @param context Context
	 * @return Folder instance
	 * @throws SQLException
	 * @throws AuthorizeException
	 */
	public static ImportLog create(Context context, String importId) throws SQLException {
		// Create a table row
		TableRow row = DatabaseManager.create(context, tableName);
		ImportLog f = new ImportLog(context, row);
		f.setDate(new Date());
		f.setImportId(importId);
		
		log.info(LogManager.getHeader(context, "create_import_log_record", "record_id=" + f.getID()));
		
		return f;
	}
	
	@Override
	public int getType() {
		return 0;
	}

	@Override
	public int getID() {
		return myRow.getIntColumn("id");
	}

	@Override
	public String getHandle() {
		return null;
	}

	@Override
	public void update() throws SQLException {
		DatabaseManager.update(ourContext, myRow);
		log.info(LogManager.getHeader(ourContext, "update_import_log", "record_id=" + getID()));

	}

	@Override
	public void updateLastModified() {
	}
	
	public Integer getYear() {
		return myRow.getIntColumn("year");
	}
	
	public void setYear(Integer year) {
		myRow.setColumn("year", year);
	}
	
	@Override
	public String getName() {
		return myRow.getStringColumn("name");
	}
	
	public void setName(String name) {
		myRow.setColumn("name", name);
	}
	
	public String getAuthors() {
		return myRow.getStringColumn("authors");
	}
	
	public void setAuthors(String authors) {
		myRow.setColumn("authors", authors);
	}
	
	public Integer getResourceId() {
		return myRow.getIntColumn("resource_id");
	}
	
	public void setResourceId(Integer resourceId) {
		myRow.setColumn("resource_id", resourceId);
	}
	
	public Boolean getDuplicate() {
		return myRow.getBooleanColumn("duplicate");
	}
	
	public void setDuplicate(Boolean duplicate) {
		myRow.setColumn("duplicate", duplicate);
	}
	
	public Date getDate() {
		return myRow.getDateColumn("date");
	}
	
	public void setDate(Date date) {
		myRow.setColumn("date", date);
	}
	
	public String getImportId() {
		return myRow.getStringColumn("import_id");
	}
	
	public void setImportId(String importId) {
		myRow.setColumn("import_id", importId);
	}
	
	public static ImportLog[] getReport(Context context, String importId, int offset, int limit) throws SQLException {
		TableRowIterator rows = DatabaseManager.query(context, "SELECT * FROM " + tableName + " WHERE import_id=? ORDER BY date ASC OFFSET ? LIMIT ?", importId, new Integer(offset), new Integer(limit));
		try {
            List<TableRow> gRows = rows.toList();
            ImportLog[] folders = new ImportLog[gRows.size()];

            for (int i = 0; i < gRows.size(); i++) {
                TableRow row = gRows.get(i);
                folders[i] = new ImportLog(context, row);
            }

            return folders;
        } finally {
            if (rows != null) {
                rows.close();
            }
        }
	}
	
	public static int getReportLength(Context context, String importId) throws SQLException {
		TableRowIterator rows = DatabaseManager.query(context, "SELECT COUNT(*) as count FROM " + tableName + " WHERE import_id=?", importId);
		return rows.next().getIntColumn("count");
	}
	
	public static String[] getReportsId(Context context, Date date) throws SQLException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Timestamp from = new Timestamp(cal.getTimeInMillis());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Timestamp till = new Timestamp(cal.getTimeInMillis());
		TableRowIterator rows = DatabaseManager.query(context, "SELECT DISTINCT import_id FROM " + tableName + " WHERE date >= ? AND date <= ?", from, till);
		try {
            List<TableRow> gRows = rows.toList();
            String[] result = new String[gRows.size()];

            for (int i = 0; i < gRows.size(); i++) {
                TableRow row = gRows.get(i);
                result[i] = row.getStringColumn("import_id");
            }

            return result;
        } finally {
            if (rows != null) {
                rows.close();
            }
        }
	}
	
	public Item getItem(Context context) throws SQLException {
		return Item.find(context, getResourceId());
	}

}
