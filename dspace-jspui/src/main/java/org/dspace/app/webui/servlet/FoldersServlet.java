package org.dspace.app.webui.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.dspace.folder.FolderService;
import org.dspace.folder.ImportFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.gson.JsonObject;

/**
 * Created by lalka on 12/23/2015.
 */
public class FoldersServlet extends DSpaceServlet{
	
	@Autowired(required = true)
	private FolderService folderService;
	
    /** Logger */
    private static Logger log = Logger.getLogger(EditProfileServlet.class);
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }
    
    protected void doDSGet(Context context, HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException{

        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        if (action == null) {
        	JSPManager.showJSP(request, response, "/folders/folder-home.jsp");
        } else if (action.equals("list")) {
        	ImportFolder[] folders = ImportFolder.findAll(context);
	        JsonObject result = new JsonObject();
	        for (ImportFolder folder: folders) {
	        	JsonObject item = new JsonObject();
	        	item.addProperty("hour", folder.getHour());
	        	item.addProperty("minute", folder.getMinute());
	        	item.addProperty("date", folder.getDate());
	        	item.addProperty("month", folder.getMonth());
	        	item.addProperty("year", folder.getYear());
	        	item.addProperty("weekday", folder.getWeekday());
	        	item.addProperty("path", folder.getPath());
	        	result.add(Integer.valueOf(folder.getID()).toString(), item);
	        }
	        response.setContentType("application/json");
	        response.getWriter().write(result.toString());
        } else if (action.equals("delete")) {
        	boolean success = false;
        	try {
        		Integer id = Integer.valueOf(request.getParameter("id"));
        		ImportFolder folder = ImportFolder.find(context, id);
        		if (folder != null) {
        			folder.delete();
        			context.complete();
        			success = true;
        			folderService.reloadSchedules();
        		} else {
        			throw new Exception("Folder not exist");
        		}
        	} catch (Exception e) {
        		log.error("Unable to delete folder", e);
        	}
        	
        	JsonObject json = new JsonObject();
    		json.addProperty("success", success);
    		response.setContentType("application/json");
	        response.getWriter().write(json.toString());
        } else if (action.equals("run")) {
        	boolean success = false;
        	try {
        		Integer id = Integer.valueOf(request.getParameter("id"));
        		ImportFolder folder = ImportFolder.find(context, id);
        		if (folder != null) {
        			success = true;
        			folderService.execute(id);
        		} else {
        			throw new Exception("Folder not exist");
        		}
        	} catch (Exception e) {
        		log.error("Unable to delete folder", e);
        	}
        	
        	JsonObject json = new JsonObject();
    		json.addProperty("success", success);
    		response.setContentType("application/json");
	        response.getWriter().write(json.toString());
        }
    }

    protected void doDSPost(Context context, HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException{

        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        boolean success = false;
        String error = null;
        try {
        	String id = request.getParameter("id");
        	String hour = request.getParameter("hour");
        	String minute = request.getParameter("minute");
        	String date = request.getParameter("date");
        	String month = request.getParameter("month");
        	String year = request.getParameter("year");
        	String weekday = request.getParameter("day");
        	String path = request.getParameter("path");
        	
        	//Checking folder exists
        	File pathFile = new File(path);
        	if (!pathFile.exists() || !pathFile.isDirectory() || !pathFile.canRead()) {
        		throw new Exception("Указанный путь не существует или не читается");
        	}
        	
        	ImportFolder folder;
        	if (id == null || id.isEmpty()) {
        		folder = ImportFolder.create(context);
        	} else {
        		folder = ImportFolder.find(context, Integer.valueOf(id));
        		if (folder == null)
        			throw new Exception("Folder not found");
        	}

        	if (hour != null && !hour.isEmpty()) {
        		folder.setHour(Integer.valueOf(hour));
        	} else {
        		folder.setHour(null);
        	}
        	if (minute != null && !minute.isEmpty()) {
        		folder.setMinute(Integer.valueOf(minute));
        	} else {
        		folder.setMinute(null);
        	}
        	if (date != null && !date.isEmpty()) {
        		folder.setDate(Integer.valueOf(date));
        	} else {
        		folder.setDate(null);
        	}
        	if (month != null && !month.isEmpty()) {
        		folder.setMonth(Integer.valueOf(month));
        	} else {
        		folder.setMonth(null);
        	}
        	if (year != null && !year.isEmpty()) {
        		folder.setYear(Integer.valueOf(year));
        	} else {
        		folder.setYear(null);
        	}
        	if (weekday != null && !weekday.isEmpty()) {
        		folder.setWeekday(Integer.valueOf(weekday));
        	} else {
        		folder.setWeekday(null);
        	}
        	folder.setPath(path);
        	folder.update();
        	context.complete();
        	success = true;
        	folderService.reloadSchedules();
        } catch (Exception e) {
        	log.error("Folder save failed", e);
        	error = e.getMessage();
        }
        
        JsonObject json = new JsonObject();
        json.addProperty("success", success);
        if (!success) {
        	json.addProperty("error", error);
        }
        response.setContentType("application/json");
        response.getWriter().write(json.toString());
    }
}