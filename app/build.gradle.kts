plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.alarm_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.alarm_app"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "v20024.04.05.23:47::author:rage"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.android.gif.drawable)
}