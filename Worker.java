
import configuration.Htaccess;
import configuration.HttpdConf;
import configuration.MimeTypes;
import endpoint.Endpoint;
//import server.processBuilder.ScriptExecutor;
import request.Request;
import resource.Resource;
import response.Response;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Worker implements Runnable {
    private Socket client;
    private MimeTypes mimeTypes;
    private HttpdConf configuration;
    private File log;
    private String id;

    public Worker(Socket client, MimeTypes mimes, HttpdConf config, File log)  {
//    public Worker(Socket client, MimeTypes mimes, HttpdConf config)  {
        this.client = client;
        this.mimeTypes = mimes;
        this.configuration = config;
//        this.log = log;
        this.id = "-";
    }

    public void run() {
        try {
            InputStream inputStream = client.getInputStream();
            Request request = null;
            Resource resource = null;
            Response response = null;
            OutputStream outputStream = client.getOutputStream();
            boolean requestOK = false;
            try {
                request = new Request(inputStream);
            } catch (Exception e) {
                String responseStr = "<h1> Bad Request, Can not parse request <h1>";
                response = new Response(null, null, 400, responseStr, "text/html", responseStr.length());
//                System.out.println("Error parsing request");
            }
            try {
                resource = new Resource(request.getUri(), configuration, request.getVerb());
                requestOK = true;
            } catch (Exception e) {
                String responseStr = "<h1> Bad Request <h1>";
                response = new Response(null, null, 400, responseStr, "text/html", responseStr.length());
//                System.out.println("Error creating resource");
            }
            boolean hasAccess = true;
            if (requestOK && resource.isProtected()) {
                Htaccess htaccess = new Htaccess(resource.getAccessFile().getAbsolutePath());
                boolean hasAuthHead = true;
                if (requestOK && !request.getHeaders().containsKey("Authorization")) {
                    hasAuthHead = false;
                    hasAccess = false;
                    String responseStr = "<h1> 401: No authorization header <h1>";
                    response = new Response(request, resource, 401, responseStr, "text/html", responseStr.length());
//                    System.out.println("No authorization header");
                }
                if (hasAuthHead) {
                    setId(htaccess.getId());
                }
                if (hasAuthHead && requestOK && !htaccess.isAuthorized(request.getHeaders().get("Authorization").split(" ")[1])) {
                    String responseStr = "<h1> 403: Forbidden <h1>";
                    response = new Response(request, resource, 403, responseStr, "text/html", responseStr.length());
//                    System.out.println("Invalid username or password");
                }
            }
            boolean fileExists = true;
            try {
                if (hasAccess && requestOK && !request.getVerb().equals("PUT") && !request.getVerb().equals("DELETE") && !resource.getDestinationFile().exists()) {
                    fileExists = false;
                    String responseStr = "<h1> 404: File Not Found <h1>";
                    response = new Response(request, resource, 404, responseStr, "text/html", responseStr.length());
//                System.out.println("File doesn't exist");
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            if (requestOK && hasAccess && fileExists) {
                Endpoint endpoints = new Endpoint(request, resource);
                if (!resource.isScript()) {
                    String verb = request.getVerb();
                    String responseBody = "";
                    byte[] responseBodyImage = new byte[]{};
                    String contentType = mimeTypes.lookup(resource.getFileType());
                    if (verb.equals("PUT")) {
                        responseBody = endpoints.PUT();
                        response = new Response(request, resource, 201, responseBody, "text/html", responseBody.length());
                    } else if (verb.equals("DELETE")) {
                        responseBody = endpoints.DELETE();
                        response = new Response(request, resource, 204, "", "text/html", responseBody.length());
                    } else if (verb.equals("POST")) {
                        if (contentType.startsWith("image")) {
                            responseBodyImage = endpoints.POSTpic();
                            response = new Response(request, resource, 200, responseBodyImage, contentType, endpoints.getContentLength());
                        } else {
                            responseBody = endpoints.POST();
                            response = new Response(request, resource, 200, responseBody, contentType, endpoints.getContentLength());
                        }
                    } else if (verb.equals("GET")) {
                        if (request.getHeaders().containsKey("If-Modified-Since")) {
                            responseBody = "";
                        } else if (contentType.startsWith("image")) {
                            responseBodyImage = endpoints.POSTpic();
                        } else {
                            responseBody = endpoints.POST();
                        }
                        if (responseBody.length() > 0) {
                            response = new Response(request, resource, 200, responseBody, contentType, endpoints.getContentLength());
                        } else if (responseBodyImage.length > 0) {
                            response = new Response(request, resource, 200, responseBodyImage, contentType, endpoints.getContentLength());
                        } else {
                            response = new Response(request, resource, 304, responseBody, "text/html", endpoints.getContentLength());
                        }
                    } else if (verb.equals("HEAD")) {
                        response = new Response(request, resource, 200, "", "", 0);
                    }
                } else {
                    try {
                        String responseBody = endpoints.ScriptExecutor(request, resource);
                        response = new Response(request, resource, 200, responseBody, "text/html", endpoints.getContentLength());
                    } catch (Exception e) {
                        response = new Response(request, resource, 500, "", "text/html", endpoints.getContentLength());
                    }
                }
            }
            response.send(outputStream);
            inputStream.close();
            outputStream.flush();
            client.close();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private void setId(String id) {
        this.id = id;
    }
}
