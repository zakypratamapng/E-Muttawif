package com.example.muttawif;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.TimeZone;

public class NotifService extends Service {

    private LocationManager locationManager;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private final Set<String> shownEvents = new HashSet<>();
    private final Queue<Runnable> dialogQueue = new LinkedList<>();
    private boolean isDialogShowing = false;

    // Area coordinates (lat, lng, radius in meters)
    private static final double[] ARAFAH = {21.3556, 39.9836, 1500};
    private static final double[] MUZDALIFAH = {21.3891, 39.8947, 1500};
    private static final double[] MINA = {21.4225, 39.8947, 2000};
    private static final double[] JAMARAT_AQABAH = {21.4192, 39.8891, 100};
    private static final double[] JAMARAT_ULA = {21.4185, 39.8897, 100};
    private static final double[] JAMARAT_WUSTHA = {21.4188, 39.8894, 100};

    // Checkpoint thawaf dan sai (sama dengan LocationService)
    private static final double[] HAJAR_ASWAD = {-7.269320, 112.797152, 25};
    private static final double[] MAQAM_IBRAHIM = {-7.268773, 112.797069, 25};
    private static final double[] RUKUN_YAMANI = {-7.268329, 112.799057, 25};
    private static final double[][] SAI_LINE1 = {
            { -7.276673, 112.793006 },
            { -7.276673, 112.793220 },
            { -7.276673, 112.793112 }
    };
    private static final double[][] SAI_LINE2 = {
            { -7.277522, 112.793008 },
            { -7.277522, 112.793227 },
            { -7.277522, 112.793112 }
    };

    // State
    private boolean thawafIfadahStarted = false;
    private boolean thawafIfadahDone = false;
    private boolean thawafWadaStarted = false;
    private boolean thawafWadaDone = false;
    private int thawafIfadahCounter = 0;
    private int thawafIfadahCheckpoint = 0;
    private int thawafWadaCounter = 0;
    private int thawafWadaCheckpoint = 0;
    private boolean saiAfterIfadahStarted = false;
    private int saiAfterIfadahCounter = 0;

    private boolean thawafWadaManualRequested = false;

