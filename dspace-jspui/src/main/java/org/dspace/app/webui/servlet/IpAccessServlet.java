package org.dspace.app.webui.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.authorize.IpAccess;
import org.dspace.content.DSpaceObject;
import org.dspace.content.MetadataValue;
import org.dspace.core.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class IpAccessServlet extends DSpaceServlet {

	@Override
	protected void doDSGet(Context context, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, AuthorizeException, SQLException, IOException {
		
		response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        if (!AuthorizeManager.isAdmin(context)) {
		    throw new AuthorizeException("Вы должны быть администратором, чтобы управлять IP доступом");
		}
        
        Integer resourceId = null;
        DSpaceObject resource = null;
        //Getting resource ID from request
    	try {
    		resourceId = Integer.valueOf(request.getParameter("rid"));
    	} catch (Exception e) {}
        
    	//Trying to get resource
        if (resourceId != null) {
        	resource = (DSpaceObject) MetadataValue.findResource(context, resourceId);
        	if (resource != null) {
        		request.setAttribute("resource", resource);
        	} else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        	}
        }
        
        JSPManager.showJSP(request, response, "/ipacl/home.jsp");
	}
	
	@Override
	protected void doDSPost(Context context, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, AuthorizeException, SQLException, IOException {
		
		response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
		
        if (!AuthorizeManager.isAdmin(context)) {
		    throw new AuthorizeException("Вы должны быть администратором, чтобы управлять папками для импорта");
		}
        
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        
        JsonObject json = new JsonObject();
        
        switch (action) {
        	case "add":
        		String ip = request.getParameter("ip");
        		String ip2 = request.getParameter("ip2");
        		String type = request.getParameter("type");
        		String ipStr = ip;
        		if (ip2 != null)
        			ipStr += "-" + ip2;
        		IpAccess rule = IpAccess.create(context);
        		rule.setAccessType(Integer.valueOf(type));
        		rule.setIp(ipStr);
        		if (id != null)
        			rule.setResourceId(Integer.valueOf(id));
        		rule.update();
        		json.addProperty("success", true);
        		break;
        		
        	case "delete":
        		IpAccess ipRule = IpAccess.find(context, Integer.valueOf(id));
        		if (ipRule != null) {
        			ipRule.delete();
        		}
        		json.addProperty("success", true);
        		break;
        		
        	default:
        		Integer resourceId = null;
        		if (id != null) {
        			try {
        				resourceId = Integer.valueOf(id);
        			} catch (NumberFormatException e) {}
        		}
        		IpAccess[] rules = IpAccess.findByResourceId(context, resourceId);
        		JsonArray jsonRules = new JsonArray();
        		for (IpAccess item: rules) {
        			JsonObject jsonRule = new JsonObject();
        			jsonRule.addProperty("id", item.getID());
        			jsonRule.addProperty("type", item.getAccessType() == IpAccess.BLACK ? "black" : "white");
        			jsonRule.addProperty("ip", item.getIp());
        			jsonRules.add(jsonRule);
        		}
        		json.add("rules", jsonRules);
        		break;
        }
        
        context.complete();
        
        response.setContentType("application/json");
        response.getWriter().write(json.toString());
	}

}
