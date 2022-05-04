package com.shopme.admin;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

public class AbstractExporter {

	public void setResponseHeader(HttpServletResponse response, String cententType, String extension, String prefix) throws IOException {
		DateFormat dateFormatter = new SimpleDateFormat("yyy-MM-dd_HH-mm-ss");
		String timeStamp = dateFormatter.format(new Date());
		String fileName = prefix + timeStamp + extension;
		response.setContentType(cententType);
		response.setHeader("Content-Disposition", "attachment; fileName=" + fileName);
	}
}
