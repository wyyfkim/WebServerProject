package configuration;

import java.io.IOException;

public class Htaccess extends ConfigurationReader {
    private Htpassword userFile;
    private String authType;
    private String authName;
    private String require;
    private String id;

    public Htaccess( String filename ) throws IOException {
        super( filename );
        this.load();
        String pwdFile = map.get("AuthUserFile").get(0);
        String pwdFileAbsPath = pwdFile.substring(1, pwdFile.length() - 1);
        userFile = new Htpassword(pwdFileAbsPath);
        authType = map.get("AuthType").get(0);
        authName = map.get("AuthName").get(0);
        require = map.get("Require").get(0);
    }
    public boolean isAuthorized(String authInfo) {
        setId(userFile.getUserID());
        return userFile.isAuthorized(authInfo);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
