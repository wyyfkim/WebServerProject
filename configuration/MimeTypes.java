package configuration;

import java.io.IOException;
import java.util.*;

public class MimeTypes extends ConfigurationReader {
    private HashMap<String, String> types;  //application/octet-stream	bin dms lha lzh exe class so dll

    public MimeTypes(String fileName) throws IOException{
        super(fileName);
        this.load();
        types = new HashMap<>();
        for (String key : map.keySet()) {
            String FileType = key;
            String[] parseKey = key.split("\t");
            if (parseKey.length > 1) {
                FileType = parseKey[0].trim();
                for (int i = 1; i < parseKey.length; i++) {
                    if (parseKey[i].trim().length() > 0) {
                        types.put(parseKey[i], FileType);
                    }
                }
            }
            if(map.get(key).get(0).length() > 0) {
                String[] extensions = map.get(key).get(0).split(" ");
                for (String extension : extensions) {
                    types.put(extension.trim(), FileType.trim());
                }
            }
        }
    }
    public String lookup(String extension) {
        return types.get(extension);
    }

}
