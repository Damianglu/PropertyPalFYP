package com.example.propertyproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.propertyproject.AppointmentsActivity;
import com.example.propertyproject.R;
import com.example.propertyproject.models.AppointmentModel;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int FULL_LIST = 0;
    private int EMPTY_LIST = 1;
    List<AppointmentModel> list;
    List<List<String>> imagesList;
    private SubAdapter subAdapter;
    public onItemClick onItemClick;
    public interface  onItemClick {
        void addImages(String documentId);
    }

    public void getImages(onItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    public AppointmentsAdapter(List<AppointmentModel> list,List<List<String>> imagesList){
        this.list = list;
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == FULL_LIST){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointments_layout, parent, false);
            return new ViewHolder0(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_list_layout, parent, false);
            return new ViewHolder1(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AppointmentModel model = list.get(position);
        List<String> stringList = imagesList.get(position);

        if(holder.getItemViewType() == FULL_LIST){
            ViewHolder0 holder0  = (ViewHolder0) holder;
            holder0.address.setText(model.getAddress());
            holder0.contractor.setText(model.getContractor());
            holder0.date.setText(model.getDate());
            holder0.description.setText(model.getDescription());
            // set up subAdapter
            holder0.recyclerView.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(),2));
            holder0.recyclerView.setHasFixedSize(true);
            subAdapter = new SubAdapter(stringList);
            holder0.recyclerView.setAdapter(subAdapter);
            holder0.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClick != null){
                        int position = holder.getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onItemClick.addImages(model.getDocumentId());
                        }
                    }
                }
            });

        } else if (holder.getItemViewType() == EMPTY_LIST){
            ViewHolder1 holder1  = (ViewHolder1) holder;
            holder1.address.setText(model.getAddress());
            holder1.contractor.setText(model.getContractor());
            holder1.date.setText(model.getDate());
            holder1.description.setText(model.getDescription());
            holder1.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClick != null){
                        int position = holder.getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onItemClick.addImages(model.getDocumentId());
                        }
                    }
                }
            });
        }

    }


    public class ViewHolder0 extends  RecyclerView.ViewHolder{
        private RecyclerView recyclerView;
        private TextView address;
        private TextView contractor;
        private TextView date;
        private TextView description;

        LinearLayout mcontractor;
        public ViewHolder0(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            address = itemView.findViewById(R.id.address);
            contractor = itemView.findViewById(R.id.contractor);
            date = itemView.findViewById(R.id.date);
            description = itemView.findViewById(R.id.description);


        }
    }

    public class ViewHolder1 extends  RecyclerView.ViewHolder{
        private TextView address;
        private TextView contractor;
        private TextView date;
        private TextView description;

        LinearLayout mcontractor;
        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            contractor = itemView.findViewById(R.id.contractor);
            date = itemView.findViewById(R.id.date);
            description = itemView.findViewById(R.id.description);

        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(imagesList.get(position) == null){
            return EMPTY_LIST;
        } else {
            return FULL_LIST;
        }
    }
}
