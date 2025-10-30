// build.gradle.kts (Nivel de módulo: app)

// 1. Importaciones correctas en la parte superior
import java.util.Properties
import java.io.FileInputStream

// 2. El bloque `plugins` solo debe contener la declaración de plugins
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // El plugin de Compose no necesita estar aquí si usas buildFeatures.compose = true
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

// 3. Carga las propiedades DESPUÉS del bloque de plugins
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

// 4. Un ÚNICO bloque 'android' con toda la configuración
android {
    namespace = "com.proyecto_grado"
    compileSdk = 35

    // Configuración de firma para el APK de release
    signingConfigs {
        create("release") {
            keyAlias = localProperties.getProperty("KEY_ALIAS")
            keyPassword = localProperties.getProperty("KEY_PASSWORD")
            storeFile = file(localProperties.getProperty("KEYSTORE_FILE", ""))
            storePassword = localProperties.getProperty("KEYSTORE_PASSWORD")
        }
    }

    defaultConfig {
        applicationId = "com.proyecto_grado"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Asigna la configuración de firma al build de release
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    // Es importante habilitar Compose Options aquí también
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Asegúrate de que esta versión es compatible
    }
}

// 5. Bloque de dependencias limpiado
dependencies {
    // --- AndroidX y Compose ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)) // BOM gestiona las versiones de Compose
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("androidx.compose.material:material-icons-extended")

    // PDF
    implementation("com.github.librepdf:openpdf:1.3.39")

    // --- Firebase ---
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Room
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
