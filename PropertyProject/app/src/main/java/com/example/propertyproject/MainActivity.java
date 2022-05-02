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


public class MainActivity extends AppCompatActivity {

    private EditText mloginEmailAddress, mloginPassword;
    private Button mloginButton, mexistingAccountButton;
    private ImageView mPropertyPalImalgeView;
    private TextView mloginTextView, mforgotPasswordTextView;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //getSupportActionBar().hide();
        mloginEmailAddress = findViewById(R.id.loginEmailAddress);
        mloginPassword = findViewById(R.id.loginPassword);
        mloginButton = findViewById(R.id.registerButton);
        mexistingAccountButton = findViewById(R.id.existingAccountButton);
        mPropertyPalImalgeView = findViewById(R.id.PropertyPalImageView);
        mloginTextView = findViewById(R.id.LoginTextView);
        mforgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        if(firebaseUser!=null)
        {
            finish();
            startActivity(new Intent(MainActivity.this, Register.class));
        }
        mexistingAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Register.class));
            }
        });

        mforgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ForgotPassword.class));
            }
        });

        mloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mloginEmailAddress.getText().toString().trim();
                String password = mloginPassword.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"All fields are required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Login the user
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                checkEmailVerification();
                                startActivity(new Intent(MainActivity.this,HomePage.class));


                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Account does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser.isEmailVerified()==true)
        {
            Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);

            finish();
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Verify your email please", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }


}