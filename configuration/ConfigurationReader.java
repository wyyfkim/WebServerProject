package configuration;

import java.io.*;
import java.util.*;

public class ConfigurationReader {
    private BufferedReader bufferedReader;
    Map<String, List<String>> map;

    public ConfigurationReader(String fileName) throws IOException {
        map = new HashMap<>();
        try {
            bufferedReader = new BufferedReader(new FileReader(new File (fileName)));
        } catch (Exception e) {

            throw new IOException();
        }

    }
//    public boolean hasMoreLines() throws IOException{
////        String temp =  bufferedReader.readLine();
//        return bufferedReader.readLine() != null
//                && bufferedReader.readLine().trim().length() > 0;
//    }
//
//    public String nextLine() throws IOException{
//            return bufferedReader.readLine();
//
//    }
    public void load() throws IOException{
        String line = bufferedReader.readLine();
        while(line != null) {
            String trimmedLine = line.trim();
            if(trimmedLine.length() == 0 || trimmedLine.charAt(0) == '#') {
                line = bufferedReader.readLine();
                continue;
            }
            String[] parameters = trimmedLine.split(" ");
            String key = parameters[0];
            StringBuilder value = new StringBuilder();
            for (int i = 1; i < parameters.length; i++) {
                value.append(parameters[i]);
                value.append(" ");
            }
            if (value.length() > 0) {
                value.deleteCharAt(value.length() - 1);
            }
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(value.toString());
            line = bufferedReader.readLine();
        }
    }
}
