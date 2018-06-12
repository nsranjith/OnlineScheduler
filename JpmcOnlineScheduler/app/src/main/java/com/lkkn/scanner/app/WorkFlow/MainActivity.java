package com.lkkn.scanner.app.WorkFlow;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lkkn.scanner.app.Qrcode;
import com.lkkn.scanner.app.R;
import com.lkkn.scanner.app.Util.JpmcToast;
import com.lkkn.scanner.app.Util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    String TAG="hi";
    private String mVerificationId="";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String phoneNumber="",code="";
    EditText mMobile,mOtp;
    DataSnapshot dataSnapshot1 ;
    TextView timer;
    CountDownTimer countDownTimer;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    public static final Pattern VALID_MOBILE_ADDRESS_REGEX =
            Pattern.compile("[7-9][0-9]{9}$", Pattern.CASE_INSENSITIVE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = shared.edit();
        editor.putString("visited","no");
        editor.commit();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mDatabase1 = mDatabase.child("Name");
        mAuth = FirebaseAuth.getInstance();
        mMobile = (EditText) findViewById(R.id.mobile);
        mOtp = (EditText) findViewById(R.id.otp);
        timer=(TextView)findViewById(R.id.timer);
        final Button resend=(Button)findViewById(R.id.resend);
        resend.setVisibility(View.GONE);
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setVisibility(View.VISIBLE);
                timer.setText("Resends Otp in"+String.valueOf(millisUntilFinished / 1000));
                resend.setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {

                timer.setVisibility(View.INVISIBLE);
                resend.setVisibility(View.VISIBLE);
            }
        };
    }
    public void verify(View view) {
        Matcher matcher = VALID_MOBILE_ADDRESS_REGEX.matcher(mMobile.getText().toString().trim());
        if (!TextUtils.isEmpty(mMobile.getText().toString())) {

            if (matcher.find() && (mMobile.getText().toString().length()==10 )) {
                if (Utils.hasActiveInternetConnection(MainActivity.this)) {
                    Toast.makeText(getApplicationContext(), "Otp sent successfully..", Toast.LENGTH_LONG).show();
                    mOtp.setVisibility(View.VISIBLE);
                    phoneNumber = mMobile.getText().toString();
                    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential credential) {
                            Log.d(TAG, "onVerificationCompleted:" + credential);
                            mOtp.setText(credential.getSmsCode());
                            code = credential.getSmsCode();
                            // Toast.makeText(getApplicationContext(),"Otp sent successfully..",Toast.LENGTH_LONG).show();
                            if (credential.getSmsCode() == null) {
                                signInWithPhoneAuthCredential(credential);
                            }
                            countDownTimer.cancel();
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            Log.w(TAG, "onVerificationFailed", e);
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            }
                            else if (e instanceof FirebaseTooManyRequestsException) {

                            }
                            countDownTimer.cancel();
                        }

                        @Override
                        public void onCodeSent(String verificationId,
                                               PhoneAuthProvider.ForceResendingToken token) {
                            Log.d(TAG, "onCodeSent:" + verificationId);
                            mVerificationId = verificationId;
                            mResendToken = token;
                            countDownTimer.start();
                        }


                    };

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91" + phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            this,
                            mCallbacks);
                } else {

                    JpmcToast.create(this, R.drawable.ic_error_outline_black_24dp, "No Internet\nPlease try again", Toast.LENGTH_SHORT);

                }
            }

            else {
                Toast.makeText(getApplicationContext(), "Please enter Valid mobile number", Toast.LENGTH_LONG).show();
            }

        } else {
                Toast.makeText(getApplicationContext(), "Please enter ur Mobile number to verify", Toast.LENGTH_LONG).show();


        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateUI();
                            Toast.makeText(getApplicationContext(), "Otp sent successfully..", Toast.LENGTH_LONG).show();
                            progressDialog = Utils.showLoadingDialog(MainActivity.this, false);
                            Log.d(TAG, "signInWithCredential:success");
                            Intent intent = new Intent(MainActivity.this, Servicess.class);
                            mOtp.setText(code);
                            SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString("Token", "");
                            editor.putString("count", "0");
                            editor.putString("key", "null");
                            editor.putString("Mobile", mMobile.getText().toString());
                            editor.putString("No", "1");
                            progressDialog.cancel();
                            if (shared.getString("activity", "").equals("register")) {
                                editor.putString("mvisited", "yes");
                                editor.putString("visited", "no");
                                editor.commit();
                              //
                                //  Toast.makeText(getApplicationContext(), shared.getString("visited",""),Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), SignIn.class));
                            }
                            else {

                               // Toast.makeText(getApplicationContext(), shared.getString("visited","")+"nothing",Toast.LENGTH_LONG).show();
                                startActivity(intent);
                                // FirebaseUser user = task.getResult().getUser();
                            }
                            editor.commit();

                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "OTP Verification failed", Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "not succeeded", Toast.LENGTH_LONG).show();

                            }


                        }
                    }
                });
    }

    private void updateUI() {
        SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();

        if (shared.getString("activity", "").equals("register")) {

            editor.putString("visited", "no");
            editor.commit();
        }
        else
        {
            editor.putString("visited","yes");
            editor.commit();
        }

        com.google.firebase.database.Query query = FirebaseDatabase.getInstance().getReference().child("Customer").orderByChild("Email").
                equalTo(shared.getString("user",""));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator it = dataSnapshot.getChildren().iterator();
                //String key = nodeDataSnapshot.getKey(); // this ke


                while (it.hasNext()) {
                    dataSnapshot1 = (DataSnapshot) it.next();

                }

                FirebaseDatabase.getInstance().getReference().child("Customer").child(dataSnapshot1.getKey()).child("Verified").setValue("True");

            }





            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }





    public void submitOtp(View view) {
        timer.setVisibility(View.VISIBLE);
        if (Utils.hasActiveInternetConnection(MainActivity.this)) {
            //progressDialog = Utils.showLoadingDialog(MainActivity.this, false);

            if (!TextUtils.isEmpty(mOtp.getText().toString())) {
                if (mVerificationId != null && (!TextUtils.isEmpty(mMobile.getText().toString()))) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mOtp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
                else {
                   // progressDialog.cancel();
                    Toast.makeText(getApplicationContext(), "Please enter mobile number", Toast.LENGTH_LONG).show();

                }
            } else {
                //progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Please enter Otp", Toast.LENGTH_LONG).show();

            }
        }
        else
        {
            //progressDialog.cancel();
            JpmcToast.create(this, R.drawable.ic_error_outline_black_24dp, "No Internet\nPlease try again", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}





