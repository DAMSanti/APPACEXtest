plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.appacex"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.appacex"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/LGPL2.1"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/io.netty.versions.properties"
            excludes += "META-INF/license/LICENSE.aix-netbsd.txt"
            excludes += "google/firestore/v1/query.proto"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.base)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.firebase.messaging.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.microsoft.graph:microsoft-graph:3.0.0") {
        exclude(group = "javax.activation", module = "activation")
    }
    // MSAL
    implementation("com.microsoft.identity.client:msal:1.4.0")
    // COIL
    implementation("io.coil-kt:coil-compose:2.1.0")
    // JAKARTA
    implementation("jakarta.activation:jakarta.activation-api:1.2.1")
    // COROUTINES
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    // GOOGLE MAPS
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-compose:2.0.0")
    //RETROFIT
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //FIREBASE
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx:21.0.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.0.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.0.0")
}