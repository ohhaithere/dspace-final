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
	
	public Integer getResourceTypeId() {
		return myRow.getIntColumn("resource_type_id");
	}
	
	public void setResourceTypeId(Integer resourceTypeId) {
		myRow.setColumn("resource_type_id", resourceTypeId);
	}
	
	public Integer getAccessType() {
		return myRow.getIntColumn("type");
	}
	
	public void setAccessType(Integer type) {
		myRow.setColumn("type", type);
	}
	
	public String getIp() {
		return myRow.getStringColumn("ip");
	}
	
	public void setIp(String ip) {
		myRow.setColumn("ip", ip);
	}
	
	public static IpAccess[] findByResourceId(Context context, Integer resourceId) throws SQLException {
		TableRowIterator rows = DatabaseManager.query(context, "select * from ip_access WHERE resource_id = ?", resourceId);
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
