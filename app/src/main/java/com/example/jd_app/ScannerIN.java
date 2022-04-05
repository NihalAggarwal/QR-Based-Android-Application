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
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.jd_app.databinding.ActivityScannerInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class ScannerIN extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    ActivityScannerInBinding binding;
    DatabaseReference reference;
    FirebaseDatabase database;
    DatabaseReference ref;
    Dormitory_Member member;
    private int count;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_in);
        FirebaseFirestore fStore;
        FirebaseAuth fAuth;
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        displayCount();
        mCodeScanner = new CodeScanner(this, scannerView);
        displayCount();
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        try {
                            displayCount();
                            FirebaseFirestore fStore;
                            FirebaseAuth fAuth;
                            fStore = FirebaseFirestore.getInstance();
                            fAuth = FirebaseAuth.getInstance();
                            String decrypt = Crypto.decrypt(result.getText());
                            String check = fStore.document("/QR_Code_Info/*").toString();
                            if(decrypt.equals(check)){
                                int a = Dormitory.temp_counter;
                                Dormitory.counter.setText(Integer.toString(a));
                                Toast.makeText(ScannerIN.this, "Scan Successful", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                int a = Dormitory.temp_counter;
                                if(a>=0) {
                                    Dormitory.counter.setText(String.valueOf(a));
                                    Toast.makeText(ScannerIN.this, "Scan Success!", Toast.LENGTH_SHORT).show();

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");

                                    reference.child(decrypt).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                                             DataSnapshot dataSnapshot = task.getResult();
                                             String Fullname = String.valueOf(dataSnapshot.child("fullname").getValue());
                                             String PhoneNo = String.valueOf(dataSnapshot.child("phoneno").getValue());
                                             String JoiningDate = String.valueOf(dataSnapshot.child("joiningdate").getValue());
                                             String BloodGroup = String.valueOf(dataSnapshot.child("bloodgroup").getValue());
                                             String Designation = String.valueOf(dataSnapshot.child("designation").getValue());
                                             String ESIC_No = String.valueOf(dataSnapshot.child("esicno").getValue());

                                            System.out.println(Fullname);
                                            System.out.println(PhoneNo);
                                            System.out.println(JoiningDate);
                                            System.out.println(BloodGroup);
                                            System.out.println(Designation);
                                            System.out.println(ESIC_No);

                                             addItemToSheet(Fullname,PhoneNo,JoiningDate,BloodGroup,Designation,ESIC_No);

                                             ref = database.getInstance().getReference().child("Dormitory");

                                             member = new Dormitory_Member();

                                             member.setFullname(Fullname);
                                             member.setPhoneno(PhoneNo);
                                             member.setJoiningdate(JoiningDate);
                                             member.setBloodgroup(BloodGroup);
                                             member.setDesignation(Designation);
                                             member.setEsicno(ESIC_No);

                                             ref.child(PhoneNo).setValue(member);

                                             ref.addValueEventListener(new ValueEventListener() {
                                                 @Override
                                                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    count = (int) snapshot.getChildrenCount();
                                                    Dormitory.temp_counter = count;
                                                    int a = Dormitory.temp_counter;
                                                    Dormitory.counter.setText(Integer.toString(a));
                                                    Dormitory.check_sms = 0;
                                                    System.out.println(count);

                                                    finish();
                                                 }

                                                 @Override
                                                 public void onCancelled(@NonNull DatabaseError error) {

                                                 }
                                             });
                                        }
                                    });

                                    finish();
                                }
                                else{
                                    a++;
                                    Toast.makeText(ScannerIN.this, "No People to sign out!!", Toast.LENGTH_SHORT).show();
                                }
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


    private void addItemToSheet(String fullname, String PhoneNo, String JoiningDate, String BloodGroup, String Designation, String ESIC_No){

        String url = "https://script.google.com/macros/s/AKfycbz_QtrrVgvvcB2ymBHSnEew0gfB8j4MLLZ3eUTZ5NoCP5zmvoLQUPetZ7JZ4xlHfjqD_w/exec"; //AddItems Version 6
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {

        }, error -> {

        }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("action","addItem");
                params.put("FullName",fullname);
                params.put("ContactNo",PhoneNo);
                params.put("JoiningDate",JoiningDate);
                params.put("BloodGroup",BloodGroup);
                params.put("Designation",Designation);
                params.put("ESIC_No",ESIC_No);

                return params;
            }
        };

        int socketTimeOut = 50000; // 50 sec
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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