package com.example.jd_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity{
    public static final String TAG = "TAG";
    public static int count;
    EditText rFullname, rEmail, rPassword, rPhone;
    Button rRegister;
    TextView rLogin_Button;
    ProgressBar lProgress;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        rFullname = findViewById(R.id.Full_Name);
        rEmail = findViewById(R.id.Email_Address);
        rPassword = findViewById(R.id.Password);
        rPhone = findViewById(R.id.Phone_No);
        rRegister =findViewById(R.id.register_button);
        rLogin_Button = findViewById(R.id.forgotPassword);

        fAuth = FirebaseAuth.getInstance();
        lProgress = findViewById(R.id.Register_Progress);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        rRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = rEmail.getText().toString();
                String password = rPassword.getText().toString();
                String name = rFullname.getText().toString();
                String phone = rPhone.getText().toString();
                if(TextUtils.isEmpty((name))){
                    rFullname.setError("Please enter your Name!!");
                }
                if(TextUtils.isEmpty(email)){
                    rEmail.setError("Please enter Email!!");
                    return;
                }
                if(TextUtils.isEmpty((password))){
                    rPassword.setError("Please enter Password!!");
                    return;
                }
                if(rPassword.length()<6){
                    rPassword.setError("Password must be greater than 6 characters");
                }
                if(rPhone.length() !=10){
                    rPhone.setError("Please enter Phone Number of 10 Digits");
                }
                lProgress.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener((task) ->{
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "User Created Successfully !!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Login.class));
//                            Map<String,Object> user = new HashMap<>();
//                                user.put("fullName",rFullname);
//                                user.put("Email",rEmail);
//                                user.put("PhoneNo",rPhone);
//                                fStore.collection("Users")
                            //fStore.collection("Users").document(userID).set(user);
//                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Log.d(TAG,"On Success: User Profile Created For: "+userID); //ctrl alt c for tag
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG,"On Failure: "+userID);
//                                }
//                            });
//                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else {
                            Toast.makeText(Register.this, "Error User Not Created " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                });
            }
        });

        rLogin_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}