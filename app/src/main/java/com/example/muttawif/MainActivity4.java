package com.example.muttawif;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        ImageView icLogout = findViewById(R.id.btnBack);
        icLogout.setOnClickListener(v -> finish());

        CardView cardTamattu = findViewById(R.id.cardTamattu);
        CardView cardIfrad = findViewById(R.id.cardIfrad);
        CardView cardQiran = findViewById(R.id.cardQiran);

        cardTamattu.setOnClickListener(v -> {
            Intent intent = new Intent(this, TamattuActivity.class);
            startActivity(intent);
        });
        cardIfrad.setOnClickListener(v -> {
            Intent intent = new Intent(this, IfradActivity.class);
            startActivity(intent);
        });
        cardQiran.setOnClickListener(v -> {
            Intent intent = new Intent(this, QiranActivity.class);
            startActivity(intent);
        });
    }
}