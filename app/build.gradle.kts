plugins {
   // alias(libs.plugins.android.application)
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "ir.shariaty.tripmate"
    compileSdk = 35

    defaultConfig {
        applicationId = "ir.shariaty.tripmate"
        minSdk = 27
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

//dependencies {
//
//    implementation(libs.appcompat)
//    implementation(libs.material)
//    implementation(libs.activity)
//    implementation(libs.constraintlayout)
//    implementation(libs.firebase.auth)
//    implementation(libs.credentials)
//    implementation(libs.credentials.play.services.auth)
//    implementation(libs.googleid)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)
//
//    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
//    implementation("com.google.firebase:firebase-analytics")
//
//}

dependencies {

    // UI Libraries
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.activity:activity:1.8.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    // Google Sign-In و Firebase Auth
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.google.firebase:firebase-auth:22.3.1")

    // Firebase BOM
    implementation (platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation ("com.google.firebase:firebase-analytics")

    // تست‌ها
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}



