package com.example.muttawif;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        storage = FirebaseStorage.getInstance();

        // Haji & Umrah buttons (no duplicates)
        setupButton(R.id.buttonNiatHaji, "niat_haji.txt");
        setupButton(R.id.buttonNiatHajiQiran, "niat_haji_qiran.txt");
        setupButton(R.id.buttonNiatUmrah, "niat_umrah.txt");
        setupButton(R.id.buttonTalbiyah, "talbiyah.txt");
        setupButton(R.id.buttonDoaSelesaiIhram, "doa_selesai_berihram.txt");
        setupButton(R.id.buttonDoaMasukMakkah, "doa_masuk_makkah.txt");
        setupButton(R.id.buttonMasukMasjidilHaram, "doa_masuk_masjidil_haram.txt");
        setupButton(R.id.buttonLihatKaabah, "doa_lihat_kaabah.txt");
        setupButton(R.id.buttonSebelumThawaf, "doa_sebelum_thawaf.txt");
        setupButton(R.id.buttonHajarAswad, "thawaf/hajar_aswad.txt");
        setupButton(R.id.buttonPutaranThawaf, "doa_putaran_thawaf.txt");
        setupButton(R.id.buttonRukunYamani, "thawaf/rukun_yamani.txt");
        setupButton(R.id.buttonAntaraRukunYamaniHajarAswad, "doa_antara_rukun_yamani_hajar_aswad.txt");
        setupButton(R.id.buttonSelesaiThawaf, "thawaf/selesai_thawaf.txt");
        setupButton(R.id.buttonMaqamIbrahim, "thawaf/maqam_ibrahim.txt");
        setupButton(R.id.buttonAirZamzam, "doa_air_zamzam.txt");
        setupButton(R.id.buttonNaikShafa, "sai/bukit.txt");
        setupButton(R.id.buttonSaiShafaMarwah, "doa_sai_shafa_marwah.txt");
        setupButton(R.id.buttonNaikMarwah, "sai/bukit.txt");
        setupButton(R.id.buttonLintasanSai, "doa_lintasan_sai.txt");
        setupButton(R.id.buttonSelesaiSai, "doa_selesai_sai.txt");
        setupButton(R.id.buttonTahallul, "doa_tahallul.txt");
        setupButton(R.id.buttonDoaTawaf, "doa_thawaf.txt");
        setupButton(R.id.buttonDoaSai, "doa_sai.txt");
        setupButton(R.id.buttonDoaMasukArafah, "doa_masuk_arafah.txt");
        setupButton(R.id.buttonDoaWukuf, "doa_wukuf.txt");
        setupButton(R.id.buttonDoaSampaiMuzdalifah, "doa_sampai_muzdalifah.txt");
        setupButton(R.id.buttonDoaSampaiMina, "doa_sampai_mina.txt");
        setupButton(R.id.buttonDoaLemparJumrah, "doa_lempar_jumrah.txt");
        setupButton(R.id.buttonDoaMasukMadinah, "doa_masuk_madinah.txt");
        setupButton(R.id.buttonDoaMasukMasjidNabawi, "doa_masuk_masjid_nabawi.txt");
        setupButton(R.id.buttonDoaPulangHaji, "doa_pulang_haji.txt");

        ImageButton backBtn = findViewById(R.id.buttonBack);
        backBtn.setOnClickListener(v -> finish());
    }

    private void setupButton(int buttonId, String fileName) {
        Button button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> showDoaPopup(fileName));
        }
    }

    private void showDoaPopup(String fileName) {
        try {
            StorageReference storageRef = storage.getReferenceFromUrl(FIREBASE_BUCKET)
                    .child(DOA_FOLDER + fileName);

            File localFile = File.createTempFile("doa", ".txt");

            storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                String doaText = readFile(localFile);
                showPopup(doaText);
            }).addOnFailureListener(e -> Toast.makeText(MainActivity2.this, "Failed to fetch doa: " + fileName, Toast.LENGTH_LONG).show());

        } catch (IOException e) {
            Toast.makeText(this, "Failed to read file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String readFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            int read = fis.read(data);
            if (read != data.length) {
                return "Failed to read doa (incomplete read).";
            }
            return new String(data);
        } catch (IOException e) {
            return "Failed to read doa.";
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
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .setTitle("Doa");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}