package com.example.propertyproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AdminRenterActivity extends AppCompatActivity {
    private CardView adminCard,renterCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_renter);

        adminCard = findViewById(R.id.admin);
        renterCard = findViewById(R.id.renter);

        adminCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAccessDialog();
            }
        });

        renterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminRenterActivity.this,splashMain.class);
                startActivity(intent);
            }
        });
    }

    private void showAccessDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.admin_renter_dialog,null);
        EditText codeEdit = view.findViewById(R.id.editCode);
        Button accessBtn = view.findViewById(R.id.confirmAccess);


        AlertDialog.Builder alertDialog =  new AlertDialog.Builder(this)
                .setView(view);

        Dialog dialog = alertDialog.create();
        dialog.show();

        accessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String accessCode = codeEdit.getText().toString().trim();
                if(accessCode.equals("1234")){
                    dialog.dismiss();
                    Intent intent = new Intent(AdminRenterActivity.this,HomePage.class);
                    startActivity(intent);
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(AdminRenterActivity.this,"Access Code is wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}