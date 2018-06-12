package com.lkkn.scanner.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lkkn.scanner.app.Util.Utils;
import com.lkkn.scanner.app.WorkFlow.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Bio extends Fragment{
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private ProgressDialog progressDialog;
    private Query query;
    private String path;
    private String mail;

    public Bio() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Bio.
     */
    // TODO: Rename and change types and number of parameters
    public static Bio newInstance(String param1, String param2) {
        Bio fragment = new Bio();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bio, container, false);
        Button update=(Button)view.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();

            }
        });



        return view;
    }

    private void update() {
        progressDialog = Utils.showLoadingDialog(getActivity(), false);
        EditText email = (EditText) view.findViewById(R.id.email);
        mail=email.getText().toString();
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email.getText().toString());
        if (matcher.find()) {
            emailVerification();
            updateDetails();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.updateEmail(email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.cancel();
                                // Log.d(TAG, "User email address updated.");
                                SharedPreferences shared = getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = shared.edit();

                                editor.putString("user","");editor.putString("visited","none");
                                editor.putString("mvisited","no");
                                editor.putString("activity","no");
                                editor.putString("image","null");
                                editor.commit();
                                Toast.makeText(getActivity(), "Updated succesfully.. Please Login again", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), SignIn.class));


                            }
                        }
                    });
        } else {
            progressDialog.cancel();
            Toast.makeText(getActivity(), "Invalid email id", Toast.LENGTH_SHORT).show();

        }
    }

    private void updateDetails() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Customer").push();
        reference.child("Email").setValue(mail);
        reference.child("Verified").setValue("True");
//        final SharedPreferences shared = getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Customer");
//        query = reference.orderByChild("Email").equalTo(shared.getString("user", ""));
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
//                    path = nodeDataSnapshot.getKey();
//                    Log.d("pass", path);
//                    if (path != null) {
//                       Notify();
//                    }
//
//                }
//                else {
//                    progressDialog.cancel();
//                    Toast.makeText(getActivity(), "You haven;t generated a token..", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                progressDialog.cancel();
//
//            }
//        });

    }

    private void Notify() {
        final DatabaseReference keyRef=FirebaseDatabase.getInstance().getReference().child("Customer").child(path).child("Email");
        keyRef.setValue(mail);
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
                            Toast.makeText(getActivity(),"Email verification link has been sent",Toast.LENGTH_LONG).show();
                        }
                        else
                        {

                            progressDialog.cancel();
                            Toast.makeText(getActivity(),"Email verification link sending failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }


    //    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }





    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Update Info");
    }
}
