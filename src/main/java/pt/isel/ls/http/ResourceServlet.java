package pt.isel.ls.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ResourceServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ResourceServlet.class);
    private static final String RESOURCE_PATH = "example.html";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        log.info("incoming request: method={}, uri={}, query-string={}, accept={}",
                req.getMethod(),
                req.getRequestURI(),
                req.getQueryString(),
                req.getHeader("Accept"));

        log.info("url = {}", ClassLoader.getSystemResource(RESOURCE_PATH));

        try (InputStream fis = ClassLoader.getSystemResourceAsStream(RESOURCE_PATH)) {
            if (fis == null) {
                log.error("Unable to open resource located at {}", RESOURCE_PATH);
                resp.setStatus(500);
                return;
            }
            Charset utf8 = StandardCharsets.UTF_8;
            resp.setContentType(String.format("text/html; charset=%s", utf8.name()));
            byte[] buf = new byte[1024];
            int len;
            OutputStream ros = resp.getOutputStream();
            while ((len = fis.read(buf)) != -1) {
                ros.write(buf, 0, len);
            }
            ros.flush();
        }
        log.info("outgoing response: method={}, uri={}, status={}, Content-Type={}",
                req.getMethod(),
                req.getRequestURI(),
                resp.getStatus(),
                resp.getHeader("Content-Type"));
    }
}
