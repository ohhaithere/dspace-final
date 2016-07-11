package org.dspace.importlog;

import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;

public class ImportLog extends DSpaceObject {

	private static final Logger log = Logger.getLogger(ImportLog.class);
	private final TableRow myRow;

	public ImportLog(Context context, TableRow row) throws SQLException {
		super(context);

		// Ensure that my TableRow is typed.
		if (null == row.getTable())
			row.setTable("import_log");

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
		TableRow row = DatabaseManager.create(context, "import_log");
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
	
	public String getLink() {
		return myRow.getStringColumn("link");
	}
	
	public void setLink(String link) {
		myRow.setColumn("link", link);
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

}
