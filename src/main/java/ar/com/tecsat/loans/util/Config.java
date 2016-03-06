package ar.com.tecsat.loans.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Config implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
    	event.getServletContext().addServlet("pdfServlet", net.sf.jasperreports.j2ee.servlets.PdfServlet.class).addMapping("/servlets/report/PDF");
    	
        System.setProperty("org.apache.el.parser.COERCE_TO_ZERO", "false");
        System.setProperty("user.timezone", "America/Argentina");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // NOOP
    }
    
    

}