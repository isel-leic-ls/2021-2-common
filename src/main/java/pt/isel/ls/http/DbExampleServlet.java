package pt.isel.ls.http;

import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbExampleServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DbExampleServlet.class);
    private final PGSimpleDataSource ds;

    public DbExampleServlet(PGSimpleDataSource ds) {
        this.ds = ds;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        log.info("incoming request: method={}, uri={}, accept={}",
                req.getMethod(),
                req.getRequestURI(),
                req.getHeader("Accept"));

        Charset utf8 = StandardCharsets.UTF_8;
        resp.setContentType(String.format("text/plain; charset=%s", utf8.name()));
        String respBody;
        try {
            respBody = query();
            resp.setStatus(200);
        } catch (SQLException e) {
            log.error("Error while doing query", e);
            respBody = "Sorry, it's me, not you";
            resp.setStatus(500);
        }
        byte[] respBodyBytes = respBody.getBytes(utf8);
        resp.setContentLength(respBodyBytes.length);
        OutputStream os = resp.getOutputStream();
        os.write(respBodyBytes);
        os.flush();
        log.info("outgoing response: method={}, uri={}, status={}, Content-Type={}",
                req.getMethod(),
                req.getRequestURI(),
                resp.getStatus(),
                resp.getHeader("Content-Type"));
    }

    private String query() throws SQLException {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("select number, name from students");
            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                int number = rs.getInt(1);
                String name = rs.getString(2);
                sb.append(String.format("%s - %s\n", number, name));
            }
            return sb.toString();
        }
    }
}
