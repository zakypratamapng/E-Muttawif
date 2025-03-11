package com.example.muttawif;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ImageButton buttonProfile, buttonLogout;
    private Button buttonMuhammadiyah, buttonNU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi tombol
        buttonProfile = findViewById(R.id.buttonProfile);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonMuhammadiyah = findViewById(R.id.buttonMuhammadiyah);
        buttonNU = findViewById(R.id.buttonNU);

        // Buka Profile Activity
        buttonProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Logout dari Firebase
        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Navigasi ke Doa Haji Muhammadiyah
        buttonMuhammadiyah.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("referensi", "Muhammadiyah");
                startActivity(intent);
            } catch (Exception e) {
                Log.e("MainActivity", "Error membuka MainActivity2: " + e.getMessage());
                Toast.makeText(this, "Terjadi kesalahan saat membuka halaman", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigasi ke Doa Haji Nahdlatul Ulama
        buttonNU.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("referensi", "NU");
                startActivity(intent);
            } catch (Exception e) {
                Log.e("MainActivity", "Error membuka MainActivity2: " + e.getMessage());
                Toast.makeText(this, "Terjadi kesalahan saat membuka halaman", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
