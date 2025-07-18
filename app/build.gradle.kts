plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.itsthe1.webexoplayer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.itsthe1.webexoplayer"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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
        // ✅ Enable desugaring for java.time.*, DateTimeFormatter, etc.
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "META-INF/io.netty.versions.properties"
            excludes += "META-INF/DEPENDENCIES.txt"
            excludes += "META-INF/DEPENDENCIES.txt.asc"
            excludes += "META-INF/DEPENDENCIES.txt.sha1"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3.android)
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil-svg:2.5.0")
    implementation("io.coil-kt:coil-base:2.5.0")

    // Navigation
    implementation(libs.androidx.navigation.common.android)
    implementation(libs.androidx.navigation.compose.android)

    implementation("androidx.compose.runtime:runtime-saveable")
    implementation(libs.tv.material)

    // Retrofit for HTTP networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // DKVideoPlayer (latest, recommended)
    implementation("xyz.doikki.android.dkplayer:dkplayer-java:3.3.7")
    implementation("xyz.doikki.android.dkplayer:dkplayer-ui:3.3.7")
    implementation("xyz.doikki.android.dkplayer:player-ijk:3.3.7")
    // Optional: Video caching and preloading
    implementation("xyz.doikki.android.dkplayer:videocache:3.3.7")

    // Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Firebase App Distribution (if used)
    implementation(libs.firebase.appdistribution.gradle)

    // ✅ Desugaring for java.time.*
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Testing
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
