package com.example.muttawif;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class dashboard extends AppCompatActivity {

    Button buttonMuhammadiyah, buttonNU;
    ImageButton buttonProfile, buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Pastikan nama layout sesuai

        buttonMuhammadiyah = findViewById(R.id.buttonMuhammadiyah);
        buttonNU = findViewById(R.id.buttonNU);
        buttonProfile = findViewById(R.id.buttonProfile);
        buttonLogout = findViewById(R.id.buttonLogout);

        buttonMuhammadiyah.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(dashboard.this, MainActivity15.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e("dashboard", "Gagal membuka MainActivity15", e);
                Toast.makeText(dashboard.this, "Error membuka halaman", Toast.LENGTH_SHORT).show();
            }
        });

        buttonNU.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(dashboard.this, MainActivity3.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e("dashboard", "Gagal membuka MainActivity3", e);
                Toast.makeText(dashboard.this, "Error membuka halaman", Toast.LENGTH_SHORT).show();
            }
        });

        buttonProfile.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, ProfileActivity.class);
            startActivity(intent);
        });

        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Logout Firebase

            // Hapus data dari SharedPreferences
            SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(dashboard.this, "Logout berhasil", Toast.LENGTH_SHORT).show();

            // Kembali ke halaman login
            Intent intent = new Intent(dashboard.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Bersihkan backstack
            startActivity(intent);
        });
    }
}
