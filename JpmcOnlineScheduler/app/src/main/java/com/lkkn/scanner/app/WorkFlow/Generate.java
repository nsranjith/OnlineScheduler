package com.lkkn.scanner.app.WorkFlow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lkkn.scanner.app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;

public class Generate extends AppCompatActivity {
    String service,branch;
    TextView total_count,running_count,e_token;
    SharedPreferences.Editor editor;
    String keys,cnt;
    private int flag=0;
    DataSnapshot dataSnapshots = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
       // getSupportActionBar().setTitle("Generate Token");
        total_count=(TextView)findViewById(R.id.tc);
        running_count=(TextView)findViewById(R.id.rc);
        e_token=(TextView)findViewById(R.id.token);
        Intent intent=getIntent();
        service=intent.getStringExtra("service");
        branch=intent.getStringExtra("branch");
        SharedPreferences shared=getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=shared.edit();
        editor.putString(branch,"no");
        Log.d("Generate",service+"");
        Log.d("Generate",branch+"");
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Branch").child(branch).child(service).child("Tc");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total_count.setText("Total Count: "+dataSnapshot.getValue()+"");


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Running").child(branch).child(service).child("rc");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                running_count.setText("Running Count: "+dataSnapshot.getValue()+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d("kn",shared.getString("key",""));
        Log.d("to",shared.getString("Token",""));
        //checkForClearance();
        checkForClearance();


    }
    public void generate(View view){
        SharedPreferences shared=getSharedPreferences("loginData", Context.MODE_PRIVATE);
        if(flag==1){
            Toast.makeText(getApplicationContext(),"You have already generated a valid Token..",Toast.LENGTH_LONG).show();
            retrieve();
            return;
        }
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Token").child(branch).child(service).push();


        final DatabaseReference ref=rootRef.child("Email");
        //shared.getString("Mobile","")
        ref.setValue(shared.getString("user",""));
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference reference1=reference.child("Branch").child(branch).child(service);
        com.google.firebase.database.Query query = reference.child("Token").child(branch).child(service).orderByChild("Email").equalTo(shared.getString("user",""));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               Iterator it = dataSnapshot.getChildren().iterator();
               //String key = nodeDataSnapshot.getKey(); // this ke

               DataSnapshot dataSnapshot1 = null;
               while(it.hasNext()) {
                   dataSnapshot1= (DataSnapshot) it.next();

               }
        final DatabaseReference uniqref= rootRef.child(dataSnapshot1.getKey());
               uniqref.setValue(dataSnapshot1.getKey());
               com.google.firebase.database.Query uquery = reference.child("Token").child(branch).child(service).orderByChild(dataSnapshot1.getKey());

              uquery.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       Iterator it = dataSnapshot.getChildren().iterator();
                       //String key = nodeDataSnapshot.getKey(); // this ke
                       int k=0;
                       DataSnapshot dataSnapshot1 = null;
                       while(it.hasNext()) {
                           dataSnapshot1= (DataSnapshot) it.next();
                           Log.d("key",dataSnapshot1.getKey() +""+k);
                           k++;
                       }
                       Log.d("lucky",dataSnapshot1.getKey() +""+k);
                       SharedPreferences shared=getSharedPreferences("loginData", Context.MODE_PRIVATE);
                       SharedPreferences.Editor editor=shared.edit();
                       editor.putString("count",k+"");
                       //editor.putString("Token","yes");
                       //editor.putString(branch,"yes");
                       keys=dataSnapshot1.getKey();
                       editor.commit();
                       rootRef.child("Key").setValue(dataSnapshot1.getKey());
                       rootRef.child("e-token").setValue(dataSnapshot1.getKey().substring(1, 7));
                       e_token.setText("Your e-token is: "+dataSnapshot1.getKey().substring(1, 7));
                       rootRef.child("Count").setValue(k+"");
                       HashMap<String, Object> result = new HashMap<>();
                       result.put("Tc",k+"");
                       reference1.updateChildren(result);

                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });







//               for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                   Log.d("val", snapshot.getChildren()+ "");
//                   if (snapshot.getKey().equals(key)) {
//                       ref.setValue(key.substring(0, 6));
//                       rootRef.child("C-count").setValue(c[0]);
//                       reference1.setValue(c[0]);
//                       c[0] = 0;
//                       break;
//
//
//                   }

                   //c[0]++;
                  // snapshot.getKey();


           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });




    }

    private void retrieve() {

        final DatabaseReference keyRef=FirebaseDatabase.getInstance().getReference().child("Token").child(branch).child(service).
                child(dataSnapshots.getKey()).child("e-token");
        keyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                e_token.setText("Your e-token is: "+dataSnapshots.getKey().substring(1, 7));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void checkForClearance() {
        final SharedPreferences shared=getSharedPreferences("loginData", Context.MODE_PRIVATE);
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        com.google.firebase.database.Query query = reference.child("Token").child(branch).child(service).orderByChild("Email").equalTo(shared.getString("user",""));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterator it = dataSnapshot.getChildren().iterator();
                    while(it.hasNext()) {
                        dataSnapshots= (DataSnapshot) it.next();
                        Log.d("keyss",dataSnapshots.getKey() );

                    }
                    final DatabaseReference keyRef=FirebaseDatabase.getInstance().getReference().child("Token").child(branch).child(service).
                            child(dataSnapshots.getKey());

                    getCount(dataSnapshots.getKey());
                    DatabaseReference running = FirebaseDatabase.getInstance().getReference().child("Running").child(branch).
                            child(service).child("rc");

                    running.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("run", dataSnapshot.getValue() + "");
                            Log.d("runs", cnt);
                            Log.d("Key", shared.getString("Key", ""));

                            if (Integer.parseInt(dataSnapshot.getValue()+"") > Integer.parseInt(cnt)) {
                                keyRef.child("Email").setValue("");
                                SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString("count", "0");
                                editor.putString("Token", "");
                                editor.putString(branch, "no");
                                editor.commit();


                            }
                            else
                            {
                                          flag=1;


                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getCount(String key) {
        final DatabaseReference keyRef=FirebaseDatabase.getInstance().getReference().child("Token").child(branch).child(service).
                child(key).child("Count");
       Log.d("pah",keyRef+"");
        keyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cnt=dataSnapshot.getValue()+"";


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void navigate(View view){

        Intent intent=new Intent(Generate.this,MapsActivity.class);
        intent.putExtra("Destination",branch);
        startActivity(intent);

    }
}
