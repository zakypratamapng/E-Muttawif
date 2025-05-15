package com.example.muttawif;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class Signup extends AppCompatActivity {

    private EditText editNama, editEmail, editPassword, editNoTelepon, editAlamat, editNoRombongan, editNamaKetua;
    private ImageView passwordToggle;
    private RadioGroup radioGroupGender;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private GoogleSignInClient googleSignInClient;
    private boolean isPasswordVisible = false;

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

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            User newUser = new User(uid, nama, email, noTelepon, alamat, noRombongan, namaKetua, jenisKelamin);
                            databaseReference.child(uid).setValue(newUser)
                                    .addOnCompleteListener(databaseTask -> {
                                        if (databaseTask.isSuccessful()) {
                                            // Save all info to SharedPreferences
                                            SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString("nama", nama);
                                            editor.putString("email", email);
                                            editor.putString("noTelepon", noTelepon);
                                            editor.putString("alamat", alamat);
                                            editor.putString("noRombongan", noRombongan);
                                            editor.putString("namaKetua", namaKetua);
                                            editor.putString("jenisKelamin", jenisKelamin);
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
                    } else {
                        Log.e(TAG, "Signup error: ", task.getException());
                        Toast.makeText(Signup.this, "Pendaftaran gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 9001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9001) {
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