package com.lkkn.scanner.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.lkkn.scanner.app.Util.JpmcToast;
import com.lkkn.scanner.app.WorkFlow.Generate;
import com.lkkn.scanner.app.WorkFlow.Servicess;
import com.lkkn.scanner.app.WorkFlow.Token;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by RANJITH on 13-03-2018.
 */

public class Branches extends android.support.v4.app.Fragment{
    String service,var;
    ProgressDialog progressDialog;
    ListView lv;
    SearchView sv;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    private View view;
    private int backscreen=0;
    int block=1;
    private SharedPreferences shared;
    private int week;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ((Servicess) getActivity()).setTitle("Select Branch");
        Log.d("time",(System.currentTimeMillis() / 1000)+"");
        final Calendar cal=Calendar.getInstance(Locale.getDefault());
        Log.d("Date", ServerValue.TIMESTAMP+"");

        shared = getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Timestamp");
        databaseReference.child(shared.getString("user","").replace('@','1').replace('.','1'))
                .setValue(ServerValue.TIMESTAMP);
        databaseReference.child(shared.getString("user","").replace('@','1').replace('.','1'))
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getValue().equals(null)) {
                    cal.setTimeInMillis(Long.parseLong(dataSnapshot.getValue() + ""));
                    String date = DateFormat.format("hh:mm aa", cal).toString();
//                    int day = cal.get(Calendar.DAY_OF_WEEK);
//                    switch (day) {
//                        case Calendar.SUNDAY:
//                            week=0;
//                            break;
//                            // Current day is Sunday
//                        case Calendar.SATURDAY:
//                            week=0;
//                            break;
//
//                        default:
//                            week=1;
//
//                            // Current day is Monday
//                            // etc.
//                    }
//                    //date.compareTo()
                    SimpleDateFormat parser = new SimpleDateFormat("hh:mm aa");

                    Date ten, eighteen;
//                    try {
//                        ten = parser.parse("08:00 AM");
//                        eighteen = parser.parse("11:00 PM");
//                        Date userDate = parser.parse(date);
//                        if (userDate.after(ten)&& userDate.before(eighteen)) {
//                            block = 1;
//                            Log.d("supers",block+"s");
//
//
//
//                        } else {
//                            Log.d("super",block+"n");
//
//                            block = 0;
//                        }
//                    } catch (ParseException e) {
//                        // Invalid da
//                    }
                    Log.d("super",block+"");
                    Log.d("date", date);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        view = inflater.inflate(R.layout.activity_book, container, false);
        //Toast.makeText(getActivity(),getArguments().getString("service"),Toast.LENGTH_LONG).show();
        arrayList=new ArrayList<String>();
        addItems();

        progressDialog=new ProgressDialog(getActivity());
        selection();
//        Spinner spinner = (Spinner) findViewById(R.id.bspinner);
//        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arrayList);
//        spinner.setAdapter(arrayAdapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                var=arrayAdapter.getItem(i);
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        return view;
    }

    private void selection() {
        lv=(ListView) view.findViewById(R.id.listView1);
        sv=(SearchView) view.findViewById(R.id.searchView);
       sv.setIconified(false);

        sv.setQueryHint("Select Branch");
//        sv.setQueryHint("Select Branches");
//        sv.setIconified(false);
        //sv.onActionViewExpanded();
//        getActivity().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,arrayList);
        lv.setAdapter(adapter);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                // TODO Auto-generated method stub
                sv.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {

                ArrayList<String> arrayList1=new ArrayList<String>();
                for (String items:arrayList){
                    if (items.toLowerCase().contains(text.toLowerCase()))
                    {
                        arrayList1.add(items);
                    }
                }
                ArrayAdapter<String> adapter1=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,arrayList1);
                lv.setAdapter(adapter1);
                adapter.getFilter().filter(text);
                return true;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent=new Intent(getActivity(),Generate.class);
//                intent.putExtra("branch",lv.getItemAtPosition(i).toString());
//                intent.putExtra("service",getArguments().getString("service"));
//                startActivity(intent);
                android.support.v4.app.Fragment fragment;
                fragment= new Token();
                if (fragment!=null && block!=0){
                    android.support.v4.app.FragmentTransaction ft=getFragmentManager().beginTransaction();
                    backscreen=2;
                   // Toast.makeText(getActivity(),"frag"+backscreen,Toast.LENGTH_LONG).show();
                    ft.replace(R.id.content_main,fragment,"Branch");
                    ft.addToBackStack("Branch");
                    Bundle bundle = new Bundle();
                    bundle.putString("branch", lv.getItemAtPosition(i).toString());
                    bundle.putString("service", getArguments().getString("service"));
                    fragment.setArguments(bundle);
                    ft.commit();

                }
                else
                {
                    JpmcToast.create(getActivity(), R.drawable.ic_error_outline_black_24dp, "Bank is closed\nTry again Later", Toast.LENGTH_SHORT);
                }


            }
        });
    }

    private void addItems() {
        arrayList.add("JNTU");
        arrayList.add("JEEDIMETLA");
        arrayList.add("SECUNDERABAD");
        arrayList.add("UPPAL");
        arrayList.add("KUKATPALLY");
        arrayList.add("BACHUPALLY");
        arrayList.add("AMEERPET");
        arrayList.add("BALANAGAR");

    }


    public void book(View view){

        Intent intent=new Intent(getActivity(),Generate.class);
        intent.putExtra("branch",var);
        intent.putExtra("service",service);
        startActivity(intent);

    }









    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      //  getActivity().setTitle("Select Branch");


    }


}
