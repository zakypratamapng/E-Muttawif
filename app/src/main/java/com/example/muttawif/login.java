package com.example.muttawif;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class login extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private ImageView passwordToggle;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private GoogleSignInClient googleSignInClient;
    private boolean isPasswordVisible = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Cek apakah pengguna sudah login
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(login.this, dashboard.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonGoogleLogin = findViewById(R.id.buttonGoogleLogin);
        Button buttonGotoSignup = findViewById(R.id.buttonGotoSignup);
        passwordToggle = findViewById(R.id.passwordToggle);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        buttonLogin.setOnClickListener(v -> loginUser());
        buttonGoogleLogin.setOnClickListener(v -> signInWithGoogle());
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
        buttonGotoSignup.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, Signup.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Masukkan email yang valid!");
            editEmail.requestFocus();
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            editPassword.setError("Password minimal 6 karakter!");
            editPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            fetchUserData(user.getUid());
                        }
                    } else {
                        Toast.makeText(login.this, "Login gagal: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserData(String uid) {
        databaseReference.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot dataSnapshot = task.getResult();
                String nama = dataSnapshot.child("nama").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String noTelepon = dataSnapshot.child("noTelepon").getValue(String.class);
                String alamat = dataSnapshot.child("alamat").getValue(String.class);
                String noRombongan = dataSnapshot.child("noRombongan").getValue(String.class);
                String namaKetua = dataSnapshot.child("namaKetua").getValue(String.class);

                // Simpan ke SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("uid", uid);
                editor.putString("nama", nama);
                editor.putString("email", email);
                editor.putString("noTelepon", noTelepon);
                editor.putString("alamat", alamat);
                editor.putString("noRombongan", noRombongan);
                editor.putString("namaKetua", namaKetua);
                editor.apply();

                Toast.makeText(login.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(login.this, dashboard.class));
                finish();
            } else {
                Toast.makeText(login.this, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show();
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
            if (task.isSuccessful()) {
                GoogleSignInAccount account = task.getResult();
                firebaseAuthWithGoogle(account);
            } else {
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
