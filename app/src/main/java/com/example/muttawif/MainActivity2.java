package com.example.muttawif;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity2 extends AppCompatActivity {

    private FirebaseStorage storage;
    private static final String FIREBASE_BUCKET = "gs://muttawif-data.appspot.com";
    private static final String DOA_FOLDER = "doa/";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        storage = FirebaseStorage.getInstance();

        // Set tombol-tombol agar saat diklik memunculkan popup doa
        setupButton(R.id.buttonNiatHaji, "niat_haji.txt");
        setupButton(R.id.buttonNiatHajiQiran, "niat_haji_qiran.txt");
        setupButton(R.id.buttonDoaSelesaiIhram, "doa_selesai_berihram.txt");
        setupButton(R.id.buttonDoaTalbiyah, "doa_talbiyah.txt");
        setupButton(R.id.buttonDoaMasukMakkah, "doa_masuk_makkah.txt");
        setupButton(R.id.buttonDoaMasjidilHaram, "doa_masuk_masjidil_haram.txt");
        setupButton(R.id.buttonDoaLihatKaabah, "doa_lihat_kaabah.txt");
        setupButton(R.id.buttonDoaTawaf, "doa_thawaf.txt");
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

        // Tombol Back
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
    }

    private void setupButton(int buttonId, String fileName) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> showDoaPopup(fileName));
    }

    private void showDoaPopup(String fileName) {
        try {
            StorageReference storageRef = storage.getReferenceFromUrl(FIREBASE_BUCKET)
                    .child(DOA_FOLDER + fileName);

            File localFile = File.createTempFile("doa", ".txt");

            storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                String doaText = readFile(localFile);
                showPopup(doaText);
            }).addOnFailureListener(e -> {
                Toast.makeText(MainActivity2.this, "Gagal mengambil doa: " + fileName, Toast.LENGTH_LONG).show();
            });

        } catch (IOException e) {
            Toast.makeText(this, "Gagal membaca file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String readFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return new String(data);
        } catch (IOException e) {
            e.printStackTrace();
            return "Gagal membaca doa.";
        }
    }

    private void showPopup(String doaText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ScrollView scrollView = new ScrollView(this);
        TextView textView = new TextView(this);
        textView.setText(doaText);
        textView.setPadding(32, 32, 32, 32);

        scrollView.addView(textView);

        builder.setView(scrollView)
                .setPositiveButton("Tutup", (dialog, which) -> dialog.dismiss())
                .setTitle("Doa");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}