    private Location lastLocation = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        startLocationUpdates();
        startEventMonitor();
        return START_STICKY;
    }

    private void startLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        android.content.pm.PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }
        try {
            String provider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    ? LocationManager.GPS_PROVIDER
                    : LocationManager.NETWORK_PROVIDER;
            locationManager.requestLocationUpdates(provider, 10000, 10, locationListener);
        } catch (SecurityException e) {
            Log.e("NotifService", "Permission error", e);
        }
    }

    private final LocationListener locationListener = location -> {
        lastLocation = location;
        checkLocationEvents(location);
    };

    private void startEventMonitor() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkTimeEvents();
                handler.postDelayed(this, 60 * 1000); // check every minute
            }
        }, 1000);
    }

    private void checkTimeEvents() {
        Date now = new Date();
        UmmalquraCalendar ummalqura = new UmmalquraCalendar();
        ummalqura.setTime(now);
        int hijriMonth = ummalqura.get(UmmalquraCalendar.MONTH) + 1; // 1-based
        int hijriDay = ummalqura.get(UmmalquraCalendar.DAY_OF_MONTH);

        String time = new SimpleDateFormat("HH:mm", Locale.US) {{
            setTimeZone(TimeZone.getTimeZone("Asia/Riyadh"));
        }}.format(now);

        // Wukuf di Arafah: 9 Dzulhijjah, 11:00 - 18:45
        if (hijriMonth == 12 && hijriDay == 9 && time.compareTo("11:00") >= 0 && time.compareTo("18:45") < 0) {
            if (lastLocation != null && isInside(lastLocation, ARAFAH)) {
                showInfoDialogOnce("arafah", "Anda berada di Arafah. Melakukan wukuf.");
            }
        }
        // Mabit di Muzdalifah: malam 10 Dzulhijjah, setelah Maghrib (18:45) hingga Dhuha (08:00)
        if ((hijriMonth == 12 && hijriDay == 9 && time.compareTo("18:45") >= 0) ||
            (hijriMonth == 12 && hijriDay == 10 && time.compareTo("08:00") < 0)) {
            if (lastLocation != null && isInside(lastLocation, MUZDALIFAH)) {
                showInfoDialogOnce("muzdalifah", "Anda berada di Muzdalifah. Melakukan mabit.");
            }
        }
        // Mabit di Mina: 11-13 Dzulhijjah
        if (hijriMonth == 12 && (hijriDay == 11 || hijriDay == 12 || hijriDay == 13)) {
            if (lastLocation != null && isInside(lastLocation, MINA)) {
                showInfoDialogOnce("mina", "Anda berada di Mina. Melakukan mabit.");
            }
        }
    }

    private void checkLocationEvents(Location location) {
        Date now = new Date();
        UmmalquraCalendar ummalqura = new UmmalquraCalendar();
        ummalqura.setTime(now);
        int hijriMonth = ummalqura.get(UmmalquraCalendar.MONTH) + 1; // 1-based
        int hijriDay = ummalqura.get(UmmalquraCalendar.DAY_OF_MONTH);

        // Jamarat Aqabah: 10 Dzulhijjah
        if (hijriMonth == 12 && hijriDay == 10 && isInside(location, JAMARAT_AQABAH)) {
            showInfoDialogOnce("aqabah-10dzulhijjah", "Anda berada di lokasi Jamarat. Hari ini hanya dilakukan Jumrah Aqabah.");
        }
        // Jamarat Ula/Wustha/Aqabah: 11-12 Dzulhijjah
        if (hijriMonth == 12 && (hijriDay == 11 || hijriDay == 12) &&
                (isInside(location, JAMARAT_ULA) || isInside(location, JAMARAT_WUSTHA) || isInside(location, JAMARAT_AQABAH))) {
            String msg = "Anda berada di lokasi Jamarat. Hari ini dilakukan Jumrah Ula, Wustha, dan Aqabah.";
            if (hijriDay == 12) {
                msg += "\nJika Anda ingin melakukan Nafar Tsani, maka Anda dapat menetap satu hari lagi di Mina dan kembali melontar jumrah pada 13 Dzulhijjah.";
            }
            showInfoDialogOnce("jamarat-" + hijriDay, msg);
        }

        // Thawaf Ifadah: setelah Mina/Jamarat selesai, di Ka'bah (checkpoint)
        if (!thawafIfadahDone && minaJamaratDone() && isInside(location, HAJAR_ASWAD)) {
            if (!thawafIfadahStarted) {
                thawafIfadahStarted = true;
                thawafIfadahCounter = 0;
                thawafIfadahCheckpoint = 0;
                playThawafIfadahCheckpoint(location);
            }
        }

        // Setelah thawaf ifadah selesai, mulai sesi sai
        if (thawafIfadahDone && !saiAfterIfadahStarted) {
            saiAfterIfadahStarted = true;
            saiAfterIfadahCounter = 0;
            playSaiAfterIfadahLap(location);
        }

        // Thawaf Wada: otomatis jika sudah thawaf ifadah+sai, atau manual jika diminta
        boolean canStartWada = (thawafIfadahDone && saiAfterIfadahStarted) || thawafWadaManualRequested;
        if (!thawafWadaDone && canStartWada && isInside(location, HAJAR_ASWAD)) {
            if (!thawafWadaStarted) {
                thawafWadaStarted = true;
                thawafWadaCounter = 0;
                thawafWadaCheckpoint = 0;
                playThawafWadaCheckpoint(location);
            }
        }
    }

    // --- Dialog Info ---
    private void showInfoDialogOnce(String eventKey, String message) {
        if (shownEvents.contains(eventKey)) return;
        shownEvents.add(eventKey);
        dialogQueue.add(() -> {
            AlertDialog dialog = new AlertDialog.Builder(getBaseContext())
                    .setTitle("Informasi")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Tutup", (d, which) -> {
                        d.dismiss();
                        isDialogShowing = false;
                        showNextDialog();
                    })
                    .create();
            if (dialog.getWindow() != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dialog.getWindow().setType(android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                } else {
                    dialog.getWindow().setType(android.view.WindowManager.LayoutParams.TYPE_PHONE);
                }
            }
            dialog.show();
            isDialogShowing = true;
        });
        if (!isDialogShowing) {
            showNextDialog();
        }
    }

    // --- Thawaf Ifadah dengan checkpoint (seperti LocationService) ---
    private void playThawafIfadahCheckpoint(Location location) {
        if (thawafIfadahCounter >= 7) {
            thawafIfadahDone = true;
            return;
        }
        switch (thawafIfadahCheckpoint) {
            case 0:
                if (isInside(location, HAJAR_ASWAD)) {
                    playDoa("doa/thawaf/hajar_aswad.txt", "prayer/thawaf/hajar_aswad.mp3", () -> playDoa("doa/thawaf/Putaran" + (thawafIfadahCounter + 1) + ".txt", "prayer/thawaf/Putaran" + (thawafIfadahCounter + 1) + ".mp3", () -> {
                        thawafIfadahCheckpoint = 1;
                        playThawafIfadahCheckpoint(location);
                    }));
                } else {
                    handler.postDelayed(() -> playThawafIfadahCheckpoint(lastLocation), 4000);
                }
                break;
            case 1:
                if (isInside(location, MAQAM_IBRAHIM)) {
                    playDoa("doa/thawaf/maqam_ibrahim.txt", "prayer/thawaf/maqam_ibrahim.mp3", () -> {
                        thawafIfadahCheckpoint = 2;
                        playThawafIfadahCheckpoint(location);
                    });
                } else {
                    handler.postDelayed(() -> playThawafIfadahCheckpoint(lastLocation), 4000);
                }
                break;
            case 2:
                if (isInside(location, RUKUN_YAMANI)) {
                    playDoa("doa/thawaf/rukun_yamani.txt", "prayer/thawaf/rukun_yamani.mp3", () -> {
                        thawafIfadahCounter++;
                        thawafIfadahCheckpoint = 0;
                        playThawafIfadahCheckpoint(location);
                    });
                } else {
                    handler.postDelayed(() -> playThawafIfadahCheckpoint(lastLocation), 4000);
                }
                break;
        }
    }

    // --- Thawaf Wada dengan checkpoint (mengikuti LocationService) ---
    private void playThawafWadaCheckpoint(Location location) {
        if (thawafWadaCounter >= 7) {
            thawafWadaDone = true;
            showAlert("Thawaf Wada selesai.", null);
            return;
        }
        switch (thawafWadaCheckpoint) {
            case 0:
                if (isInside(location, HAJAR_ASWAD)) {
                    // Sama seperti LocationService: gunakan path thawaf
                    playDoa("doa/thawaf/hajar_aswad.txt", "prayer/thawaf/hajar_aswad.mp3", () -> playDoa("doa/thawaf/Putaran" + (thawafWadaCounter + 1) + ".txt", "prayer/thawaf/Putaran" + (thawafWadaCounter + 1) + ".mp3", () -> {
                        thawafWadaCheckpoint = 1;
                        playThawafWadaCheckpoint(location);
                    }));
                } else {
                    handler.postDelayed(() -> playThawafWadaCheckpoint(lastLocation), 4000);
                }
                break;
            case 1:
                if (isInside(location, MAQAM_IBRAHIM)) {
                    playDoa("doa/thawaf/maqam_ibrahim.txt", "prayer/thawaf/maqam_ibrahim.mp3", () -> {
                        thawafWadaCheckpoint = 2;
                        playThawafWadaCheckpoint(location);
                    });
                } else {
                    handler.postDelayed(() -> playThawafWadaCheckpoint(lastLocation), 4000);
                }
                break;
            case 2:
                if (isInside(location, RUKUN_YAMANI)) {
                    playDoa("doa/thawaf/rukun_yamani.txt", "prayer/thawaf/rukun_yamani.mp3", () -> {
                        thawafWadaCounter++;
                        thawafWadaCheckpoint = 0;
                        playThawafWadaCheckpoint(location);
                    });
                } else {
                    handler.postDelayed(() -> playThawafWadaCheckpoint(lastLocation), 4000);
                }
                break;
        }
    }

    // --- SAI setelah thawaf ifadah (mengikuti LocationService) ---
    private void playSaiAfterIfadahLap(Location location) {
        if (saiAfterIfadahCounter >= 7) {
            showAlert("Sai setelah thawaf ifadah selesai.", null);
            return;
        }
        handler.postDelayed(() -> {
            boolean atSafa = isInAnySAILine(location, SAI_LINE1);
            boolean atMarwah = isInAnySAILine(location, SAI_LINE2);
            if ((saiAfterIfadahCounter % 2 == 0 && atSafa) || (saiAfterIfadahCounter % 2 == 1 && atMarwah)) {
                int lap = saiAfterIfadahCounter + 1;
                String textPath, audioPath;
                if (saiAfterIfadahCounter % 2 == 0) {
                    textPath = "doa/sai/naik_bukit_safa.txt";
                    audioPath = "prayer/sai/naik_bukit_safa.mp3";
                } else {
                    textPath = "doa/sai/naik_bukit_marwah.txt";
                    audioPath = "prayer/sai/naik_bukit_marwah.mp3";
                }
                playDoa(textPath, audioPath, () -> playDoa("doa/sai/bukit.txt", "prayer/sai/bukit.mp3", () -> {
                    saiAfterIfadahCounter++;
                    playSaiAfterIfadahLap(lastLocation);
                }));
            } else {
                playSaiAfterIfadahLap(lastLocation);
            }
        }, 4000);
    }

    private boolean isInAnySAILine(Location location, double[][] checkpoints) {
        if (location == null) return false;
        for (double[] point : checkpoints) {
            if (isInside(location, point)) return true;
        }
        return false;
    }

    // Fungsi untuk dipanggil dari luar (misal tombol di MainActivity15) agar thawaf wada bisa dimulai manual
    public void requestManualThawafWada() {
        thawafWadaManualRequested = true;
        thawafWadaStarted = false;
        thawafWadaCounter = 0;
        thawafWadaCheckpoint = 0;
    }

    // --- Helper ---
    private boolean minaJamaratDone() {
        return shownEvents.contains("mina") &&
               shownEvents.contains("aqabah-10dzulhijjah") &&
               shownEvents.contains("jamarat-11") &&
               shownEvents.contains("jamarat-12");
    }

    private boolean isInside(Location location, double[] area) {
        float[] result = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), area[0], area[1], result);
        return result[0] < area[2];
    }

    // --- Doa Display & Audio ---
    private void playDoa(String textPath, String audioPath, Runnable onComplete) {
        StorageReference storage = FirebaseStorage.getInstance().getReference();

        storage.child(textPath).getBytes(1024 * 1024)
                .addOnSuccessListener(bytes -> {
                    String text = new String(bytes, StandardCharsets.UTF_8);
                    showAlert(text, () -> playAudio(storage, audioPath, onComplete));
                })
                .addOnFailureListener(e -> {
                    Log.e("NotifService", "Failed to get text: " + textPath);
                    playAudio(storage, audioPath, onComplete);
                });
    }

    private void playAudio(StorageReference storage, String audioPath, Runnable onComplete) {
        storage.child(audioPath).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    if (mediaPlayer != null) mediaPlayer.release();
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(getApplicationContext(), uri);
                        mediaPlayer.setOnCompletionListener(mp -> {
                            mp.release();
                            if (onComplete != null) onComplete.run();
                        });
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        Log.e("NotifService", "Failed to play audio: " + audioPath, e);
                        if (onComplete != null) onComplete.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("NotifService", "Failed to get audio URL: " + audioPath, e);
                    if (onComplete != null) onComplete.run();
                });
    }

    private void showAlert(String text, Runnable onClose) {
        dialogQueue.add(() -> {
            AlertDialog dialog = new AlertDialog.Builder(getBaseContext())
                    .setTitle("Doa")
                    .setMessage(text)
                    .setCancelable(false)
                    .setPositiveButton("Tutup", (d, which) -> {
                        d.dismiss();
                        isDialogShowing = false;
                        showNextDialog();
                        if (onClose != null) onClose.run();
                    })
                    .create();
            if (dialog.getWindow() != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dialog.getWindow().setType(android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                } else {
                    dialog.getWindow().setType(android.view.WindowManager.LayoutParams.TYPE_PHONE);
                }
            }
            dialog.show();
            isDialogShowing = true;
        });
        if (!isDialogShowing) {
            showNextDialog();
        }
    }

    private void showNextDialog() {
        Runnable next = dialogQueue.poll();
        if (next != null) {
            next.run();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

