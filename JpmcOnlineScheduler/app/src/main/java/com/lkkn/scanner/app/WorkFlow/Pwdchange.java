package com.lkkn.scanner.app.WorkFlow;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lkkn.scanner.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Pwdchange extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwdchange);
    }
    public void change(View view){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        EditText email=(EditText)findViewById(R.id.mobile);
        String emailAddress =email.getText().toString().trim() ;

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Password reset e-mail sent successfully..",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),SignIn.class));

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Can't send password reset e-mail",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
