package com.example.jd_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner1=findViewById(R.id.spinner);
        List<String> categories = new ArrayList<>();
        categories.add(0,"Choose the Category to Count");
        categories.add("Dormitory");
        categories.add("Ship Area");
        //categories.add("Plot Area");

        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Choose the Category to Count")){
                }
                else{
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: "+item, Toast.LENGTH_SHORT).show();

                    if(parent.getItemAtPosition(position).equals("Dormitory")){
                        Intent intent = new Intent(MainActivity.this, Dormitory.class);
                        startActivity(intent);
                    }
                    if(parent.getItemAtPosition(position).equals("Ship Area")){
                        Intent intent = new Intent(MainActivity.this, Ship_Area.class);
                        startActivity(intent);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
    public void qr_generator(View view){
        startActivity(new Intent(getApplicationContext(),QR_Generator.class));
    }
}