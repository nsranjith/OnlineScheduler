package com.lkkn.scanner.app.Admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lkkn.scanner.app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Update extends AppCompatActivity {
    DatabaseReference references,references1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
    }
    public void update(View view){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Branch");
        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("Running");
        ArrayList<String> branches=new ArrayList<>();
        branches.add("SELECT BRANCH");
        branches.add("JNTU");
        branches.add("JEEDIMETLA");
        branches.add("SECUNDERABAD");
        branches.add("UPPAL");
        branches.add("KUKATPALLY");
        branches.add("BACHUPALLY");
        branches.add("AMEERPET");
        branches.add("BALANAGAR");
        ArrayList<String> services=new ArrayList<>();
        services.add("WITHDRAWAL");
        services.add("DEPOSIT");
        services.add("GOVT CHALLAN");
        services.add("DD ISSUE");
        services.add("NEFT");
        for(int i=0;i<branches.size();i++){
            DatabaseReference reference=databaseReference.child(branches.get(i));
            DatabaseReference reference1=databaseReference1.child(branches.get(i));
            for(int j=0;j<services.size();j++){
               references=reference.child(services.get(j));
               references.child("Tc").setValue("0");
               references1=reference1.child(services.get(j));
               references1.child("rc").setValue("0");
            }
        }

    }
    public void count(View view){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Branch");

    }
}
