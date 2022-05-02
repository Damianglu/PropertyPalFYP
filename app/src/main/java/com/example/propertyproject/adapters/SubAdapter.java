package com.example.propertyproject.adapters;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.propertyproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SubAdapter  extends RecyclerView.Adapter<SubAdapter.ViewHolder> {


    List<String> list;
    public SubAdapter(List<String> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_images_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String uri = list.get(position);
        Picasso.get().load(uri).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFullImage(uri,holder.itemView.getContext());
            }
        });

    }

    private void showFullImage(String imageUri, Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.full_image_layout,null);
        ImageView imageView = view.findViewById(R.id.imageView);

        Picasso.get().load(imageUri).into(imageView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view);

        Dialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}