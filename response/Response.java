package response;

import request.Request;
import resource.Resource;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Response {
    private Request request;
    private Resource resource;
    private int code;
    public String body;
    private byte[] imageBody;
    private String contentType;
    private int contentLength;

    private String statusLine;
    private String responseHeader;

    private Map<Integer, String> codeToReasonString = new HashMap<>();

    private Date time;

    public Response(Request request, Resource resource, int code, String body, String contentType, int contentLength){

        this.request = request;
        this.resource = resource;
        this.code = code;
        this.body = body;
        this.contentType = contentType;
        this.contentLength = contentLength;

        codeToReasonString.put(404, "Not Found");
        codeToReasonString.put(403, "Forbidden");
        codeToReasonString.put(400, "Bad Request");
        codeToReasonString.put(500, "Internal Server Error");
        codeToReasonString.put(401, "Unauthorized");
        codeToReasonString.put(304, "Not Modified");
        codeToReasonString.put(202, "Accepted");
        codeToReasonString.put(200, "OK");
        codeToReasonString.put(204, "No Content");
        codeToReasonString.put(201, "Created");
        setStatusLine();
        setHeader();
    }
    public Response(Request request, Resource resource, int code, byte[] body, String contentType, int contentLength) {

        this.request = request;
        this.resource = resource;
        this.code = code;
        this.imageBody = body;
        this.contentType = contentType;
        this.contentLength = contentLength;
        setStatusLine();
        setHeader();
    }

    public void send(OutputStream out) throws IOException{
        BufferedOutputStream output = new BufferedOutputStream(out);
        PrintWriter writer = new PrintWriter(output, true);

        //writer.println("HTTP/1.1 200 OK");
        writer.println(this.statusLine);

        //writer.println("WebServer: TEST");
        writer.println(this.responseHeader);
        if (request.getVerb().equals("GET")) {
            writer.println("Last-Modified: " + lastModifiedDate());
        }
        writer.println("Connection: close");
        if (contentLength > 0) {
            writer.println("Content-type: " + contentType);
            writer.println("Content-length: " + contentLength);
        }
        writer.println("");
        if (imageBody != null && imageBody.length > 0) {
            out.write(imageBody);
        } else {
            writer.println(body);
        }
        writer.close();


        out.close();
    }

    public void setStatusLine(){
        String Http_version;
        if(request == null) {
            Http_version = "HTTP/1.1";
        } else {
            Http_version = request.getHttpVersion();
        }
        String reasonPharse = codeToReasonString.get(code);
        this.statusLine = Http_version + " " + code + " " + reasonPharse;

    }
    public void setHeader() {
        Date time = new Date();
        this.responseHeader = "WebSever: " + "WebServer" + '\n' + "Date:" + time;
        this.time = time;
    }
    private Date lastModifiedDate() {
        File destinationFile = resource.getDestinationFile();
        long date = destinationFile.lastModified();
        Date lastModifiedDate = new Date(date);
        return lastModifiedDate;
    }
    public Date getTime() {
        return time;
    }

    public int getCode() {
        return code;
    }

    public int getContentLength() {
        return contentLength;
    }
}