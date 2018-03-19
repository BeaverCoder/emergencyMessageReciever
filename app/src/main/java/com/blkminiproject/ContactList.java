package com.blkminiproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ContactList extends AppCompatActivity {

    private ListView clist;
    private String names="";
    private String phone="";
    private String contact="";
    private String nameArray[];
    private String phoneArray[];
    private String contactInfo[];
    private String EmergencyNumber;
    private String EmergencyName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        clist = findViewById(R.id.contactList);

        Cursor contactCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");

        Set<List<String>> contactSet = new LinkedHashSet<>();
        while(contactCursor.moveToNext()){
            List<String> c = new ArrayList<String>();
            String displayName = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String displayNumber = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            if(displayName!=null && displayNumber!=null){
                displayNumber=displayNumber.replace(" ","");
                displayNumber=displayNumber.replace("-","");

                c.add(displayName);
                c.add(displayNumber);
                contactSet.add(c);
            }
        }
        List<List<String>> contlist = new ArrayList<>(contactSet);

        for(int i=0; i<contlist.size();i++){
            names += contlist.get(i).get(0) +",";
            phone += contlist.get(i).get(1)+",";
            contact+=contlist.get(i).get(0)+": "+contlist.get(i).get(1)+",";
        }

        contactCursor.close();

        nameArray=names.split(",");
        phoneArray=phone.split(",");
        contactInfo=contact.split(",");

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,contactInfo);
        clist.setAdapter(stringArrayAdapter);

        clist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String phoneNumber = phoneArray[i];
                String name = nameArray[i];
                //Toast.makeText(getApplicationContext(),name+" "+phoneNumber,Toast.LENGTH_SHORT).show();

                alertBuilder(name,phoneNumber);

            }
        });

    }

    private void alertBuilder(final String name, final String number){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?").setMessage(name+": "+number).setIcon(android.R.drawable.ic_dialog_info);



        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(ContactList.this, "Okay", Toast.LENGTH_SHORT).show();
                EmergencyNumber = number;
                EmergencyName=name;
                saveAsEmergency(getApplicationContext());
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();

    }

    private void saveAsEmergency(Context context){
        SharedPreferences preferences = getSharedPreferences("emergencyNumber", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Number",EmergencyNumber);
        editor.putString("Name",EmergencyName);
        editor.apply();

        Toast.makeText(context, EmergencyNumber+" saved as Emergency Number", Toast.LENGTH_SHORT).show();

    }

}
