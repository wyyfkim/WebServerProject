package configuration;

import java.io.IOException;
import java.util.*;

public class HttpdConf extends ConfigurationReader {
    private HashMap<String, String> aliases;
    private HashMap<String, String> scriptAliases;
    private String serverRoot;
    private String documentRoot;
    private String listen;
    private String logFile;
    private String accessFileName;

    public HttpdConf(String fileName) throws IOException{
        super(fileName);
        this.load();
        aliases = new HashMap<>();
        scriptAliases = new HashMap<>();
        List<String> aliasesList = map.get("Alias");
        List<String> scriptAliasesList = map.get("ScriptAlias");
        parseAliases(aliasesList);
        parsescriptAliasesList(scriptAliasesList);

        serverRoot = map.get("ServerRoot").get(0).substring(1, map.get("ServerRoot").get(0).length() - 1);
        documentRoot = map.get("DocumentRoot").get(0).substring(1, map.get("DocumentRoot").get(0).length() - 1);
        listen = map.get("Listen").get(0);
        logFile = map.get("LogFile").get(0).substring(1, map.get("LogFile").get(0).length() - 1);
        if (map.containsKey(accessFileName)) {
            accessFileName = map.get("AccessFileName").get(0);
        } else {
            accessFileName = ".htaccess";
        }
    }

    private void parseAliases(List<String> aliasesList) {
        for (String aliasesPair : aliasesList) {
            String[] symbolicToAbsolutePath = aliasesPair.split(" ");
            aliases.put(symbolicToAbsolutePath[0], symbolicToAbsolutePath[1].trim().substring(1, symbolicToAbsolutePath[1].length() - 1));
        }
    }
    private void parsescriptAliasesList(List<String> scriptAliasesList) {
        for (String scriptAliasesPair : scriptAliasesList) {
            String[] symbolicToAbsolutePath = scriptAliasesPair.split(" ");
            scriptAliases.put(symbolicToAbsolutePath[0], symbolicToAbsolutePath[1].trim().substring(1, symbolicToAbsolutePath[1].length() - 1));
        }
    }
    public HashMap<String, String> getAliases() {
        return aliases;
    }

    public HashMap<String, String> getScriptAliases() {
        return scriptAliases;
    }

    public String getServerRoot() {
        return serverRoot;
    }

    public String getDocumentRoot() {
        return documentRoot;
    }

    public String getListen() {
        return listen;
    }

    public String getLogFile() {
        return logFile;
    }

    public String getAccessFileName() {
        return accessFileName;
    }
}
