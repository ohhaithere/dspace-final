package org.dspace.app.webui.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.importlog.ImportErrorLog;
import org.dspace.importlog.ImportLog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ibm.icu.util.Calendar;

public class ImportLogServlet extends DSpaceServlet {

	@Override
	protected void doDSGet(Context context, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException, AuthorizeException {
		
		response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
		
		String dateStr = request.getParameter("date");
		Date date;
		try {
			if (dateStr != null) {
				DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
				date = df.parse(dateStr);
			} else {
				throw new Exception("Date parse failed");
			}
		} catch (Exception e) {
			date = Calendar.getInstance().getTime();
		}
		request.setAttribute("date", date);
		
		String area = request.getParameter("area");
		String action = request.getParameter("action");
		if (area != null && area.equals("errors")) {
			if (action == null) {
				JSPManager.showJSP(request, response, "/importlog/errors.jsp");
			} else {
				String id = request.getParameter("id");
				Integer page = request.getParameter("page") != null ? Integer.valueOf(request.getParameter("page")) : 1;
				JsonObject json = new JsonObject();
				JsonArray items = new JsonArray();
				String[] ids = ImportErrorLog.getReportsId(context, date);
				if (id == null && ids.length > 0) {
					id = ids[0];
				}
				for (int i = 0; i < ids.length; i++) {
					if (ids[i].equals(id)) {
						String prevId = null;
						String nextId = null;
						if (i > 0) {
							prevId = ids[i - 1];
						}
						if (i < (ids.length - 1)) {
							nextId = ids[i + 1];
						}
						json.addProperty("prevId", prevId);
						json.addProperty("nextId", nextId);
					}
				}
				
				if (id != null) {
					int limit = 100;
					int offset = (page - 1) * limit;
					ImportErrorLog[] report = ImportErrorLog.getReport(context, id, offset, limit);
					for (ImportErrorLog record: report) {
						items.add(new JsonPrimitive(record.getFile()));
					}
					json.addProperty("page", page);
					
					if (offset == 0) {
						int count = ImportErrorLog.getReportLength(context, id);
						json.addProperty("count", count);
						json.addProperty("pages", Math.ceil(Integer.valueOf(count).doubleValue() / Integer.valueOf(limit).doubleValue()));
						DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
						json.addProperty("firstDate", df.format(report[0].getDate()));
					}
				}
				json.add("items", items);
				json.addProperty("id", id);
				response.setContentType("application/json");
				response.getWriter().write(json.toString());
			}
		} else {
			if (action == null) {
				JSPManager.showJSP(request, response, "/importlog/log.jsp");
			} else {
				String id = request.getParameter("id");
				Integer page = request.getParameter("page") != null ? Integer.valueOf(request.getParameter("page")) : 1;
				JsonObject json = new JsonObject();
				JsonArray items = new JsonArray();
				String[] ids = ImportLog.getReportsId(context, date);
				if (id == null && ids.length > 0) {
					id = ids[0];
				}
				for (int i = 0; i < ids.length; i++) {
					if (ids[i].equals(id)) {
						String prevId = null;
						String nextId = null;
						if (i > 0) {
							prevId = ids[i - 1];
						}
						if (i < (ids.length - 1)) {
							nextId = ids[i + 1];
						}
						json.addProperty("prevId", prevId);
						json.addProperty("nextId", nextId);
					}
				}
				if (id != null) {
					int limit = 100;
					int offset = (page - 1) * limit;
					ImportLog[] report = ImportLog.getReport(context, id, offset, limit);
					for (ImportLog record: report) {
						JsonObject recordJson = new JsonObject();
						recordJson.addProperty("year", record.getYear());
						recordJson.addProperty("name", record.getName());
						recordJson.addProperty("authors", record.getAuthors());
						recordJson.addProperty("link", request.getContextPath() + "/handle/" + record.getLink());
						recordJson.addProperty("duplicate", record.getDuplicate());
						items.add(recordJson);
					}
					json.addProperty("page", page);
					
					if (offset == 0) {
						int count = ImportLog.getReportLength(context, id);
						json.addProperty("count", count);
						json.addProperty("pages", Math.ceil(Integer.valueOf(count).doubleValue() / Integer.valueOf(limit).doubleValue()));
						DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
						json.addProperty("firstDate", df.format(report[0].getDate()));
					}
				}
				json.add("items", items);
				json.addProperty("id", id);
				response.setContentType("application/json");
		        response.getWriter().write(json.toString());
			}
		}
	}

}