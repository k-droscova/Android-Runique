import com.android.build.api.dsl.LibraryExtension
import com.example.drosckar.convention.ExtensionType
import com.example.drosckar.convention.addUiLayerDependencies
import com.example.drosckar.convention.configureAndroidCompose
import com.example.drosckar.convention.configureBuildTypes
import com.example.drosckar.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin

class AndroidFeatureUiConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("runique.android.library.compose")
            }

            dependencies {
                addUiLayerDependencies(target)
            }
        }
    }
}