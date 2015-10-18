package web;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SimpleHttpServlet extends HttpServlet {
protected void doGet(HttpServletRequest request,
		             HttpServletResponse response) throws IOException
{
	response.getWriter().write("<html><body>GET response</body></html>");

}
	
}
