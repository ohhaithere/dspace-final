package org.dspace.folder;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.dspace.content.Collection;
import org.dspace.core.Context;
import org.dspace.util.MfuaXmlParser;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.ibm.icu.util.Calendar;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;

public class FolderServiceImpl implements FolderService {
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger(FolderServiceImpl.class);
	private Context context;
	private Scheduler scheduler = new Scheduler();
	private Map<Integer, String> schedules = new HashMap<Integer,String>();
	private MfuaXmlParser parser = new MfuaXmlParser();

	@Override
	public void init() throws SQLException {
		logger.info("Initializing folder service");
		context = new Context();
		scheduler.setDaemon(true);
		scheduler.start();
		
		reloadSchedules();
	}
	
	@Override
	public void reloadSchedules() throws SQLException {
		logger.info("Reloading schedules");
		
		//Cleaning schedules
		for (String id: schedules.values()) {
			scheduler.deschedule(id);
		}
		schedules.clear();
		
		ImportFolder[] folders = ImportFolder.findAll(context);
		for (ImportFolder folder: folders) {
			//Creating schedule pattern
			StringBuilder pattern = new StringBuilder();
			if (folder.getMinute() != null) {
				pattern.append(folder.getMinute());
			} else {
				pattern.append("*");
			}
			pattern.append(" ");
			if (folder.getHour() != null) {
				pattern.append(folder.getHour());
			} else {
				pattern.append("*");
			}
			pattern.append(" ");
			if (folder.getDate() != null) {
				pattern.append(folder.getDate());
			} else {
				pattern.append("*");
			}
			pattern.append(" ");
			if (folder.getMonth() != null) {
				pattern.append(folder.getMonth());
			} else {
				pattern.append("*");
			}
			pattern.append(" ");
			if (folder.getWeekday() != null) {
				pattern.append(folder.getWeekday());
			} else {
				pattern.append("*");
			}
			pattern.append(" ");
			
			ImportTask task = new ImportTask(folder.getPath(), folder.getYear());
			String scheduleId = scheduler.schedule(pattern.toString(), task);
			schedules.put(folder.getID(), scheduleId);
			Log.info("New import task " + scheduleId);
		}
	}
	
	@Override
	public void execute(int id) {
		ImportTask task = (ImportTask) scheduler.getTask(schedules.get(id));
		ImportTask manualTask = new ImportTask(task.getPath(), null);
		scheduler.launch(manualTask);
	}
	
	class ImportTask extends Task {
		
		private String path;
		private Integer year;
		
		public ImportTask(String path, Integer year) {
			this.path = path;
			this.year = year;
		}
		
		public String getPath() {
			return path;
		}

		@Override
		public void execute(TaskExecutionContext context) throws RuntimeException {
			logger.info("Executing import task for path " + path);
			//Checking year
			if (year != null) {
				Calendar cal = Calendar.getInstance();
				if (cal.get(Calendar.YEAR) != year)
					return;
			}
			
			parseDir(new File(path));
		}
		
		private void parseDir(File dir) {
			File[] list = dir.listFiles();
			for (File item: list) {
				if (item.isDirectory()) {
					parseDir(item);
				} else if (FilenameUtils.getExtension(item.getName()).equalsIgnoreCase("xml")) {
					try {
						DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
						f.setValidating(false);
						DocumentBuilder builder = f.newDocumentBuilder();
						Document doc = builder.parse(item);
						parser.createItems(doc, context, Collection.find(context, 1));
					} catch (Exception e) {
						logger.warn("Can't parse XML", e);
					} finally {
						try {
							context.getDBConnection().commit();
						} catch (Exception e) {
							logger.warn("Can't import XML", e);
						}
					}
				}
			}
		}
		
	}
	
}
