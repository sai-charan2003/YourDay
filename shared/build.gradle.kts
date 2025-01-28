import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("dev.icerock.mobile.multiplatform-resources")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildKonfig)
}

kotlin {

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            export(libs.resources)
            export(libs.graphics)
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.encoding)
            api(libs.resources)
            implementation(libs.kotlinx.datetime)
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.napier)
            api(libs.permissions)
            api(libs.permissions.compose)
            implementation(libs.koin.compose.viewmodel)
            // put your Multiplatform dependencies here
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation (libs.play.services.location)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)

        }
    }
}


android {
    namespace = "com.charan.yourday.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
multiplatformResources {
    resourcesPackage.set("com.charan.yourday")

}
buildkonfig {
    packageName = "com.charan.yourday"

    defaultConfigs {
        val apiKey: String = gradleLocalProperties(rootDir, providers).getProperty("API_KEY")
        buildConfigField(FieldSpec.Type.STRING, "API_KEY", apiKey)
    }
}


