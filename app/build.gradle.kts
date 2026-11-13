plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}


android {
    namespace = "com.fridge.fridgemanager"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }


    defaultConfig {
        applicationId = "com.fridge.fridgemanager"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-ktx:1.9.3")

    // Room
    implementation("androidx.room:room-runtime:2.7.0-alpha07")
    implementation("androidx.room:room-ktx:2.7.0-alpha07")
    ksp("androidx.room:room-compiler:2.7.0-alpha07")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // appcompat
    implementation("androidx.appcompat:appcompat:1.7.0")
// RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")
// constraintlayout
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
// material 悬浮按钮
    implementation("com.google.android.material:material:1.12.0")
// lifecycle viewmodel + livedata
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
// Room
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

}
