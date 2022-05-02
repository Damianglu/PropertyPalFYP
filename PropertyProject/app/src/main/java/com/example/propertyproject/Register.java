package com.example.propertyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private TextView mregisterTextView, mLoginTextView;
    private EditText mfullName, mloginEmailAddress, mloginPassword, mphoneNumber;
    private Button mregisterButton;
    private ImageView mPropertyPalImageView;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //getSupportActionBar().hide();


        mregisterTextView = findViewById(R.id.registerTextView);
        mLoginTextView = findViewById(R.id.LoginTextView);
        mfullName = findViewById(R.id.fullName);
        mloginEmailAddress = findViewById(R.id.loginEmailAddress);
        mloginPassword = findViewById(R.id.loginPassword);
        mphoneNumber = findViewById(R.id.phoneNumber);
        mregisterButton = findViewById(R.id.registerButton);
        mPropertyPalImageView = findViewById(R.id.PropertyPalImageView);

        firebaseAuth = FirebaseAuth.getInstance();

        mLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent=new Intent(register.this,MainActivity.class);
                //startActivity(intent);
                setContentView(R.layout.activity_main);

            }
        });

        mregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mloginEmailAddress.getText().toString().trim();
                String password = mloginPassword.getText().toString().trim();
                String phoneNumber = mphoneNumber.getText().toString().trim();
                String name = mfullName.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || name.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "All fields must be filled out",Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6)
                {
                    Toast.makeText(getApplicationContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //register user to firebase
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                User user = new User(name, password, phoneNumber, email);

                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);

                                Toast.makeText(getApplicationContext(), "Registration succesful", Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Registration unsuccesful", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            }
        });
    }

    private void sendEmailVerification()
    {
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Email verification sent succesfuly", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(Register.this,MainActivity.class));
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Failed to send verification email", Toast.LENGTH_SHORT).show();

        }
    }
}