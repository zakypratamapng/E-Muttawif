package com.example.muttawif;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity5 extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    LinearLayout menuPersiapanHaji, menuFiqihUmrah, menuFiqihHaji,
            menuDzikirDoa, menuTataUmroh, menuTataHaji;

    // Launcher untuk request permission overlay
    private ActivityResultLauncher<Intent> overlayPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        // Inisialisasi launcher untuk izin overlay
        overlayPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Settings.canDrawOverlays(this)) {
                        startLocationService();
                    } else {
                        Toast.makeText(this, "Izin overlay diperlukan agar doa dapat ditampilkan", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Cek izin lokasi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            checkOverlayPermission(); // Cek izin overlay jika sudah ada izin lokasi
        }

        // Inisialisasi menu dan tombol back tetap sama...
        menuPersiapanHaji = findViewById(R.id.menuPersiapanHaji);
        menuFiqihUmrah = findViewById(R.id.menuFiqihUmrah);
        menuFiqihHaji = findViewById(R.id.menuFiqihHaji);
        menuDzikirDoa = findViewById(R.id.menuDzikirDoa);
        menuTataUmroh = findViewById(R.id.menuTataUmroh);
        menuTataHaji = findViewById(R.id.menuTataHaji);

        menuPersiapanHaji.setOnClickListener(view -> openActivity(PersiapanHajiActivity.class));
        menuFiqihUmrah.setOnClickListener(view -> openActivity(MainActivity3.class));
        menuFiqihHaji.setOnClickListener(view -> openActivity(MainActivity4.class));
        menuDzikirDoa.setOnClickListener(view -> openActivity(MainActivity2.class));
        menuTataUmroh.setOnClickListener(view -> openActivity(TataUmrohActivity.class));
        menuTataHaji.setOnClickListener(view -> openActivity(TataHajiActivity.class));

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        if (buttonBack != null) {
            buttonBack.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity5.this, dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        Button btnThawafWada = findViewById(R.id.btnThawafWada);
        btnThawafWada.setOnClickListener(v -> {
            Intent intent = new Intent("com.example.muttawif.ACTION_START_THAWAF_WADA_MANUAL");
            sendBroadcast(intent);
            Toast.makeText(this, "Permintaan thawaf wada manual dikirim", Toast.LENGTH_SHORT).show();
        });
    }

    private void openActivity(Class<?> targetActivity) {
        Intent intent = new Intent(MainActivity5.this, targetActivity);
        startActivity(intent);
    }

    private void startLocationService() {
        try {
            Intent serviceIntent = new Intent(this, LocationService.class);
            serviceIntent.putExtra("startSaiManually", true); // ini yang bikin sesi Sai bisa dimulai langsung
            startService(serviceIntent);

            // Mulai NotifService
            try {
                Intent notifServiceIntent = new Intent(this, NotifService.class);
                startService(notifServiceIntent);
                Toast.makeText(this, "Notif Service berjalan", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Gagal memulai Notif Service", Toast.LENGTH_SHORT).show();
                Log.e("NotifService", "Error: ", e);
            }

            Toast.makeText(this, "Location Service berjalan dan Sai bisa diuji langsung", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal memulai Location Service", Toast.LENGTH_SHORT).show();
            Log.e("LocationService", "Error: ", e);
        }
    }


    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            overlayPermissionLauncher.launch(intent);
        } else {
            startLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkOverlayPermission();
            } else {
                Toast.makeText(this, "Izin lokasi diperlukan untuk fitur ini", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}