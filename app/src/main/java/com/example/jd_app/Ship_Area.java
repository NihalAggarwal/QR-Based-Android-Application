package com.example.jd_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Ship_Area extends AppCompatActivity {
    Button back,alert;
    public static TextView counter;
    Button IN_btn, OUT_btn;
    public static int temp_counter;
    FirebaseDatabase database;
    DatabaseReference ref;
    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        displayCount();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_area);
        IN_btn = findViewById(R.id.In_Button);
        OUT_btn = findViewById(R.id.Out_Button);
        counter = (findViewById(R.id.total_Counter));

        alert = findViewById(R.id.alert_shiparea);

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ref = database.getInstance().getReference().child("Ship_Area");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        AlertDialog.Builder confirmAlert = new AlertDialog.Builder(Ship_Area.this);
                        confirmAlert.setMessage("Are you sure to send Alert Messages !!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        for(DataSnapshot ds: snapshot.getChildren()){
                                            String Number = ds.getKey();
                                            String Message_Gujarati = "ચેતવણી ! \nતરત જ ડોરમીટોરી વિસ્તાર ખાલી કરો! ";
                                            String Message_Hindi = "चेतावनी!\nडारमेट्री क्षेत्र को तुरंत खाली करें!";
                                            String Message = "Emergency ! \nVacate the Dormitory Area Immediately !";

                                            SmsManager smsDormitory = SmsManager.getDefault();

                                            smsDormitory.sendTextMessage(Number,null,Message_Gujarati,null,null);
                                            smsDormitory.sendTextMessage(Number,null,Message_Hindi,null,null);
                                            smsDormitory.sendTextMessage(Number, null, Message, null, null);
                                        }

                                    }
                                })
                                .setNegativeButton("No",null);
                        AlertDialog alert = confirmAlert.create();
                        alert.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });



        back = findViewById(R.id.Back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ship_Area.this,MainActivity.class);
                startActivity(intent);

            }
        });

        IN_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ship_Area.this,Scanner_In_Ship_Area.class);
                startActivity(intent);
            }
        });

        OUT_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ship_Area.this,Scanner_Out_Ship_Area.class);
                startActivity(intent);
            }
        });
    }
    private void displayCount(){
        ref = database.getInstance().getReference().child("Ship_Area");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = (int) snapshot.getChildrenCount();
                Ship_Area.temp_counter = count;
                int a = Ship_Area.temp_counter;
                Ship_Area.counter.setText(Integer.toString(a));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}