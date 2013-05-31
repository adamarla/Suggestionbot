package gutenberg.collect;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Update extends Task {

    @Override
    protected Path execute(Path file) throws Exception {
        
        String charset = Charset.defaultCharset().toString();
        String signature = file.getFileName().toString();
        String teacherId = file.getParent().getFileName().toString().split("-")[1];
        int pages = 0;
        DirectoryStream<Path> jpegs = Files.newDirectoryStream(file, "*.jpeg");
        for (Path jpeg: jpegs) pages++;
        
        String hostport = System.getProperty("user.name").equals("gutenberg")?
            "www.gradians.com": "localhost:3000";
        URL updateScan = new URL(String.format("http://%s/suggestion", 
            hostport));
        String params = String.format(paramString,
            URLEncoder.encode(teacherId, charset), 
            URLEncoder.encode(signature, charset),
            URLEncoder.encode(String.valueOf(pages), charset));
        conn = (HttpURLConnection)updateScan.openConnection();
        conn.setDoOutput(true); // Triggers HTTP POST
        conn.setRequestProperty("Content-Type", 
            "application/x-www-form-urlencoded;charset=" + charset);
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.getOutputStream().write(params.getBytes(charset));
        conn.getOutputStream().close();
        
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            byte[] goodReturn = "{\"status\":\"ok\"}".getBytes();
            byte[] actualReturn = new byte[goodReturn.length];            
            while (conn.getInputStream().read(actualReturn) != -1) { }            
            conn.getInputStream().close();            
            return Arrays.equals(goodReturn, actualReturn)? file: null;
        } else {
            return null;
        }
    }
        
    private HttpURLConnection conn;
    
    private final String paramString = 
        "teacher_id=%s&signature=%s&num_pages=%s";

}
