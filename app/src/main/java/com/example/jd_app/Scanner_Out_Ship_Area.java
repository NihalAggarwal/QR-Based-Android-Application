package com.example.jd_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;

public class Scanner_Out_Ship_Area extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    FirebaseDatabase database;
    DatabaseReference referenceout;
    DatabaseReference ref;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_out_ship_area);


        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        try {
                            String decrypt = Crypto.decrypt(result.getText());

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
                            String compare = reference.child(decrypt).get().toString();
                            System.out.println(compare);

                            if (decrypt.equals(compare)) {
                                int a = Ship_Area.temp_counter;

                                if (a >= 0) {
                                    Ship_Area.counter.setText(String.valueOf(a));
                                    Toast.makeText(Scanner_Out_Ship_Area.this, decrypt, Toast.LENGTH_SHORT).show();

                                    DatabaseReference referenceout = FirebaseDatabase.getInstance().getReference("User");

                                    referenceout.child(decrypt).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                                            DataSnapshot dataSnapshot = task.getResult();
                                            String Fullname = String.valueOf(dataSnapshot.child("fullname").getValue());
                                            String PhoneNo = String.valueOf(dataSnapshot.child("phoneno").getValue());
                                            String JoiningDate = String.valueOf(dataSnapshot.child("joiningdate").getValue());
                                            String BloodGroup = String.valueOf(dataSnapshot.child("bloodgroup").getValue());
                                            String Designation = String.valueOf(dataSnapshot.child("designation").getValue());
                                            String ESIC_No = String.valueOf(dataSnapshot.child("esicno").getValue());
                                            deleteItemToSheet(PhoneNo);
                                        }
                                    });

                                    ref = database.getInstance().getReference().child("Ship_Area");
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            count = (int) snapshot.getChildrenCount();
                                            Ship_Area.temp_counter = count;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                    finish();
                                } else {
                                    Toast.makeText(Scanner_Out_Ship_Area.this, "No People to sign out!!", Toast.LENGTH_SHORT).show();
                                }

                                Ship_Area.counter.setText(Integer.toString(a));
                                Toast.makeText(Scanner_Out_Ship_Area.this, "Scan Unsuccessful", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                int a = Ship_Area.temp_counter;


                                Toast.makeText(Scanner_Out_Ship_Area.this, "Logged Out Success!", Toast.LENGTH_SHORT).show();
                                DatabaseReference referenceout = FirebaseDatabase.getInstance().getReference("User");

                                referenceout.child(decrypt).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                                        DataSnapshot dataSnapshot = task.getResult();
                                        String Fullname = String.valueOf(dataSnapshot.child("fullname").getValue());
                                        String PhoneNo = String.valueOf(dataSnapshot.child("phoneno").getValue());
                                        String JoiningDate = String.valueOf(dataSnapshot.child("joiningdate").getValue());
                                        String BloodGroup = String.valueOf(dataSnapshot.child("bloodgroup").getValue());
                                        String Designation = String.valueOf(dataSnapshot.child("designation").getValue());
                                        String ESIC_No = String.valueOf(dataSnapshot.child("esicno").getValue());
                                        deleteItemToSheet(PhoneNo);

                                        ref = database.getInstance().getReference().child("Ship_Area").child(PhoneNo);
                                        ref.removeValue();

                                    }
                                });
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void deleteItemToSheet(String PhoneNo) {

        String url = "https://script.google.com/macros/s/AKfycbxQ0tXOrdSSuGp_wAJQxXsyPdCVLMGgIOahDwcBs3zETmSOjIRtlQUECvVXlC5nzkwsfQ/exec"; //deleteItem version 22
        StringRequest stringRequestOut = new StringRequest(Request.Method.POST, url, response -> {

        }, error -> {

        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> paramsOut = new HashMap<String, String>();
                paramsOut.put("action", "deleteItemShip");
                paramsOut.put("ContactNo", PhoneNo);

                return paramsOut;
            }
        };

        int socketTimeOut = 50000; // 50 sec
        RetryPolicy retryPolicyOut = new DefaultRetryPolicy(socketTimeOut,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequestOut.setRetryPolicy(retryPolicyOut);

        RequestQueue requestQueueOut = Volley.newRequestQueue(this);
        requestQueueOut.add(stringRequestOut);

    }
}