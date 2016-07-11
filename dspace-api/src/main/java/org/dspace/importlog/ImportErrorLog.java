package org.dspace.importlog;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.folder.ImportFolder;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;

public class ImportErrorLog extends DSpaceObject {

	private static final Logger log = Logger.getLogger(ImportErrorLog.class);
	private final TableRow myRow;

	public ImportErrorLog(Context context, TableRow row) throws SQLException {
		super(context);

		// Ensure that my TableRow is typed.
		if (null == row.getTable())
			row.setTable("import_error_log");

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
	public static ImportErrorLog create(Context context, String importId) throws SQLException {
		// Create a table row
		TableRow row = DatabaseManager.create(context, "import_error_log");
		ImportErrorLog f = new ImportErrorLog(context, row);
		f.setDate(new Date());
		f.setImportId(importId);
		
		log.info(LogManager.getHeader(context, "create_import_error_log_record", "record_id=" + f.getID()));
		
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
		log.info(LogManager.getHeader(ourContext, "update_import_error_log", "record_id=" + getID()));

	}

	@Override
	public void updateLastModified() {
	}
	
	public Date getDate() {
		return myRow.getDateColumn("date");
	}
	
	public void setDate(Date date) {
		myRow.setColumn("date", date);
	}
	
	public String getFile() {
		return myRow.getStringColumn("file");
	}
	
	public void setFile(String file) {
		myRow.setColumn("file", file);
	}

	@Override
	public String getName() {
		return null;
	}
	
	public String getImportId() {
		return myRow.getStringColumn("import_id");
	}
	
	public void setImportId(String importId) {
		myRow.setColumn("import_id", importId);
	}

}
