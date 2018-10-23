package request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Request {
    private String uri;
    private String verb;
    private String httpVersion;
    private Map<String, String> headers;
    private String body;

    public Request(InputStream client) throws Exception{
        headers = new HashMap<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(client));
        try {
            // read the first line to get the request method, URI and HTTP version
            char[] firstChar = new char[1];
            int firstcheck= in.read(firstChar, 0, 1);
            if (firstChar[0] != ' ') {
                StringBuilder sb = new StringBuilder();
                sb.append(firstChar);
                String line = in.readLine();
                sb.append(line);

                parseFirst(sb.toString());
                //read headers

                line = in.readLine();
                while (line != null && line.trim().length() > 0) {
                    parseHeaders(line);
                    line = in.readLine();
                }
                body = "";
                if (headers.containsKey("Content-Length") && Integer.valueOf(headers.get("Content-Length")) > 0) {
                    int contentLengthLeft = Integer.valueOf(headers.get("Content-Length"));
                    char[] bodyBuilder = new char[contentLengthLeft];
                    while (contentLengthLeft > 0) {
                        contentLengthLeft -= in.read(bodyBuilder);
                    }
                    body = new String(bodyBuilder);
                }
            }

        } catch (Exception e) {
        }
    }
    private void parseFirst(String line) {
        String[] parameters = line.split(" ");
        verb = parameters[0];
        uri = parameters[1];
        httpVersion = parameters[2];
    }
    private void parseHeaders(String line) {
        int index = line.indexOf(":");
        String key = line.substring(0, index);
        String value = line.substring(index +  2, line.length());
        headers.put(key, value);
    }




    //accessors:
    public String getUri() {
        return uri;
    }

    public String getVerb() {
        return verb;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
