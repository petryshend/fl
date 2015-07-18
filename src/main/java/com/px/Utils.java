package com.px;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Utils {

    public static JSONObject getMenCountryCriteria()
    {
        String jsonString = Utils.getStringFromFile("men.json", Charset.defaultCharset());
        return new JSONObject(jsonString);
    }

    public static String getStringFromFile(String path, Charset encoding)
    {
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not read json file");
            System.exit(2);
        }
        return new String(encoded, encoding);
    }

    public static void wait(int milliseconds)
    {
        try {
            // to sleep 10 seconds
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // recommended because catching InterruptedException clears interrupt flag
            Thread.currentThread().interrupt();
            // you probably want to quit if the thread is interrupted
        }
    }
}
