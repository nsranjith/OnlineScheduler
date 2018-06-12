package com.lkkn.scanner.app.WorkFlow;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lkkn.scanner.app.Qrcode;
import com.lkkn.scanner.app.R;
import com.lkkn.scanner.app.Util.JpmcToast;
import com.lkkn.scanner.app.Util.Utils;
import com.google.android.gms.auth.api.*;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class SignIn extends AppCompatActivity {
    private static  final int RC_SIGN_IN=1;
    ProgressDialog progressDialog;
    Button signInButton;
    static GoogleApiClient googleApiClient,googleApiClient1;
    private String TAG="Response";
    GoogleSignInOptions gso,gso1;
    private FirebaseAuth mAuth;
    DataSnapshot dataSnapshot1;
    String key;
    FirebaseAuth.AuthStateListener authStateListener;
    String email="";
    int count=0;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.lkkn.scanner.app.R.layout.activity_sign_in);
        signInButton=(Button)findViewById(R.id.sign_in_button);
        SharedPreferences shareds = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        //Toast.makeText(getApplicationContext(), shareds.getString("user",""),Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        final com.google.firebase.database.Query query = databaseReference.child("Customer").orderByChild("Email").equalTo("g@gmail.com");
        final SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
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
                       // Toast.makeText(getApplicationContext(),"ërror",Toast.LENGTH_LONG).show();

                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        mGoogleSignInClient = GoogleSignIn.getClient(SignIn.this, gso);
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null) {



                    if (shared.getString("visited", "").equals("yes")||(shared.getString("mvisited", "").equals("yes") &&
                            FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())){
                        startActivity(new Intent(SignIn.this, Servicess.class));
                    }
//                    else
//                    {
//                        Auth.GoogleSignInApi.signOut(googleApiClient);
//                    }

                    else if(shared.getString("visited","").equals("no"))
                    {
                        mGoogleSignInClient = GoogleSignIn.getClient(SignIn.this, gso);
                        mGoogleSignInClient.signOut()
                                .addOnCompleteListener(SignIn.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // ...
                                    }
                                });
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("viisted", "none");
                        editor.commit();



                    }

                }
            }
        };








        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                signIn();

            }
        });


    }
    private void signIn() {
        if (Utils.hasActiveInternetConnection(SignIn.this)) {
            progressDialog = Utils.showLoadingDialog(SignIn.this, false);

            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);

        }
        else
        {
            JpmcToast.create(this, R.drawable.ic_error_outline_black_24dp, "No Internet\nPlease try again", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            email=user.getEmail();
                            final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
                            final com.google.firebase.database.Query query = databaseReference.child("Customer").orderByChild("Email").equalTo(user.getEmail());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists())
                                    {
                                        SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = shared.edit();

                                        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
                                        DatabaseReference databaseReferences = databaseReference.child("Customer").push();
                                        databaseReferences.child("Email").setValue(mAuth.getCurrentUser().getEmail());
                                        databaseReferences.child("Verified").setValue("false");
                                        editor.putString("user",user.getEmail());
                                        editor.putString("image",user.getPhotoUrl()+"");
                                       // Toast.makeText(getApplicationContext(),user.getEmail()+""+user.getPhotoUrl(),Toast.LENGTH_LONG).show();
                                        editor.commit();
                                       // progressDialog.cancel();
                                        checkForVerification();
                                       // startActivity(new Intent(SignIn.this, MainActivity.class));


                                    }
                                    else
                                    {
                                        checkForVerification();
                                        SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = shared.edit();
                                        editor.putString("user",user.getEmail());
                                        editor.putString("image",user.getPhotoUrl()+"");
                                        editor.commit();
                                        //Toast.makeText(getApplicationContext(),user.getEmail()+""+user.getDisplayName(),Toast.LENGTH_LONG).show();
                                        //startActivity(new Intent(SignIn.this, Servicess.class));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            //
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                }


                );
    }

    private void checkForVerification() {
        SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = shared.edit();
        editor.putString("user",mAuth.getCurrentUser().getEmail());
        editor.commit();


        Log.d("user",shared.getString("user",""));

        com.google.firebase.database.Query query= FirebaseDatabase.getInstance().getReference().child("Customer").orderByChild("Email").
                equalTo(mAuth.getCurrentUser().getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Iterator it = dataSnapshot.getChildren().iterator();
                //String key = nodeDataSnapshot.getKey(); // this ke


                while(it.hasNext()) {
                    dataSnapshot1= (DataSnapshot) it.next();


                }
                Log.d("key",dataSnapshot1.getKey());

                FirebaseDatabase.getInstance().getReference().child("Customer").child(dataSnapshot1.getKey()).child("Verified").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue().equals("false")){
                            progressDialog.cancel();
                            startActivity(new Intent(SignIn.this, MainActivity.class));
                        }
                        else
                        {
                            editor.putString("visited","yes");
                            editor.commit();
                            progressDialog.cancel();
                            startActivity(new Intent(SignIn.this, Servicess.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });







            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }
    public void register(View view){
        startActivity(new Intent(getApplicationContext(),Registration.class));
    }
    public void logIn(View view) {

        if (Utils.hasActiveInternetConnection(SignIn.this)) {
            progressDialog = Utils.showLoadingDialog(SignIn.this, false);

            final EditText name, password;
            name = (EditText) findViewById(R.id.name);
            password = (EditText) findViewById(R.id.pwd);
            if (!TextUtils.isEmpty(name.getText().toString())) {
                if (!TextUtils.isEmpty(password.getText().toString())) {
                    mAuth.signInWithEmailAndPassword(name.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        progressDialog.cancel();
                                        Toast.makeText(SignIn.this, "Login Failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else if (task.isSuccessful()) {
                                        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                            Toast.makeText(SignIn.this, "Login Success", Toast.LENGTH_LONG).show();
                                            //progressDialog.cancel();
                                            SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = shared.edit();
                                            editor.putString("user", name.getText().toString());
                                            editor.putString("activity", "register");
                                            editor.putString("image", "");
                                            editor.commit();
                                            checkForVerification();
                                            //startActivity(new Intent(getApplicationContext(), Servicess.class));
                                        } else {
                                            progressDialog.cancel();
                                            Toast.makeText(SignIn.this, "Please verify your Email", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    // ...
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SignIn.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(SignIn.this, "Incorrect email address", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignIn.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                    }
                else {
                    progressDialog.cancel();
                    Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
              }
            else {
                progressDialog.cancel();
                Toast.makeText(this, "Please Enter Email Id", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            JpmcToast.create(this, R.drawable.ic_error_outline_black_24dp, "No Internet\nPlease try again", Toast.LENGTH_SHORT);
        }
    }
    public void changePwd(View view){
        startActivity(new Intent(getApplicationContext(),Pwdchange.class));

    }


    }




