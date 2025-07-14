package com.example.muttawif;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class IfradActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ifrad);

        ImageView icLogout = findViewById(R.id.icLogout);
        icLogout.setOnClickListener(v -> finish());

        TextView tvInfo = findViewById(R.id.tvInfo);
        tvInfo.setBackgroundColor(getResources().getColor(android.R.color.white));
        tvInfo.setMovementMethod(new ScrollingMovementMethod());

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("doa/Ifrad.txt");
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(bytes -> {
                    String text = new String(bytes);
                    tvInfo.setText(text);
                })
                .addOnFailureListener(e -> tvInfo.setText("Failed to load info."));
    }
}