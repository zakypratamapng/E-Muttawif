plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.muttawif"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.muttawif"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.location)
    implementation(libs.exoplayer)

    implementation (libs.ummalqura.calendar)

    // Firebase Authentication & Firestore
    implementation(libs.firebase.auth)
    implementation (libs.google.firebase.auth)
    implementation (libs.google.firebase.database)
    implementation (libs.google.firebase.storage)
    implementation (libs.play.services.auth.v2050)
        // AndroidX
        implementation (libs.appcompat.v161)
        implementation (libs.material.v190)
        implementation (libs.constraintlayout)

        // Other dependencies you might need
        implementation (libs.lifecycle.livedata.ktx)
        implementation (libs.lifecycle.viewmodel.ktx)

        implementation ("com.google.android.gms:play-services-auth:21.0.0") // versi terbaru
        implementation ("com.google.android.gms:play-services-identity:18.0.1")

    // Google Sign-In
    implementation(libs.gms.play.services.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// Tambahkan ini di baris paling bawah
apply(plugin = "com.google.gms.google-services")
