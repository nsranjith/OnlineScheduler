package com.lkkn.scanner.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lkkn.scanner.app.WorkFlow.SignIn;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Qrcode extends AppCompatActivity {

    GoogleApiClient googleApiClient;
    GoogleSignInOptions gso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        googleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        //Toast.makeText(getApplicationContext(),"ërror",Toast.LENGTH_LONG).show();

                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        SharedPreferences shared = this.getSharedPreferences("loginData", Context.MODE_PRIVATE);
        if (!shared.getString("q_entered", "").equals("yes")) {
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("q_entered", "yes");
            editor.commit();

            TapTargetView.showFor(this,                 // `this` is an Activity
                    TapTarget.forView(findViewById(R.id.scan), "Scan Qr Code from here", "Navigate easily by scanning it")
                            .tintTarget(true)
                            .outerCircleColor(R.color.outer)
                            .targetCircleColor(android.R.color.white)
                            .transparentTarget(true)
                            .outerCircleAlpha(0.96f));
        }
    }


    public void scan(View view){

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.addExtra("SCAN_WIDTH",100);
        integrator.addExtra("SCAN_HEIGHT",100);
        integrator.setOrientationLocked(false);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }
    public void logout(View view){
        SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("user","");
        editor.putString("visited","none");
        editor.putString("mvisited","no");
        editor.putString("activity","no");
        editor.putString("image","null");
        editor.commit();

        Auth.GoogleSignInApi.signOut(googleApiClient);
        FirebaseAuth mAuth;
        mAuth= FirebaseAuth.getInstance();
        mAuth.signOut();
        startActivity(new Intent(this,SignIn.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();
                String url = result.getContents();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}

