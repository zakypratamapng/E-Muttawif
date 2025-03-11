package com.example.muttawif;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity2 extends AppCompatActivity {

    private FirebaseStorage storage;
    private static final String FIREBASE_BUCKET = "gs://muttawif-data.appspot.com";  // Gunakan bucket storage utama
    private static final String DOA_FOLDER = "doa/";  // Folder doa di Firebase Storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        storage = FirebaseStorage.getInstance();

        // Pastikan user login sebelum akses file
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu!", Toast.LENGTH_LONG).show();
            finish(); // Tutup activity jika belum login
            return;
        }

        // Hubungkan tombol dengan Firebase Storage
        setupButton(R.id.buttonNiatHaji, "niat_haji.txt");
        setupButton(R.id.buttonNiatHajiQiran, "niat_haji_qiran.txt");
        setupButton(R.id.buttonDoaSelesaiIhram, "doa_selesai_berihram.txt");
        setupButton(R.id.buttonDoaTalbiyah, "doa_talbiyah.txt");
        setupButton(R.id.buttonDoaMasukMakkah, "doa_masuk_makkah.txt");
        setupButton(R.id.buttonDoaMasjidilHaram, "doa_masuk_masjidil_haram.txt");
        setupButton(R.id.buttonDoaLihatKaabah, "doa_lihat_kaabah.txt");
        setupButton(R.id.buttonDoaTawaf, "doa_tawaf.txt");
        setupButton(R.id.buttonDoaSai, "doa_sai.txt");
        setupButton(R.id.buttonDoaGuntingRambut, "doa_gunting_rambut.txt");
        setupButton(R.id.buttonDoaMasukArafah, "doa_masuk_arafah.txt");
        setupButton(R.id.buttonDoaWukuf, "doa_wukuf.txt");
        setupButton(R.id.buttonDoaSampaiMuzdalifah, "doa_sampai_muzdalifah.txt");
        setupButton(R.id.buttonDoaSampaiMina, "doa_sampai_mina.txt");
        setupButton(R.id.buttonDoaLemparJumrah, "doa_lempar_jumrah.txt");
        setupButton(R.id.buttonDoaMasukMadinah, "doa_masuk_madinah.txt");
        setupButton(R.id.buttonDoaMasukMasjidNabawi, "doa_masuk_masjid_nabawi.txt");
        setupButton(R.id.buttonDoaPulangHaji, "doa_pulang_haji.txt");

        // Tombol kembali
        setupBackButton();
    }

    private void setupButton(int buttonId, String fileName) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> showDoaPopup(fileName));
    }

    private void setupBackButton() {
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish()); // Kembali ke aktivitas sebelumnya
    }

    private void showDoaPopup(String fileName) {
        try {
            // Ambil referensi file dari Firebase Storage
            StorageReference storageRef = storage.getReferenceFromUrl(FIREBASE_BUCKET)
                    .child(DOA_FOLDER + fileName);
            File localFile = File.createTempFile("doa", ".txt");

            // Download file dari Firebase Storage
            storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                String doaText = readFile(localFile);
                showPopup(doaText);
            }).addOnFailureListener(e -> {
                Toast.makeText(MainActivity2.this, "Gagal mengambil doa: " + fileName, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            });

        } catch (IOException e) {
            Toast.makeText(MainActivity2.this, "Error file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String readFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            return new String(data);
        } catch (IOException e) {
            e.printStackTrace();
            return "Gagal membaca file!";
        }
    }

    private void showPopup(String doaText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ScrollView scrollView = new ScrollView(this);
        TextView textView = new TextView(this);
        textView.setText(doaText);
        textView.setPadding(20, 20, 20, 20);
        scrollView.addView(textView);

        builder.setView(scrollView)
                .setTitle("Doa")
                .setPositiveButton("Tutup", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
