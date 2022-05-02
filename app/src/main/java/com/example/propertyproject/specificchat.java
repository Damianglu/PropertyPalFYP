package com.example.propertyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class specificchat extends AppCompatActivity {

    EditText mgetmessage;
    ImageView mimageviewsendmessage;

    CardView mcardviewofsendmessage;
    androidx.appcompat.widget.Toolbar mtoolbarofspecificchat;
    ImageView mspecificuserimageinimageview;
    TextView mnameofspecificuser;

    private String enteredmessage;
    Intent intent;
    String mreceivername, sendername, mreceiveruid, msenderuid;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    String senderroom, receiverroom;

    ImageButton mbackbuttonofspecificchat;

    RecyclerView mrecyclerviewofspecificuser;

    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specificchat);

        mgetmessage = findViewById(R.id.getmessage);
        mcardviewofsendmessage = findViewById(R.id.cardviewofsendmessage);
        mimageviewsendmessage = findViewById(R.id.imageviewsendmessage);
        mtoolbarofspecificchat = findViewById(R.id.toolbarofspecificchat);
        mnameofspecificuser = findViewById(R.id.nameofspecificuser);
        mspecificuserimageinimageview = findViewById(R.id.specificuserimageinimageview);
        mbackbuttonofspecificchat = findViewById(R.id.backbuttonofspecificchat);
        messagesArrayList = new ArrayList<>();
        mrecyclerviewofspecificuser = findViewById(R.id.recyclerviewofspecificuser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mrecyclerviewofspecificuser.setLayoutManager(linearLayoutManager);
//        messagesAdapter= new MessagesAdapter(specificchat.this, messagesArrayList);
//        mrecyclerviewofspecificuser.setAdapter(messagesAdapter);

        intent = getIntent();

        setSupportActionBar(mtoolbarofspecificchat);
        mtoolbarofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Toolbar is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");

        msenderuid = firebaseAuth.getUid();
        mreceiveruid = getIntent().getStringExtra("receiveruid");
        mreceivername = getIntent().getStringExtra("name");
        senderroom = msenderuid + mreceiveruid;
        receiverroom = mreceiveruid + msenderuid;

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("chats").child(senderroom).child("messages");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Messages messages = snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);
                    messagesAdapter = new MessagesAdapter(specificchat.this, messagesArrayList);
                    mrecyclerviewofspecificuser.setAdapter(messagesAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mbackbuttonofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mnameofspecificuser.setText(mreceivername);
        String uri = intent.getStringExtra("imageuri");
        if (uri.isEmpty()) {
            Toast.makeText(getApplicationContext(), "null is received", Toast.LENGTH_SHORT).show();
        } else {
            Picasso.get().load(uri).into(mspecificuserimageinimageview);
        }

        mimageviewsendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enteredmessage = mgetmessage.getText().toString();
                if (enteredmessage.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "null is received", Toast.LENGTH_SHORT).show();
                } else {
                    Date date = new Date();
                    currenttime = simpleDateFormat.format(calendar.getTime());
                    Messages messages = new Messages(enteredmessage, firebaseAuth.getUid(), date.getTime(), currenttime);
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats")
                            .child(senderroom)
                            .child("messages")
                            .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseDatabase.getReference()
                                    .child("chats")
                                    .child(receiverroom)
                                    .child("messages")
                                    .push()
                                    .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                    });
                    mgetmessage.setText("");
                    hideKeyboard();
                }

            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}