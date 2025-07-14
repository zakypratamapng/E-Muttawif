package com.example.muttawif;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;

public class PersiapanHajiActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persiapan_haji);

        ImageButton btnkembali = findViewById(R.id.btnBack);
        if (btnkembali != null) {
            btnkembali.setOnClickListener(v -> finish());
        }

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        Button button5 = findViewById(R.id.button5);
        Button button6 = findViewById(R.id.button6);
        Button button7 = findViewById(R.id.button7);
        Button button8 = findViewById(R.id.button8);
        Button button9 = findViewById(R.id.button9);
        Button button10 = findViewById(R.id.button10);
        Button button11 = findViewById(R.id.button11);
        Button button12 = findViewById(R.id.button12);

        if (button1 != null) button1.setOnClickListener(v -> Toast.makeText(this, "Keutamaan Haji", Toast.LENGTH_SHORT).show());
        if (button2 != null) button2.setOnClickListener(v -> Toast.makeText(this, "Jama'ah haji tamu Allah", Toast.LENGTH_SHORT).show());
        if (button3 != null) button3.setOnClickListener(v -> Toast.makeText(this, "Segeralah jadi tamu Allah", Toast.LENGTH_SHORT).show());
        if (button4 != null) button4.setOnClickListener(v -> Toast.makeText(this, "Bersabarlah dalam ibadah", Toast.LENGTH_SHORT).show());
        if (button5 != null) button5.setOnClickListener(v -> Toast.makeText(this, "Ikhlas menjalankan ibadah", Toast.LENGTH_SHORT).show());
        if (button6 != null) button6.setOnClickListener(v -> Toast.makeText(this, "Meneladani Nabi", Toast.LENGTH_SHORT).show());
        if (button7 != null) button7.setOnClickListener(v -> Toast.makeText(this, "Jangan berdebat", Toast.LENGTH_SHORT).show());
        if (button8 != null) button8.setOnClickListener(v -> Toast.makeText(this, "Hindari maksiat agar mabrur", Toast.LENGTH_SHORT).show());
        if (button9 != null) button9.setOnClickListener(v -> Toast.makeText(this, "Bertaubat sebelum berhaji", Toast.LENGTH_SHORT).show());
        if (button10 != null) button10.setOnClickListener(v -> Toast.makeText(this, "Gunakan harta yang halal", Toast.LENGTH_SHORT).show());
        if (button11 != null) button11.setOnClickListener(v -> Toast.makeText(this, "Pilih teman perjalanan baik", Toast.LENGTH_SHORT).show());
        if (button12 != null) button12.setOnClickListener(v -> Toast.makeText(this, "Pahami hakikat talbiyah", Toast.LENGTH_SHORT).show());
    }
}