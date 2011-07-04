package com.algebraweb.editor.server.logicalplan.xmlplanloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;

import com.algebraweb.editor.client.node.QueryPlan;
import com.algebraweb.editor.server.logicalplan.QueryPlanBundle;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

public class XMLPlanUploadServlet extends UploadAction{


	private static final long serialVersionUID = 4992498870698384055L;
	Hashtable<String, String> receivedContentTypes = new Hashtable<String, String>();
	Hashtable<String, File> receivedFiles = new Hashtable<String, File>();

	public XMLPlanUploadServlet() {


	}
	
	/**
	 * Save the received file to a temp file, parse it, save it to the session variable
	 */

	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
	
		String response = "";
				
		int cont = 0;
		for (FileItem item : sessionFiles) {
			if (false == item.isFormField()) {
				cont ++;
				try {
			
					//save to temp file
					File file = File.createTempFile("upload-", ".bin");
					item.write(file);
		
					//gwtupload needs this for upload cancelling
					receivedFiles.put(item.getFieldName(), file);
					receivedContentTypes.put(item.getFieldName(), item.getContentType());

					
					//parse the plan, store it into the session...
					HttpSession session = request.getSession(true);
					XMLPlanLoader planLoader = new XMLPlanLoader();
				
					
					QueryPlanBundle sessionBundle = planLoader.parsePlans(file.getAbsolutePath(),this.getServletContext());
					session.setAttribute("queryPlans",sessionBundle);
								
					System.out.println( request.getParameter("myinfo"));
					response = request.getParameter("myinfo");
					response += "!";
					
					Iterator<Integer> it = sessionBundle.getPlans().keySet().iterator();
					
					while (it.hasNext()) {
						response += it.next() + ":";
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					//throw new UploadActionException(e);
				}
			}
		}

		//remove the file, it is already stored in the temp folder
		removeSessionFileItems(request);
		
		//TODO: responses
		return response;
	}






}
