package org.dspace.folder;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class ImportFolder extends DSpaceObject {
	
	private static final Logger log = Logger.getLogger(ImportFolder.class);
	private final TableRow myRow;

	ImportFolder(Context context, TableRow row) throws SQLException
    {
        super(context);

        // Ensure that my TableRow is typed.
        if (null == row.getTable())
            row.setTable("folders");

        myRow = row;

        clearDetails();
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
	public String getName() {
		return null;
	}

	@Override
	public void update() throws SQLException, AuthorizeException {
		if (!AuthorizeManager.isAdmin(ourContext)) {
		    throw new AuthorizeException("Вы должны быть администратором чтобы сохранить папку");
		}
		
		DatabaseManager.update(ourContext, myRow);
		log.info(LogManager.getHeader(ourContext, "update_folder", "folder_id=" + getID()));
	}

	@Override
	public void updateLastModified() {
	}
	
	/**
	 * Returns folder by ID
	 * @param context Context
	 * @param id ID
	 * @return Folder
	 * @throws SQLException
	 */
	public static ImportFolder find(Context context, int id) throws SQLException {
		TableRow row = DatabaseManager.find(context, "folders", id);

        if (row == null) {
            return null;
        } else {
            return new ImportFolder(context, row);
        }
	}
	
	/**
	 * Returns all folders
	 * @param context Context
	 * @return Folder array
	 * @throws SQLException
	 */
	public static ImportFolder[] findAll(Context context) throws SQLException {

        // NOTE: The use of 's' in the order by clause can not cause an SQL 
        // injection because the string is derived from constant values above.
        TableRowIterator rows = DatabaseManager.query(context, "select * from folders");

        try {
            List<TableRow> gRows = rows.toList();
            ImportFolder[] folders = new ImportFolder[gRows.size()];

            for (int i = 0; i < gRows.size(); i++) {
                TableRow row = gRows.get(i);
                folders[i] = new ImportFolder(context, row);
            }

            return folders;
        } finally {
            if (rows != null) {
                rows.close();
            }
        }
    }
	
	/**
	 * Creates new folder
	 * @param context Context
	 * @return Folder instance
	 * @throws SQLException
	 * @throws AuthorizeException
	 */
	public static ImportFolder create(Context context) throws SQLException, AuthorizeException {
		if (!AuthorizeManager.isAdmin(context)) {
		    throw new AuthorizeException("Вы должны быть администратором чтобы создать папку");
		}

		// Create a table row
		TableRow row = DatabaseManager.create(context, "folders");
		ImportFolder f = new ImportFolder(context, row);
		
		log.info(LogManager.getHeader(context, "create_folder", "folder_id=" + f.getID()));
		
		return f;
	}
	
	/**
	 * Removes folder
	 * @throws SQLException
	 */
	public void delete() throws SQLException {
		DatabaseManager.delete(ourContext, myRow);
		log.info(LogManager.getHeader(ourContext, "delete_folder", "folder_id=" + getID()));
	}
	
	public Integer getHour() {
		Integer value = myRow.getIntColumn("hour");
		return (value != -1 ? value : null);
	}
	
	public void setHour(Integer hour) {
		if (hour != null) {
			myRow.setColumn("hour", hour);
		} else {
			myRow.setColumnNull("hour");
		}
	}
	
	public Integer getMinute() {
		Integer value = myRow.getIntColumn("minute");
		return (value != -1 ? value : null);
	}
	
	public void setMinute(Integer minute) {
		if (minute != null) {
			myRow.setColumn("minute", minute);
		} else {
			myRow.setColumnNull("minute");
		}
	}
	
	public Integer getDate() {
		Integer value = myRow.getIntColumn("date");
		return (value != -1 ? value : null);
	}
	
	public void setDate(Integer date) {
		if (date != null) {
			myRow.setColumn("date", date);
		} else {
			myRow.setColumnNull("date");
		}
	}
	
	public Integer getMonth() {
		Integer value = myRow.getIntColumn("month");
		return (value != -1 ? value : null);
	}
	
	public void setMonth(Integer month) {
		if (month != null) {
			myRow.setColumn("month", month);
		} else {
			myRow.setColumnNull("month");
		}
	}
	
	public Integer getYear() {
		Integer value = myRow.getIntColumn("year");
		return (value != -1 ? value : null);
	}
	
	public void setYear(Integer year) {
		if (year != null) {
			myRow.setColumn("year", year);
		} else {
			myRow.setColumnNull("year");
		}
	}
	
	public Integer getWeekday() {
		Integer value = myRow.getIntColumn("weekday");
		return (value != -1 ? value : null);
	}
	
	public void setWeekday(Integer weekday) {
		if (weekday != null) {
			myRow.setColumn("weekday", weekday);
		} else {
			myRow.setColumnNull("weekday");
		}
	}
	
	public String getPath() {
		return myRow.getStringColumn("path");
	}
	
	public void setPath(String path) {
		myRow.setColumn("path", path);
	}
	
}
