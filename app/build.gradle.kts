plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("com.google.devtools.ksp")
    id ("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.tolgadirek.chatwithme"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tolgadirek.chatwithme"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    val nav_version = "2.8.6"
// Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor ("androidx.room:room-compiler:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    val lifecycle_version = "2.8.5"
    //mvvmler için
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    //internetten veri ulaşımı
    val retrofitVersion = "2.8.1"
    implementation ("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation ("com.squareup.retrofit2:converter-gson:$retrofitVersion")

}