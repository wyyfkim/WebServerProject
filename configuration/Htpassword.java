package configuration;

import java.util.HashMap;
import java.util.Base64;
import java.nio.charset.Charset;
import java.security.MessageDigest;

import java.io.IOException;

public class Htpassword extends ConfigurationReader {
    private HashMap<String, String> passwords;
    private String userID;

    public Htpassword( String filename ) throws IOException {
        super( filename );
//        System.out.println( "Password file: " + filename );

        this.passwords = new HashMap<String, String>();
        this.load();
        for (String line : map.keySet()) {
            parseLine(line);
        }
    }

    protected void parseLine( String line ) {
        String[] tokens = line.split( ":" );
        if( tokens.length == 2 ) {
            passwords.put( tokens[ 0 ], tokens[ 1 ].replace( "{SHA}", "" ).trim() );
        }
    }

    public boolean isAuthorized( String authInfo ) {
        // authInfo is provided in the header received from the client
        // as a Base64 encoded string.
        String credentials = new String(
                Base64.getDecoder().decode( authInfo ),
                Charset.forName( "UTF-8" )
        );

        // The string is the key:value pair username:password
        String[] tokens = credentials.split( ":" );

        // TODO: implement this
        String inputUsername = tokens[ 0 ];
        String inputPassword = tokens[ 1 ];
        setUserID(inputUsername);
        return verifyPassword(inputUsername, inputPassword);
    }

    public boolean verifyPassword( String username, String password ) {
        // encrypt the password, and compare it to the password stored
        // in the password file (keyed by username)
        // TODO: implement this - note that the encryption step is provided as a
        // method, below
        String passwordStored = passwords.get(username);
        String encryptedPassword = encryptClearPassword(password);
        return passwordStored.equals(encryptedPassword);
    }

    private String encryptClearPassword( String password ) {
        // Encrypt the cleartext password (that was decoded from the Base64 String
        // provided by the client) using the SHA-1 encryption algorithm
        try {
            MessageDigest mDigest = MessageDigest.getInstance( "SHA-1" );
            byte[] result = mDigest.digest( password.getBytes() );

            return Base64.getEncoder().encodeToString( result );
        } catch( Exception e ) {
            return "";
        }
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

