package com.example.jd_app;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class QR_Generator extends AppCompatActivity {
    Button back;
    ImageView mimageToUpload;
    FloatingActionButton fab;
    Button CreatePDF;
    EditText FullName1,PhoneNo1,Joining_Date,BloodGroup1,designation,esic_no;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    StorageReference Sref;
    String UserID;
    int maxID;
    FirebaseDatabase database;
    DatabaseReference ref;
    Member member;
    UploadTask uploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generator);

        //Back to Main Activity
        back = findViewById(R.id.Back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QR_Generator.this, MainActivity.class);
                startActivity(intent);
            }
        });

        FullName1 = findViewById(R.id.fullName);
        PhoneNo1 = findViewById(R.id.phoneNO);
        BloodGroup1 =findViewById(R.id.bloodGroup);
        designation = findViewById(R.id.Designation);
        Joining_Date = findViewById(R.id.joining_date);
        esic_no = findViewById(R.id.ESIC_No);
        mimageToUpload=findViewById(R.id.Uploaded_Image);
        fab=findViewById(R.id.floatingActionButton);
        CreatePDF = findViewById(R.id.createPDF);


        CreatePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String FullName = FullName1.getText().toString();
                String PhoneNo = PhoneNo1.getText().toString();
                String joiningDate = Joining_Date.getText().toString();
                String BloodGroup = BloodGroup1.getText().toString();
                String Designation = designation.getText().toString();
                String ESICno = esic_no.getText().toString();

                if(FullName.isEmpty()){
                    FullName1.setError("Please enter Full Name!!");
                    return;
                }
                if(PhoneNo.isEmpty()){
                    PhoneNo1.setError("Please enter Contact No!!");
                    return;
                }
                if(joiningDate.isEmpty()){
                    Joining_Date.setError("Please enter Date!!");
                    return;
                }
                if(BloodGroup.isEmpty()){
                    BloodGroup1.setError("Please enter Blood Group!!");
                    return;
                }
                if(Designation.isEmpty()){
                    designation.setError("Please enter Designation!!");
                    return;
                }
                if(ESICno.isEmpty()){
                    esic_no.setError("Please enter ESIC No!!");
                    return;
                }

                    try {
                        fStore = FirebaseFirestore.getInstance();
                        fAuth = FirebaseAuth.getInstance();
                        Sref = FirebaseStorage.getInstance().getReference();
                        String Global = FullName + Designation + joiningDate + ESICno + PhoneNo + BloodGroup;

                        addItemToSheet(FullName,PhoneNo,joiningDate,BloodGroup,Designation,ESICno);
                        createPDF(FullName, PhoneNo, joiningDate, BloodGroup, Designation, ESICno);
                        UserID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("QR_Code_Info").document(UserID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("QR_String", Global);

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: User Profile is Created for UserID: " + UserID);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: "+ e.toString());
                            }
                        });

                        //display realtime database
                        member = new Member();
                        ref = database.getInstance().getReference().child("User");
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    maxID = (int) snapshot.getChildrenCount();
                                }else{
                                    //
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        ref = database.getInstance().getReference().child("User");

                        member.setFullname(FullName1.getText().toString());
                        member.setPhoneno(PhoneNo1.getText().toString());
                        member.setJoiningdate(Joining_Date.getText().toString());
                        member.setBloodgroup(BloodGroup1.getText().toString());
                        member.setDesignation(designation.getText().toString());
                        member.setEsicno(esic_no.getText().toString());

                        StorageReference fileRef = Sref.child(System.currentTimeMillis() + "." + "jpeg");
//                        StorageReference filePath = Sref.getFile(fileRef.getDownloadUrl);

                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                            }
                        });

                        String imageID = ref.push().getKey();
                        member.setImageurl(imageID);
                        member.setGlobalString(Global);

                        ref.child(Global).setValue(member);
                        maxID++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(QR_Generator.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .maxResultSize(1080,1080)

                        //  Path: /storage/sdcard0/Android/data/package/files/Pictures/ImagePicker
                        .saveDir(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ImagePicker"))
                        .start();
            }
        });
    }
    @Override
    public void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri image=data.getData();
        mimageToUpload.setImageURI(image);

        uploadToFireBase(image);
    }
    private void uploadToFireBase(Uri image){
        Sref = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = Sref.child(System.currentTimeMillis() + "." + "jpeg");
        uploadTask = fileRef.putFile(image);


        fileRef.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

    }
    private String getFileExtension(Uri image){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(image));
    }

    public void createPDF(String FullName, String PhoneNo, String joiningDate, String BloodGroup, String Designation,String ESICno) throws Exception {

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath,FullName+".pdf");
        OutputStream outputstream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfdocument = new PdfDocument(writer);
        Document document =new Document(pdfdocument);
        pdfdocument.setDefaultPageSize(PageSize.A5);
        document.setMargins(0,0,0,0);

        Drawable d = getDrawable(R.drawable.header);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] bitmapData = stream.toByteArray();
        ImageData imagedata = ImageDataFactory.create(bitmapData);
        Image image = new Image(imagedata);

        BitmapDrawable drawable = (BitmapDrawable) mimageToUpload.getDrawable();
        Bitmap bitmap1 =drawable.getBitmap();
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.PNG,100,stream1);
        byte[] bitmapData1 = stream1.toByteArray();
        ImageData imagedata1 = ImageDataFactory.create(bitmapData1);
        Image image1 = new Image(imagedata1).setWidth(180).setHorizontalAlignment(HorizontalAlignment.CENTER).setHeight(210);

        Drawable d2 = getDrawable(R.drawable.emergency_contact);
        Bitmap bitmap2 = ((BitmapDrawable)d2).getBitmap();
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bitmap2.compress(Bitmap.CompressFormat.PNG,100,stream2);
        byte[] bitmapData2 = stream2.toByteArray();
        ImageData imagedata2 = ImageDataFactory.create(bitmapData2);
        Image image2 = new Image(imagedata2);

        Paragraph fullname = new Paragraph();
        fullname.add("        ").setMultipliedLeading(1.3f).setBold(); // 4 spaces
        fullname.add("        "+"Name : ").setFontSize(15).setMultipliedLeading(1.3f).setBold();
        fullname.add(FullName).setFontSize(15).setUnderline().setMultipliedLeading(1.3f).setBold();

        Paragraph designation = new Paragraph();
        designation.add("        ").setMultipliedLeading(1.3f).setBold();
        designation.add("Designation : ").setFontSize(15).setMultipliedLeading(1.3f).setBold();
        designation.add(Designation).setFontSize(15).setUnderline().setMultipliedLeading(1.3f).setBold();

        Paragraph date = new Paragraph();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("DD/MM/YYYY");
        date.add("        ").setMultipliedLeading(1.3f).setBold();
        date.add("Joining Date : ").setFontSize(15).setMultipliedLeading(1.3f).setBold();
        date.add(joiningDate).setFontSize(15).setMultipliedLeading(1.3f).setBold().setUnderline();

        Paragraph esic = new Paragraph();
        esic.add("    ").setMultipliedLeading(1.3f).setBold();
        esic.add("ESIC No: ").setFontSize(15).setMultipliedLeading(1.3f).setBold();
        esic.add(ESICno).setFontSize(15).setMultipliedLeading(1.3f).setBold().setUnderline();

        Paragraph phone = new Paragraph();
        phone.add("        ").setMultipliedLeading(1.3f).setBold();
        phone.add("Contact No.: ").setMultipliedLeading(1.3f).setFontSize(15).setBold();
        phone.add(PhoneNo).setFontSize(15).setMultipliedLeading(1.3f).setBold().setUnderline();

        Paragraph bloodGroup = new Paragraph();
        bloodGroup.add("        ").setMultipliedLeading(1.3f).setBold();
        bloodGroup.add("Blood Group: ").setMultipliedLeading(1.3f).setFontSize(15).setBold();
        bloodGroup.add(BloodGroup).setFontSize(15).setMultipliedLeading(1.3f).setBold().setUnderline();

        String Global = FullName + Designation + joiningDate + ESICno + PhoneNo + BloodGroup;
        String GlobalEncrypt = Crypto.encrypt(Global);
        MultiFormatWriter multiformatWriter = new MultiFormatWriter();
        BitMatrix bitmatrix = multiformatWriter.encode(GlobalEncrypt, BarcodeFormat.QR_CODE,300,300);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmapQR = barcodeEncoder.createBitmap(bitmatrix);
        ByteArrayOutputStream streamQR = new ByteArrayOutputStream();
        bitmapQR.compress(Bitmap.CompressFormat.PNG,100,streamQR);
        byte[] bitmapDataQR = streamQR.toByteArray();
        ImageData imagedataQR = ImageDataFactory.create(bitmapDataQR);
        Image imageQR = new Image(imagedataQR).setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER);

        Paragraph scan = new Paragraph();
        scan.add("SCAN QR CODE FOR ATTENDANCE ").setFontSize(18).setBold().setItalic().setTextAlignment(TextAlignment.CENTER);

        Paragraph scan1 = new Paragraph();
        scan1.add(" EMPLOYEE QR CODE ").setFontSize(18).setBold().setItalic().setTextAlignment(TextAlignment.CENTER);


        document.add(image);
        document.add(new Paragraph(""));
        document.add(new Paragraph(""));
        document.add(image1).setMargins(10,10,10,10);
        document.add(new Paragraph(""));
        document.add(new Paragraph(""));
        document.add(fullname);
        document.add(designation);
        document.add(date);
        document.add(esic);
        document.add(phone);
        document.add(bloodGroup);
        document.add(image2);
        document.add(image);
        document.add(new Paragraph(""));
        document.add(new Paragraph(""));
        document.add(scan1);
        document.add(imageQR).setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER);
        document.add(scan);
        document.add(new Paragraph(""));
        document.add(new Paragraph(""));
        document.add(image2);

//        String fullname_a  = fullname.toString();
//        String designation_a = designation.toString();
//        String date_a = date.toString();
//        String BloodGroup_a = bloodGroup.toString();
//        String PhoneNo_a = phone.toString();
//        String ESIC_No_a = esic.toString();
//        addItemToSheet(fullname_a,PhoneNo_a,date_a,BloodGroup_a,designation_a,ESIC_No_a);
        document.close();



        Toast.makeText(this, "Check Download Folder for PDF", Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void addItemToSheet(String fullname, String PhoneNo, String JoiningDate, String BloodGroup, String Designation, String ESIC_No){

        String url = "https://script.google.com/macros/s/AKfycbz_QtrrVgvvcB2ymBHSnEew0gfB8j4MLLZ3eUTZ5NoCP5zmvoLQUPetZ7JZ4xlHfjqD_w/exec";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {

        }, error -> {

        }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("action","addItemMaster");
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
}