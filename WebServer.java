
import java.io.*;

public class WebServer {

    public static void main(String[] args) throws IOException {
        Server webServer = new Server();
        webServer.start();
    }
}