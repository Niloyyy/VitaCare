// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // alias(libs.plugins.google.gms.google.services) apply false
}

buildscript {
    repositories {
        // Make e that you have the following two repositories
        google()  // Google's Maven repository
        mavenCentral()  // Maven Central repository
    }

    dependencies {
        classpath ("com.google.gms:google-services:4.4.3")
    }
}