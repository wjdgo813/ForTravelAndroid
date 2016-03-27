package com.example.haehyeon.dbtest;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class SharedGpsActivity extends FragmentActivity {
    Button addBtn;
    EditText inputFrd;
    String id;
    TextView txt_nfrd;
    private ListView myListView;
    private CustomAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_gps);
        //  frdReadBtn = (Button)findViewById(R.id.frdReadBtn); //친구 목록 조회
        addBtn = (Button) findViewById(R.id.addBtn); // 친구추가 버튼
        inputFrd = (EditText) findViewById(R.id.inputFrd); // 친구 ID 입력란

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String frdId = inputFrd.getText().toString();

                    id = Values.setting.getString("id", "");
                    HashMap<String,String> myMap = new HashMap<String, String>();
                    myMap.put("myId",id);
                    myMap.put("frdId",frdId);
                    MyHttp addFriend = new MyHttp(myMap,AppConfig.URL_REQUESTFRIEND);

                    String result = addFriend.conn();

                    Log.v("SharedGpsActivity.resul", result);

                    JSONObject jObj = new JSONObject(result);
                    Boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(SharedGpsActivity.this, "친구등록 완료", Toast.LENGTH_SHORT).show();
                    } else {
                        String errMsg = jObj.getString("error_msg");
                        Log.v("SharedGPS.errorrrrrrr", errMsg);
                        Toast.makeText(SharedGpsActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                }

            }
        });



        try {
            id = Values.setting.getString("id", "");


            HashMap<String,String> myMap = new HashMap<String,String>();
            myMap.put("myId",id);
            MyHttp sharedGpsConn = new MyHttp(myMap,AppConfig.URL_POSTFRIEND);
            String result = sharedGpsConn.conn();

            Log.v("SharedGPS.error",result+" aaaaaaaaaaaaaaaaa");
            if(result.equals("false"))
            {
                txt_nfrd = (TextView)findViewById(R.id.txt_nfrd);
                txt_nfrd.setText("친구 요청이 없습니다. ");
            }

            JSONObject jObj = new JSONObject(result);
            Boolean error = jObj.getBoolean("error");
            Log.v("SharedGPS.errorrrr",error+"");

            String name1 = jObj.getString("name");
            Log.v("SharedGPS...",name1);
            JSONArray nameArray = new JSONArray(name1);
            String namea = "";


            if(error){
                String errMsg = jObj.getString("error_msg");
                Log.v("SharedGPS.ERRORMSG",error+"");
                Toast.makeText(SharedGpsActivity.this,errMsg,Toast.LENGTH_SHORT).show();

            }
            else{

                myAdapter = new CustomAdapter();
                myListView =(ListView)findViewById(R.id.listView);
                myListView.setAdapter(myAdapter);



                for(int i= 0 ;i<nameArray.length();i++){
                    myAdapter.add(nameArray.getString(i));
                }
                    Log.v("SharedGPS.nameList", namea);

            }

        }catch (JSONException e){
        }
    }


}
