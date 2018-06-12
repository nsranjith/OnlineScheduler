package com.lkkn.scanner.app.WorkFlow;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lkkn.scanner.app.R;
import com.lkkn.scanner.app.Util.JpmcToast;
import com.lkkn.scanner.app.Util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    ArrayList<String> arrayList;
    private String var = "SELECT BRANCH";
    EditText mobile, name, password, c_password, email;
    ProgressDialog progressDialog;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_MOBILE_ADDRESS_REGEX =
            Pattern.compile("(0/91)?[7-9][0-9]{9}$", Pattern.CASE_INSENSITIVE);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
       // Toast.makeText(getApplicationContext(), shared.getString("user", ""), Toast.LENGTH_LONG).show();


    }

    private void addItems() {

        arrayList.add("          JNTU");
        arrayList.add("          JEEDIMETLA");
        arrayList.add("          SECUNDERABAD");
        arrayList.add("          UPPAL");
        arrayList.add("          KUKATPALLY");
        arrayList.add("          BACHUPALLY");
        arrayList.add("          AMEERPET");
        arrayList.add("          BALANAGAR");
    }

    public void register(View view) {
        if (Utils.hasActiveInternetConnection(Registration.this)) {
            progressDialog = Utils.showLoadingDialog(Registration.this, false);
            name = (EditText) findViewById(R.id.mobile);
            email = (EditText) findViewById(R.id.email);
            password = (EditText) findViewById(R.id.password);
            c_password = (EditText) findViewById(R.id.cpassword);
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email.getText().toString());
            final FirebaseAuth mAuth;

            if (matcher.find()) {
                //Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(name.getText().toString())) {
                    if (password.getText().toString().equals(c_password.getText().toString())) {
                        if (password.getText().toString().length() >= 6) {
//                            if (!var.equals("          SELECT BRANCH")) {
                                mAuth = FirebaseAuth.getInstance();

                                Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email.getText().toString()
                                        , password.getText().toString())
                                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (task.isSuccessful()) {
                                                    // mAuth.reload();
                                                  //  mAuth.getCurrentUser().reload();
                                                    //boolean emailVerified = mAuth.getCurrentUser().isEmailVerified();



                                                       progressDialog.cancel();
                                                        emailVerification();
                                                        storeDetails();

                                                        Intent intent = new Intent(Registration.this, MainActivity.class);
                                                        SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = shared.edit();
                                                        editor.putString("activity","register");
                                                        editor.putString("user", email.getText().toString());
                                                        editor.commit();
                                                        Toast.makeText(Registration.this, "Successfully Registered..Thanks for Being a part of us..!", Toast.LENGTH_SHORT).show();
                                                        startActivity(intent);
                                                       // Toast.makeText(getBaseContext(), "Job Done..!", Toast.LENGTH_LONG).show();

                                                }
                                                if (!task.isSuccessful()) {
                                                    progressDialog.cancel();
                                                    Toast.makeText(getBaseContext(), "E-mail I'd already exists",
                                                          Toast.LENGTH_SHORT).show();
                                                }


                                                // ...
                                            }
                                        });


//                            } else {
//                                progressDialog.cancel();
//                                Toast.makeText(this, "Please Select Branch", Toast.LENGTH_SHORT).show();
//                            }
                        } else {
                            progressDialog.cancel();
                            Toast.makeText(this, "Minimum length of Password is 6 characters", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.cancel();
                        Toast.makeText(this, "Password Mismatch", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    progressDialog.cancel();
                    Toast.makeText(this,"Please enter your Name",Toast.LENGTH_LONG).show();
                }
            }


            else {
                progressDialog.cancel();
                Toast.makeText(this, "Please give valid User Email", Toast.LENGTH_SHORT).show();
            }

        } else {
            progressDialog.cancel();
            JpmcToast.create(this, R.drawable.ic_error_outline_black_24dp, "No Internet\nPlease try again", Toast.LENGTH_SHORT);

        }
    }

    private void emailVerification() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.cancel();
                          Toast.makeText(getApplicationContext(),"Email verification link has been sent",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Email verification link sending failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    private void storeDetails() {
        if (Utils.hasActiveInternetConnection(Registration.this)) {
            progressDialog = Utils.showLoadingDialog(Registration.this, false);
            if (var != null) {
                SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);

                DatabaseReference mDatabase, mDatabase1, mDatabase2, mDatabase3, mDatabase4;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase1 = mDatabase.child("Customer");
                mDatabase2 = mDatabase1.push();
                mDatabase2.child("Email").setValue(email.getText().toString());
                mDatabase2.child("Name").setValue(name.getText().toString());
                mDatabase2.child("Branch").setValue(var);
                mDatabase2.child("Verified").setValue("false");
                mDatabase2.child("Token").setValue(shared.getString("Tokens",""));
            }
            else {
                progressDialog.cancel();
               // Toast.makeText(getApplicationContext(), "Please choose Home branch", Toast.LENGTH_LONG).show();
            }


        } else {
            progressDialog.cancel();
            JpmcToast.create(this, R.drawable.ic_error_outline_black_24dp, "No Internet\nPlease try again", Toast.LENGTH_SHORT);
        }
    }
    public void back(View view){
        startActivity(new Intent(getApplicationContext(),SignIn.class));
    }
}



















