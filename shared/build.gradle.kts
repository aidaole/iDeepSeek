import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    kotlin("plugin.serialization") version "1.9.20"
    id("app.cash.sqldelight") version "2.0.1"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            // Ktor
            implementation("io.ktor:ktor-client-core:2.3.7")
            implementation("io.ktor:ktor-client-json:2.3.7")
            implementation("io.ktor:ktor-client-serialization:2.3.7")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
            
            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            
            // 添加序列化依赖
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            
            // Ktor 序列化支持
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
            
            // SQLDelight
            implementation("app.cash.sqldelight:runtime:2.0.1")
            implementation("app.cash.sqldelight:coroutines-extensions:2.0.1")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
        }
        
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-android:2.3.7")
            implementation("androidx.security:security-crypto:1.1.0-alpha06")
            
            // SQLDelight
            implementation("app.cash.sqldelight:android-driver:2.0.1")
        }
        
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-ios:2.3.7")
            implementation("io.ktor:ktor-client-darwin:2.3.7")
            
            // SQLDelight
            implementation("app.cash.sqldelight:native-driver:2.0.1")
        }
    }
}

android {
    namespace = "com.aidaole.ideepseek.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

sqldelight {
    databases {
        create("ChatDatabase") {
            packageName.set("com.aidaole.ideepseek.db")
        }
    }
}
