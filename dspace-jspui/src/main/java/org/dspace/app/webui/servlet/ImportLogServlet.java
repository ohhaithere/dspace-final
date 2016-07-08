package org.dspace.app.webui.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;

public class ImportLogServlet extends DSpaceServlet {
	
	/** Logger */
    private static Logger log = Logger.getLogger(EditProfileServlet.class);

	@Override
	protected void doDSGet(Context context, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException, AuthorizeException {
		
		//TODO Show import log
	}

}
