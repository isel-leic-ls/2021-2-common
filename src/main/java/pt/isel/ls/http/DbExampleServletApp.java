package pt.isel.ls.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class DbExampleServletApp {

    private static final Logger log = LoggerFactory.getLogger(DbExampleServletApp.class);

    /*
     * TCP port where to listen.
     * Standard port for HTTP is 80 but might be already in use
     */
    private static final int LISTEN_PORT = 8080;

    public static void main(String[] args) throws Exception {

        log.info("main started");

        String portDef = System.getenv("PORT");
        int port = portDef != null ? Integer.parseInt(portDef) : LISTEN_PORT;
        log.info("configured listening port is {}", port);

        String dbUrl = System.getenv("JDBC_DATABASE_URL");

        if (dbUrl == null) {
            log.error("JDBC_DATABASE_URL is not defined, ending.");
            return;
        }

        try {

            PGSimpleDataSource ds = new PGSimpleDataSource();
            ds.setUrl(dbUrl);

            Server server = new Server(port);
            ServletHandler handler = new ServletHandler();
            DbExampleServlet servlet = new DbExampleServlet(ds);

            handler.addServletWithMapping(new ServletHolder(servlet), "/*");
            log.info("registered {} on all paths", servlet);

            server.setHandler(handler);
            server.start();

            log.info("server started listening on port {}", port);
            server.join();
        } catch (IOException e) {
            log.error("Error while starting app, unable to revover", e);
        }
    }
}
