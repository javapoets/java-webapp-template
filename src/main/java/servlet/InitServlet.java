package servlet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class InitServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger();

    public static final String EMPTY = "";
    public static final String EIGHTY = "80";
    public static final String COLON = ":";

    public ServletContext servletContext;

    public Properties properties = null;  // the init properties

    private ConnectionPool connectionPool;  // a database connection pool

    public ConnectionPool getConnectionPool() {
        logger.debug("getConnectionPool()");

        logger.debug("this = "+this);
        logger.debug("this.connectionPool = "+this.connectionPool);

        return this.connectionPool;
    }

    public void init(ServletConfig config) throws ServletException {
        logger.debug("init("+config+")");

        logger.info("InitServlet.class.getName() = "+InitServlet.class.getName());
        logger.info("getClass().getName() = "+getClass().getName());

        this.servletContext = config.getServletContext();
        logger.debug("this.servletContext = " + this.servletContext);

        // a boolean flag to make sure the init() method is only called once for this servlet.
        String contextInitialized = (String)servletContext.getAttribute("contextInitialized");
        logger.debug("contextInitialized = "+contextInitialized);

        if(contextInitialized == null) {

            try {

                InputStream initPropertiesIputStream = servletContext.getResourceAsStream("/WEB-INF/init.properties");

                this.properties = new Properties();

                try {

                    this.properties.load(initPropertiesIputStream);

                    servletContext.setAttribute("properties", this.properties); // Make the properties available in the ServletContext

                } catch(java.io.IOException ioException) {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    ioException.printStackTrace(printWriter);
                    logger.error(stringWriter.toString());
                }

                logger.debug(this.properties);

                String protocol = properties.getProperty("protocol");
                String server = properties.getProperty("server");
                String port = properties.getProperty("port");
                String contextPath = properties.getProperty("context-path");

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(protocol != null ? protocol : "http");
                stringBuilder.append("://");
                stringBuilder.append(server != null ? server : "localhost");
                if(port != null && !port.equals(EMPTY) && !port.equals(EIGHTY)) stringBuilder.append(COLON).append(port);
                if(!EMPTY.equals(port) && !EIGHTY.equals(port)) stringBuilder.append(COLON).append(port);

                String serverUrl = stringBuilder.toString();

                stringBuilder.append(contextPath != null ? contextPath : "/");

                if(contextPath != null && !contextPath.equals(EMPTY)) {
                    stringBuilder.append("/").append(contextPath);
                } else {
                    stringBuilder.append("/");
                }

                String contextUrl = stringBuilder.toString();

                logger.debug("protocol = "+protocol);
                logger.debug("server = "+server);
                logger.debug("port = "+port);
                logger.debug("contextPath = "+contextPath);
                logger.debug("serverUrl = "+serverUrl);
                logger.debug("contextUrl = "+contextUrl);
                
                properties.setProperty("serverUrl", serverUrl);
                properties.setProperty("contextUrl", contextUrl);

                servletContext.setAttribute("serverUrl", serverUrl);
                servletContext.setAttribute("contextUrl", contextUrl);
                servletContext.setAttribute("smtpServer", smtpServer);
                servletContext.setAttribute("smtpPort", smtpPort);
                servletContext.setAttribute("smtpUser", smtpUser);
                servletContext.setAttribute("smtpPass", smtpPass);
                servletContext.setAttribute("emailAddressSupport", emailAddressSupport);
                servletContext.setAttribute("producersRequestEmailTo", producersRequestEmailTo);
                servletContext.setAttribute("producersRequestEmailFrom", producersRequestEmailFrom);
                servletContext.setAttribute("resetPasswordEmailFrom", resetPasswordEmailFrom);
                servletContext.setAttribute("googleSecretKey", googleSecretKey);
                servletContext.setAttribute("googleSiteKey", googleSiteKey);

            } catch(Exception exception) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                exception.printStackTrace(printWriter);
                logger.error(stringWriter.toString());
            }

            servletContext.setAttribute("contextInitialized", "true");

        } // if(contextInitialized != null) {

        super.init(config);
    }

}
