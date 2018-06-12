package com.lkkn.scanner.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lkkn.scanner.app.Util.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class TokensList extends Fragment {
    String path;
    DatabaseReference databaseReference;
    DatabaseReference references, finalReferences;
    DatabaseReference reference;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;
    private Query query;
    private ArrayList<String> arrayList;
    private ArrayList<String> services;
    private String branch="select your branch";
    private String service="select your service";
    Spinner spinner,spinner_services;
    private int backscreen;
    private int sign, flag;
    CardView cardView;
    private ProgressDialog progressDialog;

    public TokensList() {
        // Required empty public constructor
    }

    public static TokensList newInstance(String param1, String param2) {
        TokensList fragment = new TokensList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tokens_list, container, false);
        arrayList = new ArrayList<String>();
        services = new ArrayList<String>();
        cardView = (CardView) view.findViewById(R.id.card);
        addItems();
        addServices();
        spinner = (Spinner) view.findViewById(R.id.spinner_branch);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, arrayList);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                branch = arrayAdapter.getItem(i);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_services = (Spinner) view.findViewById(R.id.spinner_services);
        final ArrayAdapter<String> services_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, services);
        spinner_services.setAdapter(services_adapter);
        spinner_services.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                service = services_adapter.getItem(i);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        final Button move = (Button) view.findViewById(R.id.Go);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if ((!service.equalsIgnoreCase("SELECT YOUR SERVICE")) && (!branch.equalsIgnoreCase("SELECT YOUR BRANCH"))) {


                        cardView.setVisibility(View.INVISIBLE);
                        progressDialog = Utils.showLoadingDialog(getActivity(), false);
                        Get();
                    } else {
                        Toast.makeText(getActivity(), "Invalid Choice", Toast.LENGTH_LONG).show();
                    }

//                else
//                {
//                    Toast.makeText(getActivity(), "Select an option", Toast.LENGTH_LONG).show();
//
//                }


            }
        });


        return view;

    }

    private void addServices() {
        services.add("Select Your Branch");
        services.add("WITHDRAWAL");
        services.add("DEPOSIT");
        services.add("GOVT CHALLAN");
        services.add("DD ISSUE");
        services.add("NEFT");
    }

    private void addItems() {
        arrayList.add("Select Your Service");
        arrayList.add("JNTU");
        arrayList.add("JEEDIMETLA");
        arrayList.add("SECUNDERABAD");
        arrayList.add("UPPAL");
        arrayList.add("KUKATPALLY");
        arrayList.add("BACHUPALLY");
        arrayList.add("AMEERPET");
        arrayList.add("BALANAGAR");
    }

    public void Get() {
//        if (spinner_services.isSelected() && spinner.isSelected()) {
//
//                if (!(branch.equals("Select Your Branch")) && ((service.equals("Select Your Service")))) {
//

                    sign = 0;
                    flag = 0;
//        Log.d("bs",service+""+branch);
//        android.support.v4.app.Fragment fragment;
//        fragment= new Token();
//        if (fragment!=null){
//            android.support.v4.app.FragmentTransaction ft=getFragmentManager().beginTransaction();
//            backscreen=2;
//            Toast.makeText(getActivity(),"frag"+backscreen,Toast.LENGTH_LONG).show();
//            ft.replace(R.id.content_main,fragment,"List");
//            ft.addToBackStack("List");
//            Bundle bundle = new Bundle();
//            bundle.putString("branch", branch);
//            bundle.putString("service", service);
//            fragment.setArguments(bundle);
//            ft.commit();
//
//        }
                    final SharedPreferences shared = getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
                    reference = FirebaseDatabase.getInstance().getReference().child("Token").child(branch).child(service);
                    query = reference.orderByChild("Email").equalTo(shared.getString("user", ""));
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                                path = nodeDataSnapshot.getKey();
                                Log.d("pass", path);
                                if (path != null) {
                                    //sign=1;
                                    listTokens();
                                }

                            } else {
                                progressDialog.cancel();
                                Toast.makeText(getActivity(), "Invalid", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


//                } else {
//                    Toast.makeText(getActivity(), "Invalid Choice", Toast.LENGTH_LONG).show();
//                }
     //       }


    }

    private void listTokens() {
        progressDialog.cancel();

        cardView.setVisibility(View.VISIBLE);
        final TextView t_branch=(TextView)view.findViewById(R.id.branch);
        final TextView t_service=(TextView)view.findViewById(R.id.service);
        final TextView t_count=(TextView)view.findViewById(R.id.count);
        final TextView t_etoken=(TextView)view.findViewById(R.id.etoken);
        reference.child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String,String> map=( Map<String,String>)dataSnapshot.getValue();
                t_branch.setText(branch);
                t_service.setText(service);
                t_count.setText(map.get("Count"));
                t_etoken.setText(map.get("e-token"));
                Log.d("etoken",map.get("Count")+map.get("e-token"));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


        @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Know Your token");
    }







}
