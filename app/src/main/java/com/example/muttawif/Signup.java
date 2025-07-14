package com.example.muttawif;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Signup extends AppCompatActivity {

    private EditText editNama, editEmail, editPassword, editNoTelepon, editAlamat, editNoRombongan, editNamaKetua;
    private ImageView passwordToggle, imgProfileSignup;
    private RadioGroup radioGroupGender;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private GoogleSignInClient googleSignInClient;
    private boolean isPasswordVisible = false;
    private Button btnPilihFotoSignup;
    private Uri selectedImageUri;
    private String profileImageUrl = null;
    private static final int PICK_IMAGE_REQUEST = 201;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 202;

    private static final String TAG = "SignupActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editNama = findViewById(R.id.editNama);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editNoTelepon = findViewById(R.id.editNoTelepon);
        editAlamat = findViewById(R.id.editAlamat);
        editNoRombongan = findViewById(R.id.editNoRombongan);
        editNamaKetua = findViewById(R.id.editNamaKetua);
        Button buttonSignup = findViewById(R.id.buttonSignup);
        Button buttonGoogleSignup = findViewById(R.id.buttonGoogleSignup);
        Button buttonKembali = findViewById(R.id.buttonKembali);
        passwordToggle = findViewById(R.id.passwordToggle);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        imgProfileSignup = findViewById(R.id.imgProfileSignup);
        btnPilihFotoSignup = findViewById(R.id.btnPilihFotoSignup);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        buttonSignup.setOnClickListener(v -> checkEmailBeforeSignup());
        buttonGoogleSignup.setOnClickListener(v -> signInWithGoogle());
        buttonKembali.setOnClickListener(v -> finish());
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
        btnPilihFotoSignup.setOnClickListener(v -> checkGalleryPermissionAndPickImage());
        imgProfileSignup.setOnClickListener(v -> checkGalleryPermissionAndPickImage());
    }

    private void checkEmailBeforeSignup() {
        String email = editEmail.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Masukkan email yang valid!");
            editEmail.requestFocus();
            return;
        }

        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean emailExists = !task.getResult().getSignInMethods().isEmpty();
                        if (emailExists) {
                            Toast.makeText(Signup.this, "Email sudah digunakan! Silakan login.", Toast.LENGTH_SHORT).show();
                        } else {
                            registerUser();
                        }
                    } else {
                        Log.e(TAG, "Error checking email: ", task.getException());
                        Toast.makeText(Signup.this, "Terjadi kesalahan. Coba lagi nanti.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser() {
        String nama = editNama.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
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
            jenisKelamin = "";
            Toast.makeText(this, "Pilih jenis kelamin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nama.isEmpty() || password.isEmpty() || noTelepon.isEmpty() || alamat.isEmpty() || noRombongan.isEmpty() || namaKetua.isEmpty()) {
            Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            editPassword.setError("Password minimal 6 karakter!");
            editPassword.requestFocus();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menyimpan data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            if (selectedImageUri != null) {
                                // Simpan dengan nama unik: profile_images/UID.jpg (menimpa jika ada)
                                String fileName = "profile_images/" + uid + ".jpg";
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);
                                storageRef.putFile(selectedImageUri)
                                        .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            profileImageUrl = uri.toString();
                                            saveUserToDatabase(uid, nama, email, noTelepon, alamat, noRombongan, namaKetua, jenisKelamin, profileImageUrl, progressDialog);
                                        }).addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            Log.e(TAG, "Gagal mendapatkan URL foto: " + e.getMessage(), e);
                                            Toast.makeText(this, "Gagal mendapatkan URL foto", Toast.LENGTH_SHORT).show();
                                        }))
                                        .addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            Log.e(TAG, "Gagal mengunggah foto: " + e.getMessage(), e);
                                            Toast.makeText(this, "Gagal mengunggah foto", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                saveUserToDatabase(uid, nama, email, noTelepon, alamat, noRombongan, namaKetua, jenisKelamin, null, progressDialog);
                            }
                        }
                    } else {
                        progressDialog.dismiss();
                        Log.e(TAG, "Signup error: ", task.getException());
                        Toast.makeText(Signup.this, "Pendaftaran gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(String uid, String nama, String email, String noTelepon, String alamat, String noRombongan, String namaKetua, String jenisKelamin, String profileImageUrl, ProgressDialog progressDialog) {
        User newUser = new User(uid, nama, email, noTelepon, alamat, noRombongan, namaKetua, jenisKelamin, profileImageUrl);
        databaseReference.child(uid).setValue(newUser)
                .addOnCompleteListener(databaseTask -> {
                    progressDialog.dismiss();
                    if (databaseTask.isSuccessful()) {
                        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("nama", nama);
                        editor.putString("email", email);
                        editor.putString("noTelepon", noTelepon);
                        editor.putString("alamat", alamat);
                        editor.putString("noRombongan", noRombongan);
                        editor.putString("namaKetua", namaKetua);
                        editor.putString("jenisKelamin", jenisKelamin);
                        if (profileImageUrl != null) editor.putString("profileImageUrl", profileImageUrl);
                        editor.apply();

                        Toast.makeText(Signup.this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Signup.this, login.class));
                        finish();
                    } else {
                        Log.e(TAG, "Database error: ", databaseTask.getException());
                        Toast.makeText(Signup.this, "Gagal menyimpan data ke database", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 9001);
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
                    .into(imgProfileSignup);
        } else if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult();
                firebaseAuthWithGoogle(account);
            } catch (Exception e) {
                Log.e(TAG, "Google Sign-In gagal", e);
                Toast.makeText(this, "Google Sign-In gagal", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(this, "Login dengan Google berhasil", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, dashboard.class));
                            finish();
                        }
                    } else {
                        Log.e(TAG, "Google Auth error: ", task.getException());
                        Toast.makeText(this, "Autentikasi Google gagal", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_visibility_off);
        } else {
            editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_visibility);
        }
        isPasswordVisible = !isPasswordVisible;
        editPassword.setSelection(editPassword.getText().length());
    }
}