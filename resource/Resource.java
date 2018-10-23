package resource;

import configuration.HttpdConf;

import java.io.File;

public class Resource {
    private String uri;
    private String verb;
    private String uriFirstPart;
    private String uriRestPart;
    private String absolutePath;
    private HttpdConf config;
//    private File destinationFile;
    private File accessFile;
    public Resource(String uri, HttpdConf config, String verb) {
        this.uri = uri;
        this.verb = verb;
        this.config = config;

        this.accessFile = null;
        int uriFirstPartEndIndex = uri.indexOf("/", 1);
        if (uriFirstPartEndIndex > 0) {
            this.uriFirstPart = uri.substring(0, uriFirstPartEndIndex + 1);
            this.uriRestPart = uri.substring(uriFirstPartEndIndex + 1, uri.length());
        } else {
            uriFirstPart = uri;
            uriRestPart = "";
        }
        absolutePath();
    }

    public String absolutePath() {
        String absolutePath = "";
        if (config.getAliases().containsKey(uriFirstPart)) {
            absolutePath = config.getAliases().get(uriFirstPart) + uriRestPart;
        } else if (config.getScriptAliases().containsKey(uriFirstPart)) {
            absolutePath = config.getScriptAliases().get(uriFirstPart) + uriRestPart;
        } else {
            absolutePath = config.getDocumentRoot() + uri.substring(1, uri.length());
        }
        File file = new File(absolutePath);
        if (!verb.equals("PUT") && !verb.equals("DELETE") && !file.isFile()) {
            if (absolutePath.charAt(absolutePath.length() - 1) != '/') {
                absolutePath = absolutePath + "/";
            }
            absolutePath = absolutePath + "index.html";
        }
        this.absolutePath = absolutePath;
        return absolutePath;
    }

    public String getFileType() {
        String extension = absolutePath.substring(absolutePath.lastIndexOf(".") + 1, absolutePath.length());
        return extension;
    }

    public boolean isScript() {
        return config.getScriptAliases().containsKey(uriFirstPart);
    }

    public File getDestinationFile() {
        File destinationFile = new File(this.absolutePath);
        return destinationFile;
    }
    public boolean isProtected() {
        if (getDestinationFile() == null || !getDestinationFile().isFile()) {
            return false;
        }
        File pre = getDestinationFile().getParentFile();
        File[] allFiles = pre.listFiles();
        for (File file : allFiles) {
            if (file.getName().endsWith(config.getAccessFileName())) {
                accessFile = file;
                return true;
            }
        }
        return false;
    }

//    public File getDestinationFile() {
//        return destinationFile;
//    }

    public File getAccessFile() {
        return accessFile;
    }

    public HttpdConf getConfig() {
        return config;
    }
}
