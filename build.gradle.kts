// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // ...

    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.android.application") version "8.2.2" apply false

}
