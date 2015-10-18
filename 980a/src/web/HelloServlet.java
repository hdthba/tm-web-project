package web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class HelloServlet extends HttpServlet {

    /**
     * Constructor of the object.
     */
    public HelloServlet() {
            super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
            super.destroy(); // Just puts "destroy" string in log
            // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

            doPost(request, response);
    }

    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to post.
     * 
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {

            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();
        	String name = req.getParameter("name");
        	String[] contacts = req.getParameterValues("contact");
        	
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            out.println("<HTML>");
            out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
            out.println("  <BODY>");
        	out.println( "<h1>Hello,"+name+"</h1>");
        	if(contacts!=null)
        	{
        		out.println(" Contact Information:");
        		for(String info:contacts)
        		{
        			out.println(info+"<br>");
        		}
        	}       
            out.println("  </BODY>");
            out.println("</HTML>");
         //   out.flush();
            out.close();
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
            // Put your code here
    	
    	
    }

}


