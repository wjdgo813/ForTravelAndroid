package com.example.haehyeon.dbtest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by haehyeon on 2015-08-31.
 */
public class JoinActivity extends FragmentActivity {
    private final String SERVER_ADDRESS = "http://wjdgo813.dothome.co.kr/DB";
    EditText edtId, edtPasswd, edtRePasswd, edtName, edtAccess;

    String result = "";
    String id, password, repasswd, name ,Access;
    private ProgressDialog pDialog;
    Button btnisert;
    SessionManager session;
    boolean error=true;
    Intent intent;
    String errorMsg;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        edtId = (EditText)findViewById(R.id.editText1);
        edtPasswd = (EditText)findViewById(R.id.editText2);
        edtRePasswd = (EditText)findViewById(R.id.editText3);
        edtName = (EditText)findViewById(R.id.editText4);
        edtAccess = (EditText)findViewById(R.id.editText5);
        session = new SessionManager(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnisert = (Button)findViewById(R.id.button1);
        StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnisert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (edtId.getText().toString().equals("")
                            || edtPasswd.getText().toString().equals("")
                            || edtRePasswd.getText().toString().equals("")
                            || edtName.getText().toString().equals("")
                            ) {
                        Toast.makeText(JoinActivity.this, "모든 정보를 입력하거라", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!edtPasswd.getText().toString().equals(edtRePasswd.getText().toString()))
                    {
                        Toast.makeText(JoinActivity.this,"비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!edtAccess.getText().toString().equals("thelast"))
                    {
                        Toast.makeText(JoinActivity.this,"인증번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    id = edtId.getText().toString();
                    password = edtPasswd.getText().toString();
                    repasswd = edtRePasswd.getText().toString();
                    name = edtName.getText().toString();

                    new AsyncProgressDialog().execute();
            }
        });

    }
    private void registerUser(final String id,final String password,final String name){
        try {

            HashMap<String,String> myMap = new HashMap<String,String>();
            myMap.put("id",id);
            myMap.put("password", password);
            myMap.put("name",name);
            MyHttp joinConn = new MyHttp(myMap,AppConfig.URL_REGISTER);
            result=joinConn.conn();

            Log.v("Joinnnnnnnnnnnnnn",result);
            JSONObject jObj = new JSONObject(result);
            error = jObj.getBoolean("error");
            String er=""+error;
            Log.v("Joinnnnn error",er);
            if(error)
                 errorMsg = jObj.getString("error_msg");
            myMap.clear();
        }catch (JSONException e) {
            e.printStackTrace();
        }

}
    class AsyncProgressDialog extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            pDialog.setMessage("Registering....");
            pDialog.show();
            // TODO Auto-generated method stub

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                registerUser(id, password, name);

            } catch (Exception e) {
                Toast.makeText(JoinActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            pDialog = null;
            if(error)
            {
                Toast.makeText(JoinActivity.this,errorMsg, Toast.LENGTH_SHORT).show();
                return;
            }
            else if(!error){
                Toast.makeText(JoinActivity.this, "회원가입 성공", Toast.LENGTH_SHORT)
                        .show();
                intent = new Intent(JoinActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }


        }
    }
}
