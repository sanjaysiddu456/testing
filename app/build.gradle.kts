plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.testapplication"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.testapplication"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")

    // Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // Gson
    implementation("com.google.code.gson:gson:2.13.2")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // CardView
    implementation("androidx.cardview:cardview:1.0.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")

    // Lifecycle Runtime
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")

    // Fragment
    implementation("androidx.fragment:fragment-ktx:1.8.9")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
}