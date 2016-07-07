package org.dspace.folder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolderServiceImpl implements FolderService {
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger(FolderServiceImpl.class);

	FolderServiceImpl() {
		logger.info("Creating folder service");
	}
	
	@Override
	public void reloadSchedules() {
		// TODO Auto-generated method stub
		
	}
	
}
