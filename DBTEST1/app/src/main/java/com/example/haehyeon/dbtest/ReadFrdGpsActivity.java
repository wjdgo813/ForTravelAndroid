package com.example.haehyeon.dbtest;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class ReadFrdGpsActivity extends FragmentActivity {
    String id;
    TextView ReadGpstxt;
    private FriendListAdapter myAdapter;
    private ListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readfrdgps);
        StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            id = Values.setting.getString("id", "");
            ReadGpstxt = (TextView) findViewById(R.id.ReadGpstxt);


            HashMap<String,String> myMap = new HashMap<String,String>();
            myMap.put("id", id);
            MyHttp RFGPS = new MyHttp(myMap,AppConfig.URL_READFRIENDGPS);
            String result = RFGPS.conn();
            Log.v("ReadFrdGPS.result",result);

            JSONObject jObj = new JSONObject(result);
            Boolean error = jObj.getBoolean("error");
            Log.v("ReadFrdGPS.error",error+"");

            if(!error){
                String friend = jObj.getString("friend");
                JSONArray friendArray = new JSONArray(friend);

                myAdapter = new FriendListAdapter();
                myListView =(ListView)findViewById(R.id.frd_listView);
                myListView.setAdapter(myAdapter);

                for(int i=0;i<friendArray.length();i++){
                    JSONObject info = friendArray.getJSONObject(i);
                    myAdapter.add(info.getString("name"));
                }
            }
            else{
                String errMsg = jObj.getString("error_msg");
                Log.v("ReadFrdGPS.errorMsg",errMsg);
                Toast.makeText(ReadFrdGpsActivity.this,errMsg,Toast.LENGTH_SHORT).show();
            }
            myMap.clear();

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {

            case 2:
                String positon = data.getStringExtra("positon");
                String frdName = data.getStringExtra("frdName");
                    Intent intent = new Intent();
                    int RESULT_CODE = 2;
                    intent.putExtra("positon", positon);
                    Log.v("ReadFrdGps.postion", positon);
                    intent.putExtra("frdName", frdName);
                    setResult(RESULT_CODE, intent);
                    finish();

                break;

            default:
                break;
        }

    }

}
