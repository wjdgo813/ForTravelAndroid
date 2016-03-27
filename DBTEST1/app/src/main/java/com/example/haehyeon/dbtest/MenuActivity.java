package com.example.haehyeon.dbtest;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends FragmentActivity {

    Button btnqr;
    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnqr = (Button) findViewById(R.id.qrBtn);
        welcomeText=(TextView)findViewById(R.id.welcomeText);
        String name = Values.setting.getString("name", "");
        welcomeText.setText(name+"님을 환영합니다.");
        btnqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }
}