plugins {
    alias(libs.plugins.runique.android.library.compose)
}

android {
    namespace = "com.example.drosckar.core.presentation.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    api(libs.androidx.material3)
}