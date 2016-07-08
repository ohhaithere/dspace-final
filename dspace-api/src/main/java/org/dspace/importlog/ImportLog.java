package org.dspace.importlog;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLastModified() {
	}

}
