package com.example.tg71223.roadio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LandingFragment lf = new LandingFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, lf).commit();
    }
}
