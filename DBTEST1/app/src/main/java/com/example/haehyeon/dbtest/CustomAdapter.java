package com.example.haehyeon.dbtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by LG on 2015-10-30.
 */
public class CustomAdapter extends BaseAdapter {
    private ArrayList<String> myList;
    String myId, frdName;
    boolean isAble;




    public CustomAdapter() {
        myList = new ArrayList<String>();
    }

    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        TextView text = null;
        Button btnAc = null; // 친구 요청 승낙 btn
        Button btnRe = null; // 친구 요청 거절 btn
        CustomHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_item, parent, false);

            text = (TextView) convertView.findViewById(R.id.txt_frdmsg);
            btnAc = (Button) convertView.findViewById(R.id.btn_accept);
            btnRe = (Button) convertView.findViewById(R.id.btn_refuse);
            StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            holder = new CustomHolder();
            holder.myTextView = text;
            holder.myBtnAc = btnAc;
            holder.myBtnRe = btnRe;
            convertView.setTag(holder);

        } else {
            holder = (CustomHolder) convertView.getTag();
            text = holder.myTextView;
            btnAc = holder.myBtnAc;
            btnRe = holder.myBtnRe;
        }

        text.setText(myList.get(position) + "의 친구 요청입니다.");

        btnAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frdName = myList.get(pos);//상대방 id,

                MyThread mythread = new MyThread();
                mythread.start();

                try{

                    Thread.sleep(1000);
                }catch(Exception e)
                {

                }
                if (isAble == true) {

                    Toast.makeText(context, "친구 등록 완료", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,SharedGpsActivity.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();



                } else {
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();

                    Log.v("에러!!", "erorrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
                }

            }
        });
        return convertView;
    }

    public void add(String _msg) {
        myList.add(_msg);
    }

    public void remove(int _position) {
        myList.remove(_position);
    }


    private class CustomHolder {
        TextView myTextView;
        Button myBtnAc;
        Button myBtnRe;
    }

    public class MyThread extends Thread {
        public void run() {
            try {
                //frdId//내 친구 이름
                myId = Values.setting.getString("id", "");
                Log.v("myID",myId);
                Log.v("frdId",frdName);


                HashMap<String,String> myMap = new HashMap<String,String>();
                myMap.put("myId",myId);
                myMap.put("frdName",frdName);

                MyHttp cusAdapterHttp = new MyHttp(myMap,AppConfig.URL_AGREEFRIEND);
                String result = cusAdapterHttp.conn();
                Log.v("CustomAdapter.result",result);

                JSONObject jObj = new JSONObject(result);
                Boolean error = jObj.getBoolean("error");
                Log.v("CustomAdapter.error",error+"");

                if(error){
                    String errorMsg = jObj.getString("error_msg");
                    Log.v("CustomAdapter.errorMsg", errorMsg);
                    isAble = false;
                }
                else{
                    isAble = true;
                }




            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }

    }
}