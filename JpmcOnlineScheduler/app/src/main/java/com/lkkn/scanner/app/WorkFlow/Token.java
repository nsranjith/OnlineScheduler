package com.lkkn.scanner.app.WorkFlow;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lkkn.scanner.app.MapActivity;
import com.lkkn.scanner.app.R;
import com.lkkn.scanner.app.Util.JpmcToast;
import com.lkkn.scanner.app.Util.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;

import static java.lang.Math.ceil;

/**
 * Created by RANJITH on 13-03-2018.
 */

public class Token extends Fragment {
    String service,branch;
    TextView total_count,running_count,e_token;
    SharedPreferences.Editor editor;
    String keys,cnt="0";
    private int flag=0;
    String total="0",running="0";
    DataSnapshot dataSnapshots = null;
    private View view;
    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((Servicess) getActivity())
                .setTitle("Generate Your Token");
        view = inflater.inflate(R.layout.activity_token, container, false);
        total_count=(TextView)view.findViewById(R.id.tc);
        running_count=(TextView)view.findViewById(R.id.rc);
        e_token=(TextView)view.findViewById(R.id.token);
        service=getArguments().getString("service");
        branch=getArguments().getString("branch");
        final Button navigate=(Button)view.findViewById(R.id.navigate);
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate();
            }
        });
        final Button generate=(Button)view.findViewById(R.id.generate);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generate.setEnabled(false);
                generate();
            }
        });
        SharedPreferences shared=getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=shared.edit();
        editor.putString(branch,"no");
        Log.d("Generate",service+"");
        Log.d("Generate",branch+"");
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Branch").child(getArguments().getString("branch")).child(getArguments().getString("service")).child("Tc");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Map<String,String> map=( Map<String,String>)dataSnapshot.getValue();


                if(!dataSnapshot.getValue().equals(null)) {
                    total = dataSnapshot.getValue() + "";

                    total_count.setText(dataSnapshot.getValue() + "");
                }
                SharedPreferences shared=getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=shared.edit();
                editor.putString("TC",total);
                editor.commit();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Running").child(getArguments().getString("branch")).child(getArguments().getString("service")).child("rc");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getValue().equals(null)) {

                    running = dataSnapshot.getValue() + "";

                    running_count.setText(dataSnapshot.getValue() + "");
                }
                SharedPreferences shared=getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=shared.edit();
                editor.putString("RC",running);
                editor.commit();
                Log.d("total",shared.getString("TC",""));
                Log.d("running",shared.getString("RC",""));
                TextView time=(TextView)view.findViewById(R.id.time);
                float minutes= (float) ((((Float.parseFloat(shared.getString("TC","")))-(Float.parseFloat(shared.getString("RC",""))))*10.0)/60.0);
                if(minutes>=1)
                {
                    time.setText(ceil(minutes)+"hr");
                }

                else
                {
                    time.setText((int)(((Float.parseFloat(shared.getString("TC","")))-(Float.parseFloat(shared.getString("RC","")))))*10+"min");
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d("reyyyy",shared.getString("TC","")+"nice"+shared.getString("RC",""));
        Log.d("kn",shared.getString("key",""));
        Log.d("to",shared.getString("Token",""));
        //checkForClearance();
        checkForClearance();
        return view;
    }
    public void generate(){
        TextView textView=(TextView)getActivity().findViewById(R.id.t_token);
        textView.setVisibility(View.VISIBLE);
        SharedPreferences shared=getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        if(flag==1){
            Toast.makeText(getActivity(),"You have already generated a valid Token..",Toast.LENGTH_LONG).show();
            retrieve();
            return;
        }

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Token").child(branch).child(service).push();

        rootRef.child("fcm").setValue(shared.getString("fcm",""));
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
                final DatabaseReference uniqref= rootRef.
                        child(dataSnapshot1.getKey());
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
                        SharedPreferences shared=getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=shared.edit();
                        editor.putString("count",k+"");
                        //editor.putString("Token","yes");
                        //editor.putString(branch,"yes");
                        keys=dataSnapshot1.getKey();
                        editor.commit();
                        rootRef.child("Key").setValue(dataSnapshot1.getKey());
                        rootRef.child("e-token").setValue(dataSnapshot1.getKey().substring(1, 7));
                        e_token.setText(dataSnapshot1.getKey().substring(1, 7));
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

                e_token.setText(dataSnapshots.getKey().substring(1, 7));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void checkForClearance() {

        if (Utils.hasActiveInternetConnection(getActivity())) {
            progressDialog = Utils.showLoadingDialog(getActivity(), false);
            final SharedPreferences shared = getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            com.google.firebase.database.Query query = reference.child("Token").child(branch).child(service).orderByChild("Email").equalTo(shared.getString("user", ""));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Iterator it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            dataSnapshots = (DataSnapshot) it.next();
                            Log.d("keyss", dataSnapshots.getKey());

                        }
                        final DatabaseReference keyRef = FirebaseDatabase.getInstance().getReference().child("Token").child(branch).child(service).
                                child(dataSnapshots.getKey());

                        getCount(dataSnapshots.getKey());

                        DatabaseReference running = FirebaseDatabase.getInstance().getReference().child("Running").child(branch).
                                child(service).child("rc");

                        running.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("run", dataSnapshot.getValue() + "");
//                                Log.d("runs", cnt);
                                Log.d("Key", shared.getString("Key", ""));
                                if (cnt.equals(null))
                                {
                                    cnt="0";
                                }

                                if (Integer.parseInt(dataSnapshot.getValue() + "") > (Integer.parseInt(cnt) + 15)) {
                                    keyRef.child("Email").setValue("");
                                    SharedPreferences shared = getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = shared.edit();
                                    editor.putString("count", "0");
                                    editor.putString("Token", "");
                                    editor.putString(branch, "no");
                                    editor.commit();
                                    progressDialog.cancel();

                                } else {
                                    progressDialog.cancel();
                                    flag = 1;


                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    progressDialog.cancel();


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else
        {
            progressDialog.cancel();
            JpmcToast.create(getActivity(), R.drawable.ic_error_outline_black_24dp, "No Internet\nPlease try again", Toast.LENGTH_SHORT);

        }
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InputMethodManager inputManager = (InputMethodManager) this
                .getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = this.getActivity().getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        getActivity().setTitle("Generate Your Token");
    }
  public void navigate(){
      android.support.v4.app.Fragment fragment;
      fragment= new MapActivity();
      if (fragment!=null){
          android.support.v4.app.FragmentTransaction ft=getFragmentManager().beginTransaction();
          ft.replace(R.id.content_main,fragment,"map");
          ft.addToBackStack("map");
          Bundle bundle = new Bundle();
          bundle.putString("destination", getArguments().getString("branch"));
          fragment.setArguments(bundle);
          ft.commit();

      }



        }
}
