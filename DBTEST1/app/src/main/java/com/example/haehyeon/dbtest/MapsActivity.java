package com.example.haehyeon.dbtest;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;
import com.skp.Tmap.TMapPOIItem;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity {
    GpsInfo gps;
    TMapView mMapView;                                    // 화면에 map을 띄우기 위한 변수
    Button button2;
    TMapMarkerItem item = new TMapMarkerItem();
    String frdName;
    Boolean isTracking;
    Button friendRequestBtn;
    Button trackingBtn;
    Button isAbleSearching;
    Boolean isAbleSearch;
    ListView placeList;
    ArrayList<String> postMarker = new ArrayList<String>();
    ArrayAdapter<ListItem> mAdapter;
    EditText searchEdit;
    Button Search;
    /*
     * onCreate 메소드
     * 입력: 없음
     * 출력: 없음
     * 설명: 액티비티가 시작될 때 자동으로 호출된다. 메인 레이아웃을 만들고, 초기화를 하며 버튼 클릭 리스너를 구현한다.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_maps);
        button2 = (Button) findViewById(R.id.button2);
        gps = new GpsInfo(MapsActivity.this);
        friendRequestBtn = (Button) findViewById(R.id.friendRequestBtn);
        mMapView = (TMapView) findViewById(R.id.mapView);
        trackingBtn = (Button) findViewById(R.id.trackingBtn);
        isAbleSearching = (Button) findViewById(R.id.isAbleSearching);
        isAbleSearching.setBackgroundDrawable(getResources().getDrawable(R.drawable.tracking));

        isAbleSearch = true;
        isTracking = false;
        trackingBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.xcamera));

        placeList = (ListView)findViewById(R.id.placeList);
        searchEdit = (EditText)findViewById(R.id.searchEdit);
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<ListItem>()); // 검색결과 데이터를 가지고 있는 adapter
        placeList.setAdapter(mAdapter);

        button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.qr));
        placeList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListItem item = (ListItem)placeList.getItemAtPosition(position);
                moveMap(item.item.getPOIPoint().getLatitude(), item.item.getPOIPoint().getLongitude());
                mAdapter.clear(); // 아이템이 눌렸을 때, 그 리스트를 맵에서 안보이게 clear시킴
            }
        });

        // APP KEY
        mMapView.setSKPMapApiKey("458a10f5-c07e-34b5-b2bd-4a891e024c2a");


		/*
         * onApiKey 메소드
		 * 입력: 없음
		 * 출력: 없음
		 * 설명: 올바른 APP KEY면 성공메시지를 띄우고 맵을 set함, 그렇지 않으면 실패메시지를 띄움
		 */
        mMapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {

            @Override
            public void SKPMapApikeySucceed() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MapsActivity.this, "api setup success", Toast.LENGTH_SHORT).show();
                        setupMap();
                    }
                });
            }

            @Override
            public void SKPMapApikeyFailed(String arg0) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MapsActivity.this, "api setup fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

		/*
         * onClick 메소드
		 * 입력: 없음
		 * 출력: 없음
		 * 설명: 줌인버튼이 눌리면 TMAP에서 제공하는 함수인 MapZoomIn()을 사용해 맵을 zoom in 한다
		 */
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, QrActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        friendRequestBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String gpsInfo = gps.getLatitude() + "," + gps.getLatitude();
                    String id = Values.setting.getString("id", "");
                    HashMap<String, String> myMap = new HashMap<String, String>();
                    myMap.put("id", id);
                    myMap.put("gps", gpsInfo);
                    MyHttp menuConn = new MyHttp(myMap, AppConfig.URL_GPSREGISTER);
                    String result = menuConn.conn();
                    Log.v("MenuResult", result);
                    JSONObject jObj = new JSONObject(result);
                    Boolean error = jObj.getBoolean("error");

                    if (error) {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(MapsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.v("MenuError", errorMsg);
                    } else {
                        JSONObject user = jObj.getJSONObject("user");
                        String gps = user.getString("gps");
                        Log.v("Menuuuuuu.gps", gps);/*
                        Values.editor.putString("gps", gps);
                        Values.editor.commit();*/
                        Intent intent = new Intent(MapsActivity.this, SharedGpsActivity.class);
                        startActivity(intent);
                        myMap.clear();
                    }
                } catch (JSONException e) {

                }

            }
        });
        /*
        Tracking Mode 설정 Button
         */
        trackingBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isTracking = !isTracking;
                mMapView.setTrackingMode(isTracking);
                if (isTracking) {
                    Toast.makeText(MapsActivity.this, "카메라 따라가기 On", Toast.LENGTH_SHORT).show();
                   // BitmapDrawable trackingO = new BitmapDrawable(getResources().getDrawable(R.drawable.camera),)
                    trackingBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.camera));
                    //trackingBtn.invalidateDrawable(getResources().getDrawable(R.drawable.ic_launcher));
                } else {
                    trackingBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.xcamera));
                    Toast.makeText(MapsActivity.this, "카메라 따라가기 Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
        * ReadFrdGPSActivity로 넘어가서 친구 좌표값을 onActivityResult를 통해 받아옴.
        */
        Button btn = (Button) findViewById(R.id.friendList);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mMapView.MapZoomIn();
                Intent intent = new Intent(MapsActivity.this, ReadFrdGpsActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        isAbleSearching.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isAbleSearch = !isAbleSearch;
                String id = Values.setting.getString("id", "");
                String a;
                if (isAbleSearch) {
                    a = "1";
                    isAbleSearching.setBackgroundDrawable(getResources().getDrawable(R.drawable.tracking));
                } else {
                    a = "0";
                    isAbleSearching.setBackgroundDrawable(getResources().getDrawable(R.drawable.xtracking));
                }
                //isAbleSearch=true;
                try {
                    HashMap<String, String> myMap = new HashMap<String, String>();
                    myMap.put("isAble", a);
                    myMap.put("id", id);
                    MyHttp isAbleConn = new MyHttp(myMap, AppConfig.URL_AGREETRACKING);
                    String result = isAbleConn.conn();
                    JSONObject jObj = new JSONObject(result);
                    Boolean error = jObj.getBoolean("error");
                    Log.v("MapsActivity.isAble",result);
                    if (error) {
                        Toast.makeText(MapsActivity.this, jObj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MapsActivity.this, "위치 추적 상태 : "+isAbleSearch, Toast.LENGTH_SHORT).show();
                    }
                    myMap.clear();
                } catch (JSONException e) {
                }

            }
        });

		/*
         * onClick 메소드
		 * 입력: 없음
		 * 출력: 없음
		 * 설명: 줌인버튼이 눌리면 TMAP에서 제공하는 함수인 MapZoomOut()을 사용해 맵을 zoom out 한다
		 */
        btn = (Button) findViewById(R.id.btn_zoom_out);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mMapView.MapZoomOut();
            }
        });
        Search = (Button)findViewById(R.id.Search);
        Search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String keyword = searchEdit.getText().toString();
                if (keyword != null && !keyword.equals("")) {
                    if(postMarker != null){ // 이전 marker에 저장된 id들이 있으면 이걸 제거하고나서 새로운걸 검색하게 함
                        for(int i = 0; i<postMarker.size(); i++){
                            mMapView.removeMarkerItem(postMarker.get(i));
                        }
                    }
                    TMapData data = new TMapData();
                    data.findAllPOI(keyword, new TMapData.FindAllPOIListenerCallback() {

                        @Override
                        public void onFindAllPOI(final ArrayList<TMapPOIItem> poilist) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    /*for (int i = 0; i < mAdapter.getCount(); i++) {// 전에 검색했었던 값들의 마커제거
                                        ListItem item = mAdapter.getItem(i);
                                        mMapView.removeMarkerItem(item.id);
                                    }*/
                                    mAdapter.clear();
                                    for (TMapPOIItem poi : poilist) { // 새로 검색된 값들 마커추가
                                        ListItem item = new ListItem();
                                        item.item = poi;
                                        mAdapter.add(item);
                                        postMarker.add(searchAddMarker(item));
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1:
                String destination = data.getStringExtra("destination");
                TMapData tmapData = new TMapData();

                double destiLat = getDestiLat(destination);
                double destiLon = getDestiLon(destination);
                Log.v("MapsActivity.myLoca", gps.getLatitude() + "," + gps.getLongitude());
                Log.v("MapsActivity.desti", destiLat + "," + destiLon);
                //37.4762297,126.63569
                mMapView.removeTMapPath();
                tmapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, new TMapPoint(gps.getLatitude(), gps.getLongitude()), new TMapPoint(destiLat, destiLon),
                        new TMapData.FindPathDataListenerCallback() {
                            @Override
                            public void onFindPathData(TMapPolyLine polyLine) {
                                mMapView.addTMapPath(polyLine);
                            }
                        });
                break;

            case 2:
                String friend = data.getStringExtra("positon");//위치값
                frdName = data.getStringExtra("frdName");
                Log.v("MapsActivity.friend", friend);
                Location frd = getFrdLocation(friend);
                addMarker(frd);
                realTimeFriend();
                break;

            default:
                break;
        }

    }

    private int poiIndex = 0;
    private String searchAddMarker(ListItem list) {
        TMapMarkerItem searchItem = new TMapMarkerItem();
        Bitmap bm = ((BitmapDrawable)getResources().getDrawable(R.drawable.cursor)).getBitmap(); // 마커이미지 바꿈
        searchItem.setIcon(bm);
        searchItem.setTMapPoint(list.item.getPOIPoint());
        searchItem.setPosition(0.5f, 1);
        searchItem.setCalloutTitle(list.item.getPOIName());
        //searchItem.setCalloutSubTitle(list.item.getPOIContent());
        Bitmap right = ((BitmapDrawable)getResources().getDrawable(R.drawable.red_button2)).getBitmap(); // 누르면 위,경도 뜨는 아이콘
        searchItem.setCalloutRightButtonImage(right);
        searchItem.setCanShowCallout(true);
        list.id = "poi" + poiIndex++;
        mMapView.addMarkerItem(list.id, searchItem); // mapView에 마커아이템 추가
        return searchItem.getID();
    }

    private void addMarker(Location location) {
        Bitmap bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.friend)).getBitmap(); // 마커이미지 바꿈
        item.setIcon(bm);
        item.setTMapPoint(new TMapPoint(location.getLatitude(), location.getLongitude()));
        item.setPosition(0.5f, 1);
        item.setName("도착지");

        item.setCalloutTitle("friend");

        item.setCanShowCallout(true);

        mMapView.addMarkerItem("markerasd", item);    // mapView에 마커아이템 추가
    }
    /*
     * Used for receiving notifications from the LocationManager when the location has changed.
     * LocationManager service가 requestLocationUpdates메소드를 이용하면 등록됨
     * 현재 위치를 나타내기 위한 리스너
     */

    LocationListener mCurrentLocation = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (isAbleSearch) {
                try {

                    setCurrentLocation(location.getLatitude(), location.getLongitude());
                    HashMap<String, String> myMap = new HashMap<String, String>();
                    String myGps = location.getLatitude() + "," + location.getLongitude();
                    String id = Values.setting.getString("id", "");
                    myMap.put("gps", myGps);
                    myMap.put("id", id);
                    MyHttp registerMe = new MyHttp(myMap, AppConfig.URL_GPSREGISTER);

                    String result = registerMe.conn();
                    JSONObject jObj = new JSONObject(result);
                    Boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Log.v("Maps.locationCheack", "");
                    } else {
                        Log.v("Maps.locationCheack", "error");
                    }
                    myMap.clear();

                } catch (JSONException e) {
                }

                Log.v("MapsActivity.locachange", location.getLongitude() + "");
            }
            else{
                setCurrentLocation(location.getLatitude(), location.getLongitude());
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };

    /*
     * onStart 메소드
     * 입력: 없음
     * 출력: 없음
     * 설명: 액티비티가 시작되면 LocaionManager에 위치, 현재위치 리스너를 업데이트
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * onStop 메소드
     * 입력: 없음
     * 출력: 없음
     * 설명: 위치갱신한것을 다 지움
     */
    @Override
    protected void onStop() {
        super.onStop();
        gps.stopUsingGPS();
        //gps1.CloseGps();
    }

    boolean isInitialized = false;

    /*
     * setupMap 메소드
     * 입력: 없음
     * 출력: 없음
     * 설명: MAP을 셋팅하는 메소드.
     */
    private void setupMap() {


        gps = new GpsInfo(MapsActivity.this);
        Location me = gps.getLocation();

        if (gps.isGetLocation()) {

            mMapView.setLanguage(mMapView.LANGUAGE_KOREAN);
            mMapView.setMapType(mMapView.MAPTYPE_STANDARD);
            setMyPosition(me);
            checkRequestFridn();
            /*
		 * OnCalloutRightButtonClick 메소드
		 * 입력: 없음
		 * 출력: 마커아이템을 반환함
		 * 설명: 마커아이템에서 오른쪽버튼이 눌리면 실행되는 메소드. 해당 마커의 위, 경도값을 가져와 메시지로 띄움
		 */
            mMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {

                @Override
                public void onCalloutRightButton(final TMapMarkerItem item) {
                    // 아두이노에 위,경도값 전달하기 위해 설정한 변수
                    String longitude = String.format("%.6f", item.longitude);
                    String latitude = String.format("%.6f", item.latitude);


                    TMapData tMapData = new TMapData();
                    mMapView.removeTMapPath();
                    tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, new TMapPoint(gps.getLatitude(), gps.getLongitude()), new TMapPoint(item.latitude, item.longitude),
                            new TMapData.FindPathDataListenerCallback() {
                                @Override
                                public void onFindPathData(TMapPolyLine polyLine) {
                                    mMapView.addTMapPath(polyLine);
                                }
                            });
                    String str = "" + longitude + "," + latitude;
                    Toast.makeText(MapsActivity.this,longitude+","+latitude,Toast.LENGTH_SHORT).show();
                    //MainActivity.blue.send(str);
                }
            });

        } else {
            gps.showSettingsAlert();
        }
    }

    public void onResume() {
        super.onResume();
        String locProv = gps.locationManager.getBestProvider(gps.getCriteria(), true);
        Log.v("Maps.locProv", locProv);
        gps.locationManager.requestLocationUpdates(locProv, 3000, 3, mCurrentLocation);
        setupMap();

    }

    public void setMyPosition(Location myPos) {
        setCurrentLocation(myPos.getLatitude(), myPos.getLongitude());
        mMapView.setTrackingMode(isTracking);
        moveMap(myPos);
        mMapView.setZoomLevel(30);
    }


    public void setCurrentLocation(double lat, double lon) {
        mMapView.setLocationPoint(lon, lat);
        mMapView.setIconVisibility(true);
        Bitmap bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.me)).getBitmap();
        mMapView.setIcon(bm);
       // mMapView.setCenterPoint(lon, lat);
    }

    public double getDestiLat(String dest) {
        //GEO:37.3028339,127.9081367 (한라대 좌표)
        char tmp;
        boolean ableGetLat = false;
        boolean ableGetLon = false;
        String strLat = "";
        for (int i = 0; i < dest.length(); i++) {
            tmp = dest.charAt(i);
            if (ableGetLat == true && tmp != ',') {
                strLat += tmp;
            }

            if (tmp == ':') {
                ableGetLat = true;
            } else if (tmp == ',') {
                ableGetLat = false;
            }
        }
        double lat = Double.parseDouble(strLat);
        return lat;
    }

    public double getDestiLon(String dest) {
        //GEO:37.3028339,127.9081367 (한라대 좌표)
        char tmp;
        boolean ableGetLat = false;
        boolean ableGetLon = false;
        String strLon = "";
        for (int i = 0; i < dest.length(); i++) {
            tmp = dest.charAt(i);
            if (ableGetLon == true) {
                strLon += tmp;
            }

            if (tmp == ':') {
                ableGetLat = true;
            } else if (tmp == ',') {
                ableGetLat = false;
                ableGetLon = true;
            }
        }
        double lon = Double.parseDouble(strLon);
        return lon;
    }

    public Location getFrdLocation(String friend) {
        char tmp;
        Boolean write = false;
        String strLat = "";
        String strLon = "";
        //37.4770997,126.63641
        for (int i = 0; i < friend.length(); i++) {
            tmp = friend.charAt(i);
            if (!write && tmp != ',') {
                strLat += tmp;
            } else if (write == true) {
                strLon += tmp;
            }
            if (tmp == ',') {
                write = true;
            }
        }
        double lat = Double.parseDouble(strLat);
        double lon = Double.parseDouble(strLon);
        Location frdLoca = new Location("friend");
        frdLoca.setLatitude(lat);
        frdLoca.setLongitude(lon);
        return frdLoca;
    }

    /*
     * moveMap 메소드
     * 입력: location=
     * 출력: 없음
     * 설명:  입력값에 맞게 그 값이 중앙에 오도록 map을 움직임
     */
    private void moveMap(Location location) {
        mMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
    }

    /* moveMap 메소드
     * 입력: 위,경도
     * 출력: 없음
     * 설명: 입력값에 맞게 그 값이 중앙에 오도록 map을 움직임
     */
    private void moveMap(double lat, double lng) {
        mMapView.setCenterPoint(lng, lat);
    }

    public Location realTimeSearchFriendPositon(String frdNameParam) {
        Location frdLoca = new Location("frdPosi");
        try {
            HashMap<String, String> myMap = new HashMap<String, String>();
            myMap.put("frdName", frdNameParam);
            MyHttp mapsActivitysSearchFriend = new MyHttp(myMap, AppConfig.URL_SEARCHFRIEND);


            String result = mapsActivitysSearchFriend.conn();
            JSONObject jObj = new JSONObject(result);
            Boolean error = jObj.getBoolean("error");

            if (error) {
            } else {
                String friendPosition = jObj.getString("position");
                frdLoca = getFrdLocation(friendPosition);
                Log.v("MapsActivity.positon", friendPosition);
                myMap.clear();
            }

        } catch (JSONException e) {
        }
        return frdLoca;

    }

    //친구 요청 확인
    public void checkRequestFridn(){

        try {
            String myId = Values.setting.getString("id", "");

            HashMap<String, String> myMap = new HashMap<String, String>();
            myMap.put("myId", myId);
            MyHttp sharedGpsConn = new MyHttp(myMap, AppConfig.URL_POSTFRIEND);
            String result = sharedGpsConn.conn();

            Log.v("SharedGPS.error", result + " aaaaaaaaaaaaaaaaa");
            if (result.equals("false")) {
                friendRequestBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.friendadd));
            }
            else {
                JSONObject jObj = new JSONObject(result);
                Boolean error = jObj.getBoolean("error");
                Log.v("SharedGPS.errorrrr", error + "");
                if (error) {
                    String errMsg = jObj.getString("error_msg");
                    Log.v("SharedGPS.ERRORMSG", error + "");
                    Toast.makeText(MapsActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                }
                else{
                    friendRequestBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.friendaddrequest));
                }
            }
            myMap.clear();
        }
        catch (JSONException e){
        }
    }

    //친구 마커 추가
    public void realTimeFriend() {
        if (!frdName.equals("")) {
            MyThread myThread = new MyThread();
            myThread.start();

        } else {
            Toast.makeText(MapsActivity.this, "친구 이름 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public class MyThread extends Thread {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3000);
                    addMarker(realTimeSearchFriendPositon(frdName));
                } catch (Exception e) {
                }
            }
        }
    }


}