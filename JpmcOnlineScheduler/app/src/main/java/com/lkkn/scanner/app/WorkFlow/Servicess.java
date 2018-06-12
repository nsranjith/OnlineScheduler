package com.lkkn.scanner.app.WorkFlow;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.lkkn.scanner.app.Bio;
import com.lkkn.scanner.app.Branches;
import com.lkkn.scanner.app.ChatNode;
import com.lkkn.scanner.app.R;
import com.lkkn.scanner.app.TokensList;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Servicess extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView lv;
    SearchView sv;
    ArrayAdapter<String> adapter;
    GoogleApiClient googleApiClient;
    GoogleSignInOptions gso;
    ArrayList<String> arrayList;
    private String var;
    private ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private NavigationView navigationView;
    private View header;
    private SharedPreferences shared;
    public int backscreen=0;
    private String key;
    private DataSnapshot dataSnapshots;
    private String token;
    private Context context=this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        token=FirebaseInstanceId.getInstance().getToken();
        super.onCreate(savedInstanceState);
        store();
        setContentView(R.layout.activity_servicess);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        Log.d("Mine",shared.getString("user",""));
        if (!shared.getString("entered","").equals("yes"))
        {
            tap();
        }

        // check if no view has focus:


        //Toast.makeText(getApplicationContext(),shared.getString("user",""),Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        authStateListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(mAuth.getCurrentUser()==null){
                    startActivity(new Intent(Servicess.this,SignIn.class));

                }

            }
        };
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
                        //Toast.makeText(getApplicationContext(),"ërror",Toast.LENGTH_LONG).show();

                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(getApplicationContext(), ChatNode.class));


            }


       });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header=navigationView.getHeaderView(0);
        TextView email = (TextView)header.findViewById(R.id.textView);
        email.setText(shared.getString("user",""));
        setIcon();
        arrayList=new ArrayList<String>();

        addItems();
        selection();
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);
//        // Creating ArrayAdapter using the string array and default spinner layout
//        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arrayList);
//        // Specify layout to be used when list of choices appears
//
//        // Applying the adapter to our spinner
//        spinner.setAdapter(arrayAdapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                var=arrayAdapter.getItem(i);
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

    }

    private void tap() {
        SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("entered", "yes");
        editor.commit();
        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.fab), "Click here for Queries ", "Chat bot which responds with answers ")
                        .tintTarget(true)
                        .outerCircleColor(R.color.outer)
                        .targetCircleColor(android.R.color.white)
                        .transparentTarget(true)
                        .outerCircleAlpha(0.96f));
    }

    private void store() {
        SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        final DatabaseReference token_reference=FirebaseDatabase.getInstance().getReference();
        com.google.firebase.database.Query s_query = token_reference.child("Customer").orderByChild("Email").equalTo(shared.getString("user",""));
        s_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                key = nodeDataSnapshot.getKey();
                Log.d("hi",key+"h");
                token_reference.child("Customer").child(key).child("Token").setValue(token);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("hi","entered");

            }
        });


    }

    private void setIcon() {
        ImageView icon;
        icon = (ImageView) header.findViewById(R.id.profile_image);
        if (!shared.getString("image", "").equals("")) {
            Picasso.with(this).load(shared.getString("image","")).into(icon);
        }
        else {
            icon.setImageResource(R.drawable.image);

        }
        SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("fcm", token);
        editor.commit();

    }



    private void selection() {
        lv=(ListView) findViewById(R.id.listView1);
        sv=(SearchView)findViewById(R.id.searchView1);

        sv.setIconified(false);
        sv.setQueryHint("Select Services");
       /// sv.onActionViewExpanded();
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arrayList);
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
                ArrayAdapter<String> adapter1=new ArrayAdapter<String>(Servicess.this, android.R.layout.simple_list_item_1,arrayList1);
                lv.setAdapter(adapter1);
                adapter.getFilter().filter(text);
                return true;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                Intent intent=new Intent(Servicess.this,Book.class);
