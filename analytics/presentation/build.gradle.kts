plugins {
    alias(libs.plugins.runique.android.feature.ui)
}

android {
    namespace = "com.example.drosckar.analytics.presentation"
}

dependencies {
    implementation(projects.analytics.domain)
}