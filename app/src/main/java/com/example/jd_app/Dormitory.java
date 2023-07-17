package com.example.jd_app;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dormitory extends AppCompatActivity {

    Button back,alert;
    public static TextView counter;
    Button IN_btn, OUT_btn;
    public static int temp_counter;
    FirebaseDatabase database;
    DatabaseReference ref;
    private int count;
    public static int check_sms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        displayCount();
        super.onCreate(savedInstanceState);
//        displayCount();
        setContentView(R.layout.activity_dormitory);
        IN_btn = findViewById(R.id.In_Button);
        OUT_btn = findViewById(R.id.Out_Button);
        counter = (findViewById(R.id.total_Counter));


        alert = findViewById(R.id.alert_dormitory);

        back = findViewById(R.id.Back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dormitory.this,MainActivity.class);
                startActivity(intent);

            }
        });

        IN_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dormitory.this,ScannerIN.class);
                startActivity(intent);
            }
        });

        OUT_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dormitory.this,ScannerOut.class);
                startActivity(intent);
            }
        });

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ref = database.getInstance().getReference().child("Dormitory");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        AlertDialog.Builder confirmAlert = new AlertDialog.Builder(Dormitory.this);
                        confirmAlert.setMessage("Are you sure to send Alert Messages !!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        int temp = 0;
                                        for(DataSnapshot ds: snapshot.getChildren()){
                                            temp++;
                                            String Number = ds.getKey();
                                            String Message_Gujarati = "ચેતવણી ! \nતરત જ પ્લોટ વિસ્તાર ખાલી કરો! ";
                                            String Message_Hindi = "चेतावनी!\nप्लाट क्षेत्र को तुरंत खाली करें!";
                                            String Message = "Emergency ! \nVacate the Plot Area Immediately !";

                                            SmsManager smsDormitory = SmsManager.getDefault();

                                            smsDormitory.sendTextMessage(Number,null,Message_Gujarati,null,null);
                                            smsDormitory.sendTextMessage(Number,null,Message_Hindi,null,null);
                                            smsDormitory.sendTextMessage(Number, null, Message, null, null);

                                        }

                                        Toast.makeText(Dormitory.this, "SMS Sent Successfully to " +temp+" users in Plot Area", Toast.LENGTH_LONG).show();

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


    }
    private void displayCount(){
        ref = database.getInstance().getReference().child("Dormitory");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = (int) snapshot.getChildrenCount();
                Dormitory.temp_counter = count;
                int a = Dormitory.temp_counter;
                Dormitory.counter.setText(Integer.toString(a));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}