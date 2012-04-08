/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Christian
 */
public class UrlHelper {

    public static String getTextByUrl(String urlAsString) {
        URL url;
        InputStream is = null;
        BufferedReader reader;
        String line;
        StringBuilder sBuilder = new StringBuilder();
        URLConnection connection = null;

        try {
            url = new URL(urlAsString);
            connection = url.openConnection();
            connection.setConnectTimeout(60000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.14) Gecko/20080418 Ubuntu/7.10 (gutsy) Firefox/2.0.0.14");
            is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));

            while ((line = reader.readLine()) != null) {
                sBuilder.append(line);
            }

            return sBuilder.toString();

        } catch (Exception e) {
            play.Logger.fatal(e.getMessage());

        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }

        return "";
    }
    
    public static byte[] getByteArrayByUrl(String urlAsString) {
        URL url;
        InputStream is = null;
        String line;
        byte[] file = null;
        URLConnection connection = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            url = new URL(urlAsString);
            connection = url.openConnection();
            connection.setConnectTimeout(60000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.14) Gecko/20080418 Ubuntu/7.10 (gutsy) Firefox/2.0.0.14");
            is = connection.getInputStream();
            
            int _byte;
            
            while ((_byte = is.read()) != -1)
            {
                output.write(_byte);
            }
            

        } catch (Exception e) {
            play.Logger.fatal(e.getMessage());

        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }

        return output.toByteArray();
    }
}
