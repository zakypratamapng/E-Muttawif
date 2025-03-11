package com.example.muttawif;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    private EditText editNama, editEmail, editPassword, editNoTelepon, editAlamat, editNoRombongan;
    private Button buttonSignup, buttonGoogleSignup, buttonKembali;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private GoogleSignInClient googleSignInClient;

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
        buttonSignup = findViewById(R.id.buttonSignup);
        buttonGoogleSignup = findViewById(R.id.buttonGoogleSignup);
        buttonKembali = findViewById(R.id.buttonKembali);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://muttawif-data-default-rtdb.firebaseio.com/")
                .getReference("users");

        // Konfigurasi Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        buttonSignup.setOnClickListener(v -> registerUser());
        buttonGoogleSignup.setOnClickListener(v -> signInWithGoogle());
        buttonKembali.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String nama = editNama.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String noTelepon = editNoTelepon.getText().toString().trim();
        String alamat = editAlamat.getText().toString().trim();
        String noRombongan = editNoRombongan.getText().toString().trim();

        if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || noTelepon.isEmpty() || alamat.isEmpty() || noRombongan.isEmpty()) {
            Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Masukkan email yang valid!");
            editEmail.requestFocus();
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
                            User newUser = new User(uid, nama, email, noTelepon, alamat, noRombongan);

                            databaseReference.child(uid).setValue(newUser)
                                    .addOnCompleteListener(databaseTask -> {
                                        if (databaseTask.isSuccessful()) {
                                            Toast.makeText(Signup.this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Signup.this, login.class));
                                            finish();
                                        } else {
                                            Toast.makeText(Signup.this, "Gagal menyimpan data ke database", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(Signup.this, "Pendaftaran gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult();
                firebaseAuthWithGoogle(account);
            } catch (Exception e) {
                Toast.makeText(this, "Google Sign-In gagal!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    User newUser = new User(uid, user.getDisplayName(), user.getEmail(), "", "", "");
                    databaseReference.child(uid).setValue(newUser)
                            .addOnCompleteListener(databaseTask -> {
                                if (databaseTask.isSuccessful()) {
                                    Toast.makeText(this, "Pendaftaran Google Berhasil!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Signup.this, login.class));
                                    finish();
                                }
                            });
                }
            } else {
                Toast.makeText(Signup.this, "Autentikasi Google Gagal!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
