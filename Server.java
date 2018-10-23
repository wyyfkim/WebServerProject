
import configuration.HttpdConf;
import configuration.MimeTypes;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class Server {
    HttpdConf configuration;
    MimeTypes mimeTypes;
    ServerSocket socket;
    Map<String, File> accessFiles;
    Thread currentThread;
    public Server() {
    }
    public void start() {

        HttpdConf configuration = null;
        MimeTypes mimeTypes = null;
        File logFile = null;
        Worker newWorker = null;
        try {
            synchronized(this){
                this.currentThread = Thread.currentThread();
            }
            ServerStart();
            configuration = readInConfigurationFile();
            mimeTypes = readInMimeTypesFile();
            logFile = createLogFile(configuration);
            while (true) {
                Socket client = socket.accept();
                newWorker = new Worker(client, mimeTypes, configuration, logFile);
//                newWorker = new Worker(client, mimeTypes, configuration);
                Thread newThread = new Thread(newWorker);
                newThread.start();
            }
        } catch (Exception e) {
            System.out.println("Server start error");
            System.exit(1);
        }
    }
    private void ServerStart() throws IOException {
        final int DEFAULT_PORT = 8096;
        try {
            socket = new ServerSocket( DEFAULT_PORT );
            System.out.println("Opened socket " + DEFAULT_PORT);
        } catch (IOException e) {
            System.out.println("Error opening socket");
            throw new IOException();
        }
    }
    private HttpdConf readInConfigurationFile() throws IOException {
        try {
            URL url = WebServer.class.getResource("conf/httpd.conf");
            File file = new File(url.getPath());
            return new HttpdConf(file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error Reading configuration file");
            throw new IOException();
        }
    }
    private MimeTypes readInMimeTypesFile() throws IOException {
        try {
            URL url = WebServer.class.getResource("conf/mime.types");
            File file = new File(url.getPath());
            return new MimeTypes(file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error Reading configuration file");
            throw new IOException();
        }
    }
    private File createLogFile(HttpdConf configuration) {
        String logFileName = configuration.getLogFile();
        String logParentName = logFileName.substring(0, logFileName.lastIndexOf("/") + 1);
        File logParentFile = new File(logParentName);
        if (!logParentFile.exists()){
            logParentFile.mkdir();
        }
        try {
            File logFile = new File (logFileName);
            logFile.createNewFile();
            return logFile;
        } catch (IOException e) {
            System.out.println("Log file creation error");
            return null;
        }
    }


}
