package com.example.muttawif;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView textNama = findViewById(R.id.textNama);
        TextView textNoTelepon = findViewById(R.id.textNoTelepon);
        TextView textAlamat = findViewById(R.id.textAlamat);
        TextView textNoRombongan = findViewById(R.id.textNoRombongan);
        TextView textNamaKetua = findViewById(R.id.textNamaKetua);
        TextView textJenisKelamin = findViewById(R.id.textJenisKelamin);
        Button buttonKembali = findViewById(R.id.buttonKembali);

        // Retrieve user data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        User user = new User(
                prefs.getString("uid", ""),
                prefs.getString("nama", "Tidak tersedia"),
                prefs.getString("email", "Tidak tersedia"),
                prefs.getString("noTelepon", "Tidak tersedia"),
                prefs.getString("alamat", "Tidak tersedia"),
                prefs.getString("noRombongan", "Tidak tersedia"),
                prefs.getString("namaKetua", "Tidak tersedia"),
                prefs.getString("jenisKelamin", "Tidak tersedia")
        );

        textNama.setText("Nama: " + user.nama);
        textNoTelepon.setText("No Telepon: " + user.noTelepon);
        textAlamat.setText("Alamat: " + user.alamat);
        textNoRombongan.setText("No Rombongan: " + user.noRombongan);
        textNamaKetua.setText("Nama Ketua Rombongan: " + user.namaKetua);
        textJenisKelamin.setText("Jenis Kelamin: " + user.jenisKelamin);

        buttonKembali.setOnClickListener(v -> finish());
    }
}