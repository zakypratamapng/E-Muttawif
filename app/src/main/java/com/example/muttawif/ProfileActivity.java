package com.example.muttawif;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private EditText editNama, editNoTelepon, editAlamat, editNoRombongan, editNamaKetua;
    private RadioGroup radioGroupGender;
    private String uid;
    private String email;
    private DatabaseReference databaseReference;
    private Button buttonSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editNama = findViewById(R.id.editNama);
        editNoTelepon = findViewById(R.id.editNoTelepon);
        editAlamat = findViewById(R.id.editAlamat);
        editNoRombongan = findViewById(R.id.editNoRombongan);
        editNamaKetua = findViewById(R.id.editNamaKetua);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        buttonSimpan = findViewById(R.id.buttonSimpan);

        uid = getIntent().getStringExtra("uid");
        email = getIntent().getStringExtra("email");
        String nama = getIntent().getStringExtra("nama");

        if (uid == null || uid.isEmpty()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                uid = currentUser.getUid();
                if (email == null) email = currentUser.getEmail();
                if (nama == null) nama = currentUser.getDisplayName();
            }
        }

        if (uid == null || uid.isEmpty()) {
            Toast.makeText(this, "User ID tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (nama != null) {
            editNama.setText(nama);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memuat data profil...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Cek apakah data user sudah ada dan lengkap
        databaseReference.child(uid).get().addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful() && task.getResult().exists()) {
                User user = task.getResult().getValue(User.class);
                // Cek data user tidak null dan semua field utama terisi
                if (user != null
                        && user.nama != null && !user.nama.isEmpty()
                        && user.noTelepon != null && !user.noTelepon.isEmpty()
                        && user.alamat != null && !user.alamat.isEmpty()
                        && user.noRombongan != null && !user.noRombongan.isEmpty()
                        && user.namaKetua != null && !user.namaKetua.isEmpty()
                        && user.jenisKelamin != null && !user.jenisKelamin.isEmpty()) {
                    // Data lengkap, langsung buka ProfileInfoActivity
                    Intent intent = new Intent(this, ProfileInfoActivity.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("nama", user.nama);
                    intent.putExtra("email", user.email);
                    intent.putExtra("noTelepon", user.noTelepon);
                    intent.putExtra("alamat", user.alamat);
                    intent.putExtra("noRombongan", user.noRombongan);
                    intent.putExtra("namaKetua", user.namaKetua);
                    intent.putExtra("jenisKelamin", user.jenisKelamin);
                    intent.putExtra("profileImageUrl", user.profileImageUrl);
                    startActivity(intent);
                    finish();
                } else {
                    // Data belum lengkap, tampilkan form dan simpan jika diklik
                    buttonSimpan.setOnClickListener(v -> saveProfile());
                }
            } else {
                // Data belum ada, tampilkan form dan simpan jika diklik
                buttonSimpan.setOnClickListener(v -> saveProfile());
            }
        });
    }

    private void saveProfile() {
        String nama = editNama.getText().toString().trim();
        String noTelepon = editNoTelepon.getText().toString().trim();
        String alamat = editAlamat.getText().toString().trim();
        String noRombongan = editNoRombongan.getText().toString().trim();
        String namaKetua = editNamaKetua.getText().toString().trim();

        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        String jenisKelamin;
        if (selectedGenderId == R.id.radioLaki) {
            jenisKelamin = "Laki-laki";
        } else if (selectedGenderId == R.id.radioPerempuan) {
            jenisKelamin = "Perempuan";
        } else {
            Toast.makeText(this, "Pilih jenis kelamin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nama.isEmpty() || noTelepon.isEmpty() || alamat.isEmpty() || noRombongan.isEmpty() || namaKetua.isEmpty()) {
            Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan data profil...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        saveProfileToDatabase(nama, noTelepon, alamat, noRombongan, namaKetua, jenisKelamin, progressDialog);
    }

    private void saveProfileToDatabase(String nama, String noTelepon, String alamat, String noRombongan, String namaKetua, String jenisKelamin, ProgressDialog progressDialog) {
        try {
            User user = new User(uid, nama, email, noTelepon, alamat, noRombongan, namaKetua, jenisKelamin, null);
            databaseReference.child(uid).setValue(user).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(this, ProfileInfoActivity.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("nama", nama);
                    intent.putExtra("email", email);
                    intent.putExtra("noTelepon", noTelepon);
                    intent.putExtra("alamat", alamat);
                    intent.putExtra("noRombongan", noRombongan);
                    intent.putExtra("namaKetua", namaKetua);
                    intent.putExtra("jenisKelamin", jenisKelamin);
                    intent.putExtra("profileImageUrl", (String) null);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Gagal menyimpan profil", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Gagal menyimpan profil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}