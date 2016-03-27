package com.example.haehyeon.dbtest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends FragmentActivity {
    public static final String SERVER_ADDRESS = "http://wjdgo813.dothome.co.kr/DB";
    Intent intent;
    Button btnLogin;
    Button btnJoin;
    EditText edtid;
    EditText edtpass;
    private SessionManager session;
    ProgressDialog dialog;
    String id,myId,passwrd;
    String result = "";
    boolean checkLogin = false;
    String jsonName;
    boolean error = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtid = (EditText)findViewById(R.id.edtid);
        edtpass = (EditText)findViewById(R.id.edtpass);
        session = new SessionManager(getApplicationContext());
        Values.setting = getSharedPreferences("setting",0);
        Values.editor = Values.setting.edit();


    }
    class AsyncProgressDialog extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

            // TODO Auto-generated method stub

            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("로그인중ㅋㅋㅋ");
            dialog.setMessage("쫌만 기다려...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params){
        try {
            LoginCheck(myId,passwrd);
        }
        catch (Exception e) {
            Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
        }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            dialog.dismiss();
            dialog=null;
            if (!error) {
                try {

                    id = Values.setting.getString("id", "");
                    //welcomeText.setText(jsonName + "님을 환영합니다.\n 잠시만 기다려 주십시오.");

                    intent = new Intent(MainActivity.this, MenuActivity.class);
                    Toast.makeText(MainActivity.this, jsonName+"님을 환영합니다!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }catch (Exception e){
                }
            } else if (error) {
                new AlertDialog.Builder(MainActivity.this)
                        .setPositiveButton("확인", null)
                        .setMessage("아이디와 비밀번호를 확인하세요.").show();
            } else {
                Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }

        }



    }
    public void login() {
        myId = edtid.getText().toString();
        passwrd = edtpass.getText().toString();
        if (myId.length() <= 0) {
            new AlertDialog.Builder(MainActivity.this).setPositiveButton("확인", null)
                    .setMessage("ID를 입력해 주세요").show();
        } else if (passwrd.length() <= 0) {
            new AlertDialog.Builder(MainActivity.this).setPositiveButton("확인", null)
                    .setMessage("패스워드를 입력해 주세요").show();
        } else {
            checkLogin = false;
            new AsyncProgressDialog().execute();
        }
    }
    public void mOnClick(View v) {
        btnLogin = (Button) findViewById(R.id.btnlogin);
        btnJoin = (Button) findViewById(R.id.btnJoin);
        if (v.getId() == btnLogin.getId()) {
            login();

        } else if ((v.getId() == btnJoin.getId())) {
            Toast.makeText(MainActivity.this, "회원가입 페이지로 이동합니다.", Toast.LENGTH_SHORT)
                    .show();
            intent = new Intent(MainActivity.this, JoinActivity.class);
            startActivity(intent);
        }
    }

    public void LoginCheck(final String myId,final String passwrd){
        try {
            HashMap<String,String> myMap = new HashMap<String,String>();
            myMap.put("id",myId);
            myMap.put("password", passwrd);
            MyHttp mainConn = new MyHttp(myMap,AppConfig.URL_LOGIN);

            result = mainConn.conn();
            Log.v("Mainnnn",result);
            JSONObject jObj = new JSONObject(result);
            error = jObj.getBoolean("error");
            String err = ""+error;
            Log.v("Mainnn",err);
            JSONObject user = jObj.getJSONObject("user");
            jsonName = user.getString("name");
            Values.editor.putString("name",jsonName);
            Values.editor.putString("id",myId);
            Values.editor.commit();

            if(!error){
                    session.setLogin(true, myId);
            }
            else {
                // 로그인 에러. 에러 메시지를 확인한다.
                String errorMsg = jObj.getString("error_msg");
                Toast.makeText(MainActivity.this,errorMsg, Toast.LENGTH_SHORT).show();
            }
            myMap.clear();

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}



