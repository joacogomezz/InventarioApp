import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.secrets.gradle)
    alias(libs.plugins.jetbrainsKotlinSerialization)
}

android {
    namespace = "com.joaqo.inventarioapp"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.joaqo.inventarioapp"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions.add("environment")
    productFlavors {
        create("dev") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://joaquinproducts.chuy7x.space/\"")
            resValue("string", "app_name", "INVENTARIOAPP (DEV)")
        }

        create("prod") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://joaquinproducts.chuy7x.space/\"")
            resValue("string", "app_name", "INVENTARIOAPP Pro")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

secrets {
    propertiesFileName = "local.properties"
    defaultPropertiesFileName = "local.defaults.properties"
    ignoreList.add("sdk.dir")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.lifecycle.viewmodel.compose)   //viewModel()
    implementation(libs.com.squareup.retrofit2.retrofit)        // Retrofit
    implementation(libs.com.squareup.retrofit2.converter.json)  // JSON
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // OkHttp logging
    implementation(libs.io.coil.kt.coil.compose)
    implementation(libs.androidx.navigation.compose)                // Navigation
    implementation(libs.androidx.compose.material.icons.extended)   // Icons
    implementation(libs.kotlinx.serialization.json)                 // Serialization
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}


