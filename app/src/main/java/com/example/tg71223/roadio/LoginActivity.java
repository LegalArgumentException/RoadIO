package com.example.tg71223.roadio;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class LoginActivity extends AppCompatActivity implements LandingFragment.LoginFragmentListener, SignUpFragment.SignUpFragmentListener {

    private boolean login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = true;
        LandingFragment lf = new LandingFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, lf).commit();

//        FrameLayout fl = (FrameLayout) findViewById(R.id.fragment_container);
//        fl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                transitionScene();
//            }
//        });
    }

    public void transitionScene() {
        Fragment transitionFrag;
        if(login) {
            transitionFrag = new SignUpFragment();
        } else {
            transitionFrag = new LandingFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, transitionFrag)
                .commit();
        login = !login;
    }

    public void changeActivity() {
        Intent i = new Intent(LoginActivity.this, MapActivity.class);
        startActivity(i);
    }
}
