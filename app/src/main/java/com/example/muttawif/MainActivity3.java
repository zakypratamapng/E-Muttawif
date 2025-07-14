package com.example.muttawif;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        ImageView backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(view -> finish());

        TextView fiqihUmrahText = findViewById(R.id.fiqihUmrahText);
        // Convert Markdown bold to HTML bold
        String detail = getFiqihUmrahDetail()
                .replace("**", "<b>")
                .replace("<b><b>", "<b>")
                .replace("</b><b>", "")
                .replace("\n", "<br>");
        fiqihUmrahText.setText(android.text.Html.fromHtml(detail));
    }

    private String getFiqihUmrahDetail() {
        return "FIQIH UMRAH – PENJELASAN LENGKAP\n\n" +
                "1. **Ihram di Miqot**\n" +
                "   Jamaah wajib memulai niat umrah dari miqot yang telah ditentukan tergantung dari arah kedatangan. " +
                "Sebelum ihram, dianjurkan untuk mandi, memakai wangi-wangian (khusus laki-laki), dan mengenakan pakaian ihram " +
                "yaitu dua lembar kain putih tanpa jahitan (untuk laki-laki). Lalu mengucapkan niat umrah: \"Labbaik Allahumma Umrah.\"\n\n" +

                "2. **Thawaf di Ka'bah**\n" +
                "   Setelah tiba di Masjidil Haram, jamaah melakukan thawaf sebanyak 7 putaran mengelilingi Ka'bah dengan memulai dari Hajar Aswad " +
                "dan berakhir di titik yang sama. Thawaf dilakukan berlawanan arah jarum jam dan disertai dengan dzikir serta doa. " +
                "Dianjurkan untuk mencium atau memberi isyarat ke Hajar Aswad jika memungkinkan.\n\n" +

                "3. **Sholat di Maqam Ibrahim**\n" +
                "   Setelah thawaf, disunnahkan untuk sholat 2 rakaat di belakang Maqam Ibrahim. " +
                "Jika terlalu padat, boleh di tempat lain di area Masjidil Haram. Bacaan surah yang dianjurkan setelah Al-Fatihah adalah Al-Kafirun dan Al-Ikhlas.\n\n" +

                "4. **Minum Air Zamzam**\n" +
                "   Setelah sholat, jamaah disunnahkan untuk minum air zamzam sambil berdoa sesuai kebutuhan. " +
                "Disunnahkan berdiri saat minum dan menghadap kiblat.\n\n" +

                "5. **Sa’i antara Shafa dan Marwah**\n" +
                "   Setelah minum zamzam, jamaah menuju bukit Shafa untuk memulai sa’i. Dimulai dari Shafa dan berakhir di Marwah, dilakukan sebanyak 7 kali " +
                "(1 perjalanan dari Shafa ke Marwah dihitung 1 kali). Selama sa’i disunnahkan membaca doa dan dzikir, serta berlari kecil di area lampu hijau untuk laki-laki.\n\n" +

                "6. **Tahallul (Cukur atau Potong Rambut)**\n" +
                "   Setelah selesai sa’i, jamaah melakukan tahallul yaitu mencukur habis rambut kepala (gundul) atau memotong sedikit (minimal 3 helai rambut) " +
                "bagi laki-laki. Wanita cukup memotong rambut sepanjang satu ruas jari. Setelah tahallul, larangan ihram menjadi gugur dan umrah pun selesai.\n\n" +

                "Catatan Tambahan:\n" +
                "- Umrah dapat dilakukan dalam waktu singkat (beberapa jam saja)\n" +
                "- Pastikan menjaga niat, adab, dan kesabaran selama menjalankan ibadah.\n" +
                "- Disarankan memperbanyak doa, dzikir, dan membaca Al-Quran selama di tanah suci.\n\n" +
                "Semoga ibadah umrah Anda diterima oleh Allah dan menjadi umrah yang mabrurah. Aamiin.";
    }
}