plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.hypex.gitcoz"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hypex.gitcoz"
        minSdk = 32
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val telegramBotToken = (project.findProperty("TELEGRAM_BOT_TOKEN") as String?) ?: ""
        val telegramChatId = (project.findProperty("TELEGRAM_CHAT_ID") as String?) ?: ""
        buildConfigField("String", "TELEGRAM_BOT_TOKEN", "\"$telegramBotToken\"")
        buildConfigField("String", "TELEGRAM_CHAT_ID", "\"$telegramChatId\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86_64")
            isUniversalApk = false
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    val retrofit_version = "2.11.0"
    val coil_version = "2.6.0"
    val nav_version = "2.8.0"
    val material3_version = "1.3.0"

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(platform("androidx.compose:compose-bom:2024.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    debugImplementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:$material3_version")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.2")

    // Navigation
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")

    // Coil
    implementation("io.coil-kt:coil-compose:$coil_version")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // Markdown
    implementation("com.github.jeziellago:compose-markdown:0.5.8")

    // Animated Navigation Bar
    implementation("com.exyte:animated-navigation-bar:1.0.0")

    // Custom Tabs (for GitHub OAuth browser flow)
    implementation("androidx.browser:browser:1.8.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
