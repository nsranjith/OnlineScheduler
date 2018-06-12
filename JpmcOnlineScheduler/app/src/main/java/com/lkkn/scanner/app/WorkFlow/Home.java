package com.lkkn.scanner.app.WorkFlow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lkkn.scanner.app.Admin.Update;
import com.lkkn.scanner.app.R;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    public void admin(View view){
        startActivity(new Intent(getApplicationContext(), Update.class));

    }
    public void users(View view){
        startActivity(new Intent(getApplicationContext(), Registration.class));

    }


}
