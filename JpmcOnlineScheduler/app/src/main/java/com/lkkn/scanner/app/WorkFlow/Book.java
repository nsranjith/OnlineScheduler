package com.lkkn.scanner.app.WorkFlow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.lkkn.scanner.app.R;

import java.util.ArrayList;

public class Book extends AppCompatActivity {

    String service,var;
    ProgressDialog progressDialog;
    ListView lv;
    SearchView sv;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        //getActionBar().setTitle("Select Branches");
        //getSupportActionBar().setTitle("Select Branches");

        Intent i=getIntent();
        service=i.getStringExtra("service");
        arrayList=new ArrayList<String>();
        addItems();
        Log.d("se",service);
        progressDialog=new ProgressDialog(this);
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

    }

    private void selection() {
        lv=(ListView) findViewById(R.id.listView1);
        sv=(SearchView) findViewById(R.id.searchView1);
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arrayList);
        lv.setAdapter(adapter);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                // TODO Auto-generated method stub
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
                ArrayAdapter<String> adapter1=new ArrayAdapter<String>(Book.this, android.R.layout.simple_list_item_1,arrayList1);
                lv.setAdapter(adapter1);
                adapter.getFilter().filter(text);
                return true;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(Book.this,Generate.class);
                intent.putExtra("branch",lv.getItemAtPosition(i).toString());
                intent.putExtra("service",service);
                startActivity(intent);


            }
        });
    }

    private void addItems() {
        arrayList.add("SELECT BRANCH");
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

        Intent intent=new Intent(this,Generate.class);
        intent.putExtra("branch",var);
        intent.putExtra("service",service);
        startActivity(intent);

    }


    




}
