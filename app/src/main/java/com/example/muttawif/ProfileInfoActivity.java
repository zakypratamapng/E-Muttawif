package com.example.muttawif;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileInfoActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 301;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 302;
    private Uri selectedImageUri;
    private String uid;
    private String profileImageUrl;
    private ImageView imgAvatar;
    private DatabaseReference userRef;
    private Button btnSavePhoto;
    private boolean fromGoogleLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgAvatar = findViewById(R.id.imgAvatar);
        Button btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnSavePhoto = findViewById(R.id.btnSavePhoto);
        TextView tvNama = findViewById(R.id.tvNama);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvNoTelepon = findViewById(R.id.tvNoTelepon);
        TextView tvAlamat = findViewById(R.id.tvAlamat);
        TextView tvNoRombongan = findViewById(R.id.tvNoRombongan);
        TextView tvNamaKetua = findViewById(R.id.tvNamaKetua);
        TextView tvJenisKelamin = findViewById(R.id.tvJenisKelamin);

        uid = getIntent().getStringExtra("uid");
        String nama = getIntent().getStringExtra("nama");
        String email = getIntent().getStringExtra("email");
        String noTelepon = getIntent().getStringExtra("noTelepon");
        String alamat = getIntent().getStringExtra("alamat");
        String noRombongan = getIntent().getStringExtra("noRombongan");
        String namaKetua = getIntent().getStringExtra("namaKetua");
        String jenisKelamin = getIntent().getStringExtra("jenisKelamin");
        profileImageUrl = getIntent().getStringExtra("profileImageUrl");

        // Cek apakah activity ini dibuka dari login Google
        fromGoogleLogin = getIntent().getBooleanExtra("fromGoogleLogin", false);

        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        if (isNullOrEmpty(nama) || isNullOrEmpty(noTelepon) || isNullOrEmpty(alamat)
                || isNullOrEmpty(noRombongan) || isNullOrEmpty(namaKetua) || isNullOrEmpty(jenisKelamin)) {
            Toast.makeText(this, "Data profil tidak lengkap. Silakan lengkapi profil Anda.", Toast.LENGTH_LONG).show();
            return;
        }

        tvNama.setText(nama);
        tvEmail.setText(email != null ? email : "-");
        tvNoTelepon.setText(noTelepon);
        tvAlamat.setText(alamat);
        tvNoRombongan.setText(noRombongan);
        tvNamaKetua.setText(namaKetua);
        tvJenisKelamin.setText(jenisKelamin);

        // Tampilkan foto profil jika ada
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            try {
                Glide.with(this)
                        .load(profileImageUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_account_circle)
                        .into(imgAvatar);
            } catch (Exception e) {
                imgAvatar.setImageResource(R.drawable.ic_account_circle);
            }
        } else {
            imgAvatar.setImageResource(R.drawable.ic_account_circle);
        }

        btnChangePhoto.setOnClickListener(v -> checkGalleryPermissionAndPickImage());
        imgAvatar.setOnClickListener(v -> checkGalleryPermissionAndPickImage());

        btnSavePhoto.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadImageToFirebaseAndSave();
            } else {
                Toast.makeText(this, "Silakan pilih gambar terlebih dahulu.", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> handleBack());
    }

    private void handleBack() {
        if (fromGoogleLogin) {
            Intent intent = new Intent(this, dashboard.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop()
                    .placeholder(R.drawable.ic_account_circle)
                    .error(R.drawable.ic_account_circle)
                    .into(imgAvatar);
        }
    }

    private void checkGalleryPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= 34) { // Android 14+
            if (ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_VISUAL_USER_SELECTED")
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{"android.permission.READ_MEDIA_VISUAL_USER_SELECTED"},
                        PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6-12
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        } else {
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Izin akses galeri diperlukan untuk memilih foto.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebaseAndSave() {
        if (selectedImageUri == null || uid == null) return;
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mengunggah foto...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Simpan dengan nama unik: profile_images/UID.jpg (menimpa jika ada)
        String fileName = "profile_images/" + uid + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);

        // putFile() akan menimpa file lama dengan nama yang sama di Firebase Storage
        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    profileImageUrl = uri.toString();
                    userRef.child("profileImageUrl").setValue(profileImageUrl)
                        .addOnSuccessListener(aVoid -> {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Foto profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            Glide.with(this)
                                    .load(profileImageUrl)
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_account_circle)
                                    .error(R.drawable.ic_account_circle)
                                    .into(imgAvatar);
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Gagal menyimpan foto profil", Toast.LENGTH_SHORT).show();
                        });
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    android.util.Log.e("ProfileInfoActivity", "Gagal mendapatkan URL foto: " + e.getMessage(), e);
                    Toast.makeText(this, "Gagal mendapatkan URL foto", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    android.util.Log.e("ProfileInfoActivity", "Gagal mengunggah foto: " + e.getMessage(), e);
                    Toast.makeText(this, "Gagal mengunggah foto", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleBack();
    }
}