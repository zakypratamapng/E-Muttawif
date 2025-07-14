package com.example.muttawif;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TataHajiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tata_haji);

        TextView textView = findViewById(R.id.textHaji);

        String tataHaji = "🕋 TATA CARA HAJI TAMATTU’ (DENGAN PENJELASAN):\n\n" +
                "1. 🕌 *Niat dan Ihram dari Miqat*\n" +
                "   - Dilakukan di miqat (Dhul Hulaifah, Yalamlam, Qarnul Manazil, dll).\n" +
                "   - Ucapkan: “Labbaikallahumma Hajjan”.\n\n" +
                "2. 👕 *Memakai Pakaian Ihram dan Menjauhi Larangan*\n" +
                "   - Untuk pria: dua lembar kain putih tanpa jahitan.\n" +
                "   - Wanita: pakaian biasa yang menutup aurat.\n\n" +
                "3. 🕋 *Umrah Dulu (Thawaf – Sa’i – Tahallul)*\n" +
                "   - Lakukan thawaf 7 putaran mengelilingi Ka’bah.\n" +
                "   - Sa’i dari Shafa ke Marwah 7 kali.\n" +
                "   - Potong rambut sebagai tanda tahallul.\n\n" +
                "4. ⛰️ *8 Dzulhijjah (Tarwiyah) – Mulai Haji*\n" +
                "   - Berniat haji dari Mekkah, kembali berihram.\n" +
                "   - Menuju Mina dan bermalam.\n\n" +
                "5. ⛰️ *9 Dzulhijjah – Wukuf di Arafah*\n" +
                "   - Waktu utama: dari Dzuhur hingga Maghrib.\n" +
                "   - Puncak haji. Banyak berdoa, dzikir, dan istighfar.\n\n" +
                "6. 🌌 *Malam 10 Dzulhijjah – Mabit di Muzdalifah*\n" +
                "   - Bermalam dan mengumpulkan batu untuk jumrah.\n" +
                "   - Salat Maghrib dan Isya dijamak ta’khir.\n\n" +
                "7. 🪨 *10 Dzulhijjah – Melempar Jumrah Aqabah*\n" +
                "   - Lempar 7 batu kecil sambil mengucap “Allahu Akbar”.\n\n" +
                "8. 🐑 *Penyembelihan Hewan Qurban*\n" +
                "   - Wajib bagi haji tamattu’ dan qiran.\n\n" +
                "9. ✂️ *Cukur Rambut (Tahallul Awal)*\n" +
                "   - Pria dianjurkan cukur habis, wanita cukup potong seujung jari.\n\n" +
                "10. 🕋 *Thawaf Ifadah & Sa’i (kalau belum)*\n" +
                "    - Rukun haji. Wajib dikerjakan sebelum meninggalkan Makkah.\n\n" +
                "11. ⛺ *11–13 Dzulhijjah – Mabit di Mina*\n" +
                "    - Bermalam di Mina selama hari Tasyriq.\n" +
                "    - Melempar tiga jumrah setiap hari (Ula, Wustha, Aqabah).\n\n" +
                "12. 🚶‍♂️ *Thawaf Wada’*\n" +
                "    - Dilakukan sebelum keluar dari Makkah untuk pulang.";

        textView.setText(tataHaji);
        ImageButton buttonBack = findViewById(R.id.btnBack);
        if (buttonBack != null) {
            buttonBack.setOnClickListener(v -> finish());
        }
    }
}