//                intent.putExtra("service",lv.getItemAtPosition(i).toString());
//                startActivity(intent);
                display(lv.getItemAtPosition(i).toString());


            }
        });

    }

    private void display(String service) {

        android.support.v4.app.Fragment fragment;
        fragment= new Branches();
        if (fragment!=null){
            android.support.v4.app.FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            backscreen=1;
            //Toast.makeText(this,getFragmentManager().getBackStackEntryCount(),Toast.LENGTH_LONG).show();

            ft.replace(R.id.content_main,fragment,"Service");
            ft.addToBackStack("Service");
            Bundle bundle = new Bundle();
            bundle.putString("service", service);
            fragment.setArguments(bundle);
            ft.commit();

        }

    }

    private void addItems() {
        arrayList.add("WITHDRAWAL");
        arrayList.add("DEPOSIT");
        arrayList.add("GOVT CHALLAN");
        arrayList.add("DD ISSUE");
        arrayList.add("NEFT");

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.servicess, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {




            Auth.GoogleSignInApi.signOut(googleApiClient);
            SharedPreferences shared = getSharedPreferences("loginData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();

         //   Toast.makeText(getApplicationContext(),mAuth.getCurrentUser()+"nice man", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            editor.putString("user","");
            editor.putString("visited","none");
            editor.putString("mvisited","no");
            editor.putString("activity","no");
            editor.putString("image","null");
            editor.commit();

            //easy
            startActivity(new Intent(Servicess.this,SignIn.class));



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.update) {
            android.support.v4.app.Fragment update;
            update = new Bio();
            if (update != null) {
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                backscreen = 1;
                //Toast.makeText(this,getFragmentManager().getBackStackEntryCount(),Toast.LENGTH_LONG).show();

                ft.replace(R.id.content_main, update, "Bio");
                ft.addToBackStack("Bio");
                //Bundle bundle = new Bundle();
                //fragment.setArguments(bundle);
                ft.commit();
            }

            // Handle the camera action
        } else if (id == R.id.token) {
            android.support.v4.app.Fragment fragment;
            fragment = new TokensList();
            if (fragment != null) {
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                backscreen = 1;
                //Toast.makeText(this,getFragmentManager().getBackStackEntryCount(),Toast.LENGTH_LONG).show();

                ft.replace(R.id.content_main, fragment, "Select");
                ft.addToBackStack("Select");
                //Bundle bundle = new Bundle();
                //fragment.setArguments(bundle);
                ft.commit();

            }


        }
        else if (id == R.id.service) {
            startActivity(new Intent(getApplicationContext(), Servicess.class));

        }
        else if (id==R.id.chat){
            startActivity(new Intent(getApplicationContext(),ChatNode.class));
        }
//        else if (id==R.id.reschedule) {
//            android.support.v4.app.Fragment fragment;
//            fragment = new Refresh();
//            if (fragment != null) {
//                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                backscreen = 1;
//                //Toast.makeText(this,getFragmentManager().getBackStackEntryCount(),Toast.LENGTH_LONG).show();
//
//                ft.replace(R.id.content_main, fragment, "Select");
//                ft.addToBackStack("Select");
//                //Bundle bundle = new Bundle();
//                //fragment.setArguments(bundle);
//                ft.commit();;
//
//        }


        //} else if (id == R.id.Path) {
//                android.support.v4.app.Fragment fragment;
//                fragment = new MapActivity();
//                if (fragment != null) {
//                    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    backscreen = 1;
//                    //Toast.makeText(this,getFragmentManager().getBackStackEntryCount(),Toast.LENGTH_LONG).show();
//
//                    ft.replace(R.id.content_main, fragment, "branch");
//                    ft.addToBackStack("branch");
//                    //Bundle bundle = new Bundle();
//                    //fragment.setArguments(bundle);
//                    ft.commit();
           // }


           else if (id == R.id.nav_share) {
            final String appPackageName = context.getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Welcome to Our Online Scheduler please Click the link to download the App: https://play.google.com/store/apps");
            sendIntent.setType("text/plain");
            context.startActivity(sendIntent);
            }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        backscreen=0;

        ActionBar ab=null;
        int fragmentCount = getSupportFragmentManager().getBackStackEntryCount();
        if (fragmentCount != 0)
        {
            android.support.v4.app.FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(fragmentCount - 1);
            String fragmentTag = backEntry.getName();


            if (fragmentTag != null)
            {
                // If back pressed when on Quiz
                switch (fragmentTag){
                    case "Select":
                        backscreen=3;
                       // this.setTitle("Select Service");
                        break;
                    case "Branch":
                        backscreen=3;
                      /// this.setTitle("Select Service");
                        break;
                    case "Service":
                        backscreen=3;
                       // this.setTitle("Select Service");
                        break;
                    case "Bio":
                        backscreen=3;
                       // this.setTitle("Select Service");
                        break;
                    case "map":
                        backscreen=3;
                        //this.setTitle("Select Service");
                        break;

                }

            }

        }

        if(backscreen==3) {
            Toast.makeText(getApplicationContext(),"Choose your option from Navigation bar",Toast.LENGTH_LONG).show();

            //super.onBackPressed();
        }
        else
        {

            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }


//        Toast.makeText(this,backscreen+"",Toast.LENGTH_LONG).show();
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//            Toast.makeText(getApplicationContext(),"Hello",Toast.LENGTH_LONG).show();
//        }
//        if(backscreen==1){
//
//            super.onBackPressed();
//
//        }
//        else
//        {
//            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
//            homeIntent.addCategory( Intent.CATEGORY_HOME );
//            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(homeIntent);
//        }






    }
    public void submit(View view){
//        SharedPreferences shared=getSharedPreferences("Service", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor=shared.edit();
//        editor.putString("Service",var);
//        editor.commit();

        //startActivity(new Intent(this,Book.class));
//        Intent intent=new Intent(this,Book.class);
//        intent.putExtra("service",var);
//        startActivity(intent);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference rootRefs = FirebaseDatabase.getInstance().getReference().child(var);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //Log.d("p",snapshot.getKey()+"");
                if (snapshot.hasChild(var)) {

//                    Toast.makeText(Servicess.this, "hijhhhjhj", Toast.LENGTH_LONG).show();
//                    System.out.println("yes");
//                    Log.d("tag","yes");
                    rootRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("withdrawal")) {

                              //  Toast.makeText(Servicess.this, "yijhhhjhj", Toast.LENGTH_LONG).show();
                                System.out.println("yes");
                                Log.d("tag", "yes");
                            }
                            else
                            {
                                Log.d("tag","no");
                                rootRefs.child("withdrawal").child("tc").setValue(1);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });




                    // run some code
                }
                else {

                    //Toast.makeText(Servicess.this, "hihjhj", Toast.LENGTH_LONG).show();
                    System.out.println("no");
                    Log.d("tag","no");
                    // run some code
                }
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

    @Override
    protected void onResume() {
        super.onResume();

        sv.clearFocus();
    }
}