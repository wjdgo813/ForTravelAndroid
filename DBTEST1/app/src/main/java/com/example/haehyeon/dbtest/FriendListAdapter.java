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

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by LG on 2015-10-30.
 */
public class FriendListAdapter extends BaseAdapter {
    private ArrayList<String> myList;
    String myId, frdName;
    String friendPosition;




    public FriendListAdapter() {
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

        TextView txt_frd = null;
        Button btnSearch = null; // 친구 요청 승낙 btn
        CustomHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend_list, parent, false);

            txt_frd = (TextView) convertView.findViewById(R.id.txt_frd);
            btnSearch = (Button) convertView.findViewById(R.id.search);

            StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            holder = new CustomHolder();
            holder.myTextView = txt_frd;
            holder.myBtnSch = btnSearch;
            convertView.setTag(holder);

        } else {
            holder = (CustomHolder) convertView.getTag();
            txt_frd = holder.myTextView;
            btnSearch = holder.myBtnSch;
        }

        txt_frd.setText(myList.get(position));

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frdName = myList.get(pos);//상대방 id,

                MyThread mythread = new MyThread();
                mythread.start();

                try {
                    //frdId//내 친구 이름
                    myId = Values.setting.getString("id", "");

                    Log.v("myID", myId);
                    Log.v("frdName", frdName);

                    HashMap<String,String> myMap = new HashMap<String,String>();
                    myMap.put("frdName",frdName);
                    MyHttp frdAdapter = new MyHttp(myMap,AppConfig.URL_SEARCHFRIEND);
                    String result = frdAdapter.conn();
                    Log.v("FrdListAdapter.result",result);

                    JSONObject jObj = new JSONObject(result);
                    Boolean error = jObj.getBoolean("error");

                    if(error){
                        String errorMsg = jObj.getString("error_msg");
                        if(errorMsg.equals("2")){
                            Toast.makeText(context,"현재 "+frdName+"님께서 위치추적을 원하지 않으십니다.",Toast.LENGTH_SHORT).show();
                            Log.v("FrdListAdapter.not", "접근ㄴㄴ");
                        }
                        else{
                            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        friendPosition = jObj.getString("position");
                        Log.v("FriendList.positon", friendPosition);
                        String check ="2";
                            int RESULT_CODE = 2;
                            Intent intent = new Intent(context, ReadFrdGpsActivity.class);
                            intent.putExtra("positon", friendPosition);
                            intent.putExtra("frdName", frdName);
                            ((Activity) context).setResult(RESULT_CODE, intent);
                            ((Activity) context).finish();


                    }
                    myMap.clear();

                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
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
        Button myBtnSch;
    }

    public class MyThread extends Thread {
        public void run() {
        }

    }
}