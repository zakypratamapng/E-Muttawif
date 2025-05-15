package com.example.muttawif;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DoaPopupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_doa); // gunakan layout XML buatanmu

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView doaText = findViewById(R.id.doaTextView); // pastikan ID cocok
        String doaIsi = getIntent().getStringExtra("doaIsi");
        if (doaIsi != null) {
            doaText.setText(doaIsi);
        }
    }
}
