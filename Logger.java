import request.Request;
import response.Response;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Logger {
    private File logFile;
    public Logger(File fileName) {
        this.logFile = fileName;
    }
    public void write(Request request, Response response, String clientRemoteIP, String id) {

        String IPaddress = clientRemoteIP;
        String userID = id;
        String finishedTime = "-";
        String firstLine = "-";
        String code = "-";
        String contentLength = "-";
        if (request != null && response != null) {
            DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
            finishedTime = formatter.format(response.getTime());
            firstLine = " '" + request.getVerb() + " " + request.getUri() + " " + request.getHttpVersion() + "' ";
            code = response.getCode() + "";
            if (response.getContentLength() > 0) {
                contentLength = response.getContentLength() + "";
            }
        }
        String logString = IPaddress + " - " + userID + " [" + finishedTime + "] " + firstLine + " " + code + " " + contentLength + " ";
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(logFile));
            fileWriter.write(logString);
            fileWriter.newLine();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
