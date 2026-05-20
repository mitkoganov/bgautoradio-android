plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace  = "com.bgautoradio"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bgautoradio"
        minSdk        = 26          // Android 8.0 — covers all modern head units
        targetSdk     = 35
        versionCode   = 21
        versionName   = "1.0.20"

        // Remote station catalog URL — override in release flavor if needed
        buildConfigField("String", "SPOTIFY_CLIENT_ID",    "\"ff8116acb06844fdae4af7ac69fe2975\"")
        buildConfigField("String", "SPOTIFY_REDIRECT_URI", "\"bgautoradio://spotify-callback\"")
        manifestPlaceholders["redirectSchemeName"] = "bgautoradio"
        manifestPlaceholders["redirectHostName"]   = "spotify-callback"

        buildConfigField(
            "String",
            "STATIONS_REMOTE_URL",
            "\"https://raw.githubusercontent.com/bgautoradio/stations/main/bulgarian_radio_stations.json\""
        )
        // HERE Traffic API key — register free at developer.here.com
        buildConfigField("String", "HERE_API_KEY", "\"d9-Fu1_un99EwPAFJXnTbxDMXD8ZEbvOcXPZ7bETSQ4\"")

    }

    buildTypes {
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix   = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.media3.common.util.UnstableApi"
        )
    }

    buildFeatures {
        compose      = true
        buildConfig  = true
    }

    // Force landscape for automotive head units
    // Can be overridden per-Activity in Manifest
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Hidden API bypass (needed for setLaunchWindowingMode on Android 9+)
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")

    // Core
    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.activity.compose)
    implementation(libs.splash.screen)
    implementation(libs.coroutines.android)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.animation)
    debugImplementation(libs.compose.ui.tooling)

    // Navigation
    implementation(libs.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.datastore.preferences)

    // Media3 / ExoPlayer
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.session)
    implementation(libs.media3.ui)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    // OSMDroid — map renderer (HERE tile source via REST)
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // Spotify auth (OAuth PKCE login)
    implementation("com.spotify.android:auth:2.1.1")
    implementation("androidx.browser:browser:1.5.0")

    // Spotify App Remote SDK (local AAR — play tracks/playlists without opening Spotify)
    implementation(files("libs/spotify-app-remote-release-0.8.0.aar"))
}
