package com.example.haehyeon.dbtest;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by LG on 2015-11-25.
 */
public class MyHttp {
    HashMap<String,String> myMap;
    String myURL;
    public MyHttp(HashMap<String,String> myMap,String myURL){
        this.myMap = myMap;
        this.myURL = myURL;
    }


    public String conn() {
        String result="";
        try
        {
            String[] keys = myMap.keySet().toArray(new String[0]);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(myURL);

            int length = keys.length;

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(length);
            for(int i=0;i<length;i++) {
                String key = keys[i];
                String val = myMap.get(key);
                nameValuePairs.add(new BasicNameValuePair(key, val));
                Log.v("MyHttp.value", key);
                Log.v("MyHttp.value",val);
            }


            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            result = sb.toString();
            Log.v("Myhttp.result",result);

        } catch (IOException e) {
        }
        if(result.equals("")){
            return result="false";
        }
        Log.v("MyHttp.result",result);
       return result;
    }
}
