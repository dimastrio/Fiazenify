buildscript {
    repositories {
        google()  // Google's Maven repository

        mavenCentral()  // Maven Central repository


    }
    dependencies {
        // Add the dependency for the Google services Gradle plugin
        classpath("com.google.gms:google-services:4.4.0")
        // Dagger Hilt
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.42")

    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.4.1" apply false
    id("com.android.library") version "7.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
}