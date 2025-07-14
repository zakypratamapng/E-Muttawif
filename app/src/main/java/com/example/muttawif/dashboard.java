package com.example.muttawif;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dashboard extends AppCompatActivity {

    Button buttonMuhammadiyah, buttonNU;
    ImageButton buttonProfile, buttonLogout;
    TextView tvDashboardNama, tvDashboardNoRombongan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Pastikan nama layout sesuai

        buttonMuhammadiyah = findViewById(R.id.buttonMuhammadiyah);
        buttonNU = findViewById(R.id.buttonNU);
        buttonProfile = findViewById(R.id.buttonProfile);
        buttonLogout = findViewById(R.id.buttonLogout);

        tvDashboardNama = findViewById(R.id.tvDashboardNama);
        tvDashboardNoRombongan = findViewById(R.id.tvDashboardNoRombongan);

        // Ambil data user dari Firebase berdasarkan UID user yang login
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        String nama = user.getNama() != null ? user.getNama() : "Nama User";
                        String noRombongan = user.getNoRombongan() != null ? user.getNoRombongan() : "-";
                        tvDashboardNama.setText(nama);
                        tvDashboardNoRombongan.setText("Rombongan: " + noRombongan);
                    } else {
                        tvDashboardNama.setText("Nama User");
                        tvDashboardNoRombongan.setText("Rombongan: -");
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    tvDashboardNama.setText("Nama User");
                    tvDashboardNoRombongan.setText("Rombongan: -");
                }
            });
        } else {
            tvDashboardNama.setText("Nama User");
            tvDashboardNoRombongan.setText("Rombongan: -");
        }

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
                Intent intent = new Intent(dashboard.this, MainActivity5.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e("dashboard", "Gagal membuka MainActivity5", e);
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
            SharedPreferences preferences1 = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences1.edit();
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