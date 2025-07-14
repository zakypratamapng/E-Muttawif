package com.example.muttawif;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TataUmrohActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tata_umroh);

        TextView textView = findViewById(R.id.textUmroh);

        String tataUmroh = "🕋 TATA CARA UMROH (DENGAN PENJELASAN):\n\n" +
                "1. 🕌 *Niat Umroh dari Miqat*\n" +
                "   - Ucapkan: “Labbaikallahumma Umratan”.\n" +
                "   - Berniat dari miqat (sesuai rute masuk ke Makkah).\n\n" +
                "2. 👕 *Memakai Pakaian Ihram dan Menjauhi Larangan*\n" +
                "   - Pria: dua kain ihram tanpa jahitan.\n" +
                "   - Wanita: pakaian syar’i, tidak menggunakan cadar dan sarung tangan.\n\n" +
                "3. 🕋 *Thawaf*\n" +
                "   - Mengelilingi Ka’bah 7 putaran dimulai dari Hajar Aswad.\n" +
                "   - Setiap putaran dibaca doa dan dzikir.\n\n" +
                "4. 🧎 *Salat Dua Rakaat di Belakang Maqam Ibrahim*\n" +
                "   - Jika memungkinkan, atau di tempat mana pun dalam Masjidil Haram.\n\n" +
                "5. 🏃 *Sa’i antara Shafa dan Marwah*\n" +
                "   - 7 kali perjalanan: Shafa ke Marwah = 1, dan seterusnya hingga 7.\n" +
                "   - Boleh istirahat di tengah, tetap menjaga urutan.\n\n" +
                "6. ✂️ *Tahallul*\n" +
                "   - Pria cukur habis atau pendek, wanita potong ujung rambut.\n" +
                "   - Setelah tahallul, semua larangan ihram telah selesai.\n\n" +
                "7. ✅ *Umroh Selesai*\n" +
                "   - Ibadah umroh selesai dan diperbolehkan aktivitas biasa kembali.";

        textView.setText(tataUmroh);
        ImageButton buttonBack = findViewById(R.id.btnBack);
        if (buttonBack != null) {
            buttonBack.setOnClickListener(v -> finish());
        }
    }
}
