package com.furkanmeydan.lastchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    ImageView imageView;
    Uri imageData; //kaynak yolu
    Bitmap selectedImage;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    EditText edtNameSU,edtEmailSU,edtSifreSU,edtSifreTekrarSU;
    String eMail,password;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        if(Build.VERSION.SDK_INT>=23){
            //ilk başta izin verilmiş mi verilmemiş mi onu kontrol etmemiz gerekiyor
            if(this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
            }

        }

        imageView = findViewById(R.id.signUpFoto);
        edtNameSU=findViewById(R.id.editName);
        edtEmailSU=findViewById(R.id.editEpostaUye);
        edtSifreSU=findViewById(R.id.edtSifreUye);
        edtSifreTekrarSU=findViewById(R.id.edtSifreTekrarUye);
        progressBar=findViewById(R.id.progressBarr);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery, 2);
            }
        }
    }

    public void selectImage(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 2);

        }

    }

    public void setUserInfo(){
        UUID uuid = UUID.randomUUID();
        final String path="image/" + uuid + ".jpg";

        storageReference.child(path).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String uid = firebaseAuth.getUid();
                        String name = edtNameSU.getText().toString();
                        String url = uri.toString();
                        databaseReference.child(Child.users).push().setValue(
                                new UserInfo(uid,name,url)
                        );
                        progressBar.setVisibility(View.GONE);

                        Intent intent = new Intent(SignUpActivity.this,HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);

                    }
                });
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageData = data.getData();
            try {

                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);
                } else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                    imageView.setImageBitmap(selectedImage);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }


    public void btnKaydol(View view) {

        if (imageData == null ||
                edtNameSU.getText().toString().isEmpty() ||
                edtSifreSU.getText().toString().isEmpty() ||
                edtSifreTekrarSU.getText().toString().isEmpty() ||
                edtEmailSU.getText().toString().isEmpty()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setTitle("HATA!");
            builder.setMessage("Lütfen bütün alanları eksiksiz doldurun");

            return;

        }

        else if (edtSifreSU.getText().toString().equals(edtSifreTekrarSU.getText().toString())) {

            System.out.println("Basıldı 2");

            eMail = edtEmailSU.getText().toString();
            password= edtSifreSU.getText().toString();

            progressBar.setVisibility(view.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(eMail, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    setUserInfo();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"HATA!",Toast.LENGTH_LONG).show();
                }
            });

        }



    }
}