package com.example.muttawif;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView textNama, textNoTelepon, textAlamat, textNoRombongan;
    private Button buttonKembali;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textNama = findViewById(R.id.textNama);
        textNoTelepon = findViewById(R.id.textNoTelepon);
        textAlamat = findViewById(R.id.textAlamat);
        textNoRombongan = findViewById(R.id.textNoRombongan);
        buttonKembali = findViewById(R.id.buttonKembali);

        // Ambil data dari SharedPreferences (Simpan saat Signup)
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        textNama.setText("Nama: " + prefs.getString("nama", "Tidak tersedia"));
        textNoTelepon.setText("No Telepon: " + prefs.getString("noTelepon", "Tidak tersedia"));
        textAlamat.setText("Alamat: " + prefs.getString("alamat", "Tidak tersedia"));
        textNoRombongan.setText("No Rombongan: " + prefs.getString("noRombongan", "Tidak tersedia"));

        // Tombol kembali ke MainActivity1 (Pemilihan Referensi)
        buttonKembali.setOnClickListener(v -> finish());
    }
}
