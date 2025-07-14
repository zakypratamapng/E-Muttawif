package com.example.muttawif;

public class User {
    public String uid, nama, email, noTelepon, alamat, noRombongan, namaKetua, jenisKelamin;
    public String profileImageUrl; // Tambahan untuk foto profil

    public User() {
        // Konstruktor kosong diperlukan untuk Firebase
    }

    public User(String uid, String nama, String email, String noTelepon, String alamat, String noRombongan, String namaKetua, String jenisKelamin) {
        this.uid = uid;
        this.nama = nama;
        this.email = email;
        this.noTelepon = noTelepon;
        this.alamat = alamat;
        this.noRombongan = noRombongan;
        this.namaKetua = namaKetua;
        this.jenisKelamin = jenisKelamin;
        this.profileImageUrl = null;
    }

    public User(String uid, String nama, String email, String noTelepon, String alamat, String noRombongan, String namaKetua, String jenisKelamin, String profileImageUrl) {
        this.uid = uid;
        this.nama = nama;
        this.email = email;
        this.noTelepon = noTelepon;
        this.alamat = alamat;
        this.noRombongan = noRombongan;
        this.namaKetua = namaKetua;
        this.jenisKelamin = jenisKelamin;
        this.profileImageUrl = profileImageUrl;
    }

    // ...existing getter/setter...
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNoTelepon() { return noTelepon; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getNoRombongan() { return noRombongan; }
    public void setNoRombongan(String noRombongan) { this.noRombongan = noRombongan; }

    public String getNamaKetua() { return namaKetua; }
    public void setNamaKetua(String namaKetua) { this.namaKetua = namaKetua; }

    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
