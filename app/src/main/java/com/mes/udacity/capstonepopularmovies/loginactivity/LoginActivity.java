package com.mes.udacity.capstonepopularmovies.loginactivity;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mes.udacity.capstonepopularmovies.R;

import static com.mes.udacity.capstonepopularmovies.utils.StaticMethods.attachLoginFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentManager fragmentManager = getSupportFragmentManager();
        attachLoginFragment(fragmentManager,R.id.login_container);
    }

}
