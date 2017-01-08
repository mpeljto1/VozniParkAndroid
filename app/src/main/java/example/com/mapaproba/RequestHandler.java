package example.com.mapaproba;

/**
 * Created by Mirnes on 27.12.2016..
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class RequestHandler
{


    public String sendGetRequestParam(String requestURL, String user, String pass){
        StringBuilder sb =new StringBuilder();
        try {

            URL url = new URL(requestURL + user + "&pass=" + pass);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String s;

            while((s=bufferedReader.readLine())!=null) {
                sb.append(s + "\n");
                //Log.d("String sb", s + "nesto");
            }
        }catch(Exception e){
        }
        return sb.toString();
    }
}
