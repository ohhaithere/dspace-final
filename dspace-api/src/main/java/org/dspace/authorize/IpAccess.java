package org.dspace.authorize;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class IpAccess extends DSpaceObject {
	
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	
	private static final Logger log = Logger.getLogger(IpAccess.class);
	private final TableRow myRow;
	private static final String tableName = "ip_access";
	
	public IpAccess(Context context, TableRow row) throws SQLException {
		super(context);

		// Ensure that my TableRow is typed.
		if (null == row.getTable())
			row.setTable(tableName);

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
		DatabaseManager.update(ourContext, myRow);
		log.info(LogManager.getHeader(ourContext, "update_ip_access", "record_id=" + getID()));
	}

	@Override
	public void updateLastModified() {}
	
	public Integer getResourceId() {
		return myRow.getIntColumn("resource_id");
	}
	
	public void setResourceId(Integer resourceId) {
		myRow.setColumn("resource_id", resourceId);
	}
	
	public Integer getAccessType() {
		return myRow.getIntColumn("access_type");
	}
	
	public void setAccessType(Integer type) {
		myRow.setColumn("access_type", type);
	}
	
	public String getIp() {
		return myRow.getStringColumn("ip");
	}
	
	public void setIp(String ip) {
		myRow.setColumn("ip", ip);
	}
	
	public void delete() throws SQLException {
		DatabaseManager.delete(ourContext, myRow);
		log.info(LogManager.getHeader(ourContext, "delete_ip_access", "rule_id=" + getID()));
	}
	
	public static IpAccess create(Context context) throws SQLException, AuthorizeException {
		if (!AuthorizeManager.isAdmin(context)) {
		    throw new AuthorizeException("Вы должны быть администратором чтобы создать правило IP доступа");
		}

		// Create a table row
		TableRow row = DatabaseManager.create(context, "ip_access");
		IpAccess f = new IpAccess(context, row);
		
		log.info(LogManager.getHeader(context, "ip_access", "rule_id=" + f.getID()));
		
		return f;
	}
	
	/**
	 * Returns folder by ID
	 * @param context Context
	 * @param id ID
	 * @return Folder
	 * @throws SQLException
	 */
	public static IpAccess find(Context context, int id) throws SQLException {
		TableRow row = DatabaseManager.find(context, "ip_access", id);

        if (row == null) {
            return null;
        } else {
            return new IpAccess(context, row);
        }
	}
	
	public static IpAccess[] findAll(Context context) throws SQLException {

        // NOTE: The use of 's' in the order by clause can not cause an SQL 
        // injection because the string is derived from constant values above.
        TableRowIterator rows = DatabaseManager.query(context, "select * from ip_access");

        try {
            List<TableRow> gRows = rows.toList();
            IpAccess[] rules = new IpAccess[gRows.size()];

            for (int i = 0; i < gRows.size(); i++) {
                TableRow row = gRows.get(i);
                rules[i] = new IpAccess(context, row);
            }

            return rules;
        } finally {
            if (rows != null) {
                rows.close();
            }
        }
    }
	
	public static IpAccess[] findByResourceId(Context context, Integer resourceId) throws SQLException {
		TableRowIterator rows;
		if (resourceId != null) {
			rows = DatabaseManager.query(context, "select * from ip_access WHERE resource_id = ?", resourceId);
		} else {
			rows = DatabaseManager.query(context, "select * from ip_access WHERE resource_id IS NULL");
		}
		try {
            List<TableRow> gRows = rows.toList();
            IpAccess[] rules = new IpAccess[gRows.size()];

            for (int i = 0; i < gRows.size(); i++) {
                TableRow row = gRows.get(i);
                rules[i] = new IpAccess(context, row);
            }

            return rules;
        } finally {
            if (rows != null) {
                rows.close();
            }
        }
	}

}
