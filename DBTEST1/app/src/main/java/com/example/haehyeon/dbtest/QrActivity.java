package com.example.haehyeon.dbtest;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrActivity extends FragmentActivity {

    static String geoStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        IntentIntegrator.initiateScan(this);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MenuActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();

            } else {
                Log.d("MenuActivity", "Scanned" + result.getContents());

                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(QrActivity.this,MapsActivity.class);
               // intent.putExtra("destination",result.getContents()); //도착 정보 전송

                int RESULT_CODE = 1;
                Intent intent = new Intent();
                intent.putExtra("destination", result.getContents());
                setResult(RESULT_CODE, intent);
                finish();

               // startActivity(intent);
               // finish();

            }
        } else {
            Log.d("MenuActivity", "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
