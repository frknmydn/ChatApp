package com.furkanmeydan.lastchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    EditText edtEposta, edtSifre;
    Button btnGiris,btnUyeOl;
    FirebaseAuth firebaseAuth;
    String eMail,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        tanimla();







    }


    public void tanimla(){
        edtEposta=findViewById(R.id.editEposta);
        edtSifre=findViewById(R.id.editSifre);
        btnGiris=findViewById(R.id.btnGiris);
        firebaseAuth=FirebaseAuth.getInstance();



    }

    public void girisYap(View view){

        eMail = edtEposta.getText().toString();
        password = edtSifre.getText().toString();

        Log.d("Tagg",eMail);
        Log.d("Tagg",password);

        firebaseAuth.signInWithEmailAndPassword(eMail,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent intent = new Intent(SignInActivity.this,HomeActivity.class);


                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInActivity.this,"E mail veya şifre yanlış",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void kayitOl(View view){
        Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
        startActivity(intent);
    }
}