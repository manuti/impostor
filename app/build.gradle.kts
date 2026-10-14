plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.impostor.game"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.impostor.game"
        minSdk = 24
        targetSdk = 36
        versionCode = 11
        versionName = "0.4.2"
    }

    buildTypes {
        release {
            // R8 (minify + shrinkResources): el release no tenía ninguna
            // optimización activada. Medido el 2026-10-14 sobre este mismo
            // código: 7,45 MB -> 1,62 MB (-78%) solo por activar R8.
            isMinifyEnabled = true
            isShrinkResources = true
            // Firma de PRUEBA con la clave de debug mientras no exista el
            // keystore de release (fase 4, lo custodia el usuario): sin firma
            // el APK no se instala y el ciclo del proyecto exige probarlo en
            // dispositivo. Es la MISMA clave que los APKs publicados hasta
            // ahora (todos salían de `assembleDebug`), así que se actualiza
            // encima sin desinstalar. Sustituir por el keystore real al publicar.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

// Kotlin integrado en AGP 9: el compilador de Compose se activa con compose = true.
// La configuración del compilador Kotlin va en el bloque kotlin {} de nivel de módulo.
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    debugImplementation(libs.compose.ui.tooling)
}
