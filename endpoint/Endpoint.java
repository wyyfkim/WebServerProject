package endpoint;

import request.Request;
import resource.Resource;

import java.io.*;
import java.util.Map;

public class Endpoint {
    private Request request;
    private Resource resource;
    private int contentLength;
    private String contentType;
    public Endpoint(Request request, Resource resource) {
        this.request = request;
        this.resource = resource;
        this.contentLength = 0;
        this.contentType = "html";
    }

//    public String callEndpointByVerb() {
//        String verb = request.getVerb();
//        if (verb.equals("PUT")) {
//            return PUT();
//        } else if (verb.equals("DELETE")) {
//            return DELETE();
//        } else if (verb.equals("POST")) {
//            return POST();
////        }
////        else if (verb.equals("GET")) {
////            return GET();
//        } else if (verb.equals("HEAD")) {
//            return HEAD();
//        }
//        return "error";
//    }

    public String PUT() {
        String newFilePath = resource.absolutePath();
        String newFileParentPath = newFilePath.substring(0, newFilePath.lastIndexOf("/") + 1);
        File logParentFile = new File(newFileParentPath);
        if (!logParentFile.exists()){
            logParentFile.mkdir();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }
        try {
            File logFile = new File (newFilePath);
//            while (parent == null) {
//                parent = logFile.getParentFile();
//            }
            logFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Log file creation error");
        }
        return "<h1> 201 File created successfully </h1>";
    }
    public String DELETE() {
        File targetFile = resource.getDestinationFile();
        if (targetFile.exists()) {
            targetFile.delete();
        }
        return "";
    }

    public String POST() {
        File destinationFile = resource.getDestinationFile();
        StringBuilder repsonseBodyBuilder = new StringBuilder();
        char[] fileBuffer = new char[1024];
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(destinationFile));
            int currentLength = 0;
            while ((currentLength = fileReader.read(fileBuffer)) > 0) {
                contentLength += currentLength;
                repsonseBodyBuilder.append(new String(fileBuffer));
            }
            return repsonseBodyBuilder.toString();
        } catch (Exception e) {
            return "";
        }

//        BufferedReader readerForScript = new BufferedReader(new InputStream(destinationFile));
//        contentLength = readerForScript.read(contentFromScript);
    }

//    public BufferedImage POSTpic() {
    public byte[] POSTpic() {
        File destinationFile = resource.getDestinationFile();
        StringBuilder repsonseBodyBuilder = new StringBuilder();
//        char[] pixels = new char[1024];
        try {
//            BufferedImage imgBuffer = ImageIO.read(destinationFile);
//
//            byte[] pixels = (byte[])imgBuffer.getRaster().getDataElements(0, 0, imgBuffer.getWidth(), imgBuffer.getHeight(), null);

            byte[] pixels = new byte[(int) destinationFile.length()];
            FileInputStream fis = new FileInputStream(destinationFile);
            fis.read(pixels, 0, (int) destinationFile.length());
            fis.close();
            return pixels;
        } catch (Exception e) {
            return new byte[0];
        }

    }
    public String HEAD() {
        return "";
    }

    public String ScriptExecutor(Request request, Resource resource) {
        try {
            java.lang.ProcessBuilder processBuilder = new ProcessBuilder(resource.absolutePath());
            Map<String, String> RequestHeaders = request.getHeaders();
            Map<String, String> environmentVariables = processBuilder.environment();
            environmentVariables.clear();
            for (String key : RequestHeaders.keySet()) {
                environmentVariables.put("HTTP_" + key, RequestHeaders.get(key));
            }
            environmentVariables.put("SERVER_PROTOCOL", request.getHttpVersion());
            environmentVariables.put("QUERY_STRING", request.getBody());
            Process process = null;
            process = processBuilder.start();
            char[] contentFromScript = new char[1024];
            InputStream inputStreamFromScript = process.getInputStream();
            BufferedReader readerForScript = new BufferedReader(new InputStreamReader(inputStreamFromScript));
            contentLength = readerForScript.read(contentFromScript);
            contentType = "html";
            return new String(contentFromScript);
        } catch (Exception e) {
            contentType = "html";
            return "<h1> 500 Script execution error </h1>";
        }
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }
}
