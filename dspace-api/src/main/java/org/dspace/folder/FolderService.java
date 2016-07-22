package org.dspace.folder;

import java.sql.SQLException;

public interface FolderService {
	
	/**
	 * Init service
	 * @throws SQLException
	 */
	public void init() throws SQLException;
	
	/**
	 * Reloads schedules
	 * @throws SQLException 
	 */
	public void reloadSchedules() throws SQLException;
	
	/**
	 * Starts task execution
	 * @param id ID
	 */
	public void execute(int id);
	
}
