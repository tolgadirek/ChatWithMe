// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    kotlin("jvm") version "2.0.20" // Kotlin sürümünü 2.0.20 yap
    id ("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false
    id ("androidx.navigation.safeargs.kotlin") version "2.8.6" apply false
}