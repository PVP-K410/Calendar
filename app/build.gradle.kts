import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.VariantDimension
import org.gradle.configurationcache.extensions.capitalized
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
    id("com.google.firebase.crashlytics")

    kotlin("kapt")
    kotlin("plugin.serialization") version "1.9.22"
}

val configuration = Properties()
    .apply {
        load(FileInputStream(project.file("configuration.properties")))
    }

android {
    compileSdk = 34
    namespace = "com.pvp.app"

    defaultConfig {
        applicationId = "com.pvp.app"
        minSdk = 28
        targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionCode = 1
        versionName = "dev-1.0.0.0"

        resourceConfigurations.addAll(
            listOf(
                "en",
                "lt"
            )
        )

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigDefault()
    }

    signingConfigs {
        create("_debug") {
            keyAlias = configuration.getProperty("debug.signing.key.alias")
            keyPassword = configuration.getProperty("debug.signing.key.password")
            storeFile = file(configuration.getProperty("debug.signing.store.file"))
            storePassword = configuration.getProperty("debug.signing.store.password")
        }

        create("_release") {
            keyAlias = configuration.getProperty("release.signing.key.alias")
            keyPassword = configuration.getProperty("release.signing.key.password")
            storeFile = file(configuration.getProperty("release.signing.store.file"))
            storePassword = configuration.getProperty("release.signing.store.password")
        }
    }

    buildTypes {
        debug {
            val alias = "debug"

            signingConfig = signingConfigs.getByName("_$alias")
            versionNameSuffix = "-$alias"

            withApplicationName("${rootProject.name} (${alias.capitalized()})")
        }

        release {
            val alias = "release"

            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("_$alias")
            versionNameSuffix = "-$alias"

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            withApplicationName(rootProject.name)
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
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    // Development
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.health.connect:connect-client:1.1.0-alpha07")
    implementation("androidx.hilt:hilt-common:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.paging:paging-common-android:3.3.0-rc01")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("com.google.android.gms:play-services-auth:21.1.1")

    // at >= 2.3.0 gRPC version mismatch with firebase libraries
    implementation("com.google.api-client:google-api-client-android:2.2.0")

    implementation("com.google.apis:google-api-services-calendar:v3-rev411-1.25.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.patrykandpatrick.vico:compose:2.0.0-alpha.19")
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.19")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-svg:2.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}

fun ApplicationBuildType.withApplicationName(name: String) {
    manifestPlaceholders["applicationName"] = name
}

fun VariantDimension.buildConfigDefault() {
    buildConfigField(
        "String",
        "GOOGLE_OAUTH_CLIENT_ID",
        "\"${configuration.getProperty("google.oauth.client.id")}\""
    )
}
