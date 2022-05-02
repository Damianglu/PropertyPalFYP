package com.example.propertyproject;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class chatFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    LinearLayoutManager linearLayoutManager;
    private FirebaseAuth firebaseAuth;

    ImageView mimageviewofuser;

    FirestoreRecyclerAdapter<firebasemodel2, NoteViewHolder> chatAdapter;

    RecyclerView mrecyclerview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chatfragment,container,false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mrecyclerview = v.findViewById(R.id.recyclerview);

        //Query query = firebaseFirestore.collection("chatusers");
        Query query = firebaseFirestore.collection("chatusers").whereNotEqualTo("uid", firebaseAuth.getUid());
        FirestoreRecyclerOptions<firebasemodel2> allusername = new FirestoreRecyclerOptions.Builder<firebasemodel2>()
                .setQuery(query, firebasemodel2.class)
                .build();

        chatAdapter = new FirestoreRecyclerAdapter<firebasemodel2, NoteViewHolder>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int position, @NonNull firebasemodel2 firebasemodel2) {

                noteViewHolder.particularusername.setText(firebasemodel2.getName());
                String uri = firebasemodel2.getImage();

                Picasso.get().load(uri).into(mimageviewofuser);
                if(firebasemodel2.getStatus().equals("Online"))
                {
                    noteViewHolder.statusofuser.setText(firebasemodel2.getStatus());
                    noteViewHolder.statusofuser.setTextColor(Color.GREEN);

                }else
                {
                    noteViewHolder.statusofuser.setText(firebasemodel2.getStatus());
                }

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), specificchat.class);
                        intent.putExtra("name", firebasemodel2.getName());
                        intent.putExtra("receiveruid", firebasemodel2.getUid());
                        intent.putExtra("imageuri", firebasemodel2.getImage());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatviewlayout, parent, false);
                return new NoteViewHolder(view);
            }
        };


        mrecyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mrecyclerview.setLayoutManager(linearLayoutManager);
        mrecyclerview.setAdapter(chatAdapter);

        return v;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder
    {
        private TextView particularusername;
        private TextView statusofuser;

        public NoteViewHolder(@NonNull View itemView)
        {
            super(itemView);
            particularusername = itemView.findViewById(R.id.nameofuser);
            statusofuser = itemView.findViewById(R.id.statusofuser);
            mimageviewofuser = itemView.findViewById(R.id.imageviewofuser);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(chatAdapter!=null)
        {
            chatAdapter.stopListening();
        }
    }
}
