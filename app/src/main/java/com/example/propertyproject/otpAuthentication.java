package com.example.propertyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class otpAuthentication extends AppCompatActivity {

    TextView mchangenumber;
    EditText mgetotp;
    android.widget.Button mverifyotp;
    String enteredotp;
    private FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ProgressBar mprogressbarofotpauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_authentication);

        mchangenumber = findViewById(R.id.changenumber);
        mgetotp = findViewById(R.id.getotp);
        mverifyotp = findViewById(R.id.verifyotp);
        mprogressbarofotpauth = findViewById(R.id.progressbarofotpauth);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mchangenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(otpAuthentication.this, splashMain.class);
                startActivity(intent);
            }
        });

        mverifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredotp=mgetotp.getText().toString();
                if(enteredotp.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please enter your otp",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mprogressbarofotpauth.setVisibility(View.VISIBLE);
                    String codereceived = getIntent().getStringExtra("otp");
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codereceived, enteredotp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    checkIfUserExist();

                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        mprogressbarofotpauth.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "You are not logged in",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void checkIfUserExist(){
        firebaseFirestore
                .collection("chatusers")
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot  = task.getResult();
                            String uid = documentSnapshot.getString("uid");
                            if(uid != null){
                                mprogressbarofotpauth.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "You are logged in",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(otpAuthentication.this, chatActivity.class);
                                startActivity(intent);
                                finish();;
                            } else {
                                mprogressbarofotpauth.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "You are logged in",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(otpAuthentication.this, setProfile.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}