// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

    //hilt
    alias(libs.plugins.dagger.hilt.plugin) apply false
    alias(libs.plugins.kotlin.kapt) apply false

    //firebase
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}