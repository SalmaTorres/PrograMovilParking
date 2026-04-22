import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.net.URL

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.google.gms.google.services)
}

val locoKey: String by lazy {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
    }
    properties.getProperty("locoKey") ?: ""
}

kotlin {
    androidTarget {
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
            baseName = "ComposeApp"
            isStatic = true
            // Required when using NativeSQLiteDriver
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation( libs.androidx.activity.compose)
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation("org.osmdroid:osmdroid-android:6.1.18")
            implementation("com.google.android.gms:play-services-location:21.0.1")
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.config)
            implementation(libs.firebase.database)
            implementation(libs.kotlinx.coroutines.play.services)
            implementation(libs.androidx.work.runtime)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.navigation.compose)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.easypark.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.easypark.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.firebase.database)
    debugImplementation(libs.compose.uiTooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

tasks.register("downloadLocoTranslations") {
    group = "localization"
    description = "Downloads translations from Loco (localise.biz)"

    doLast {
        if (locoKey.isEmpty()) {
            throw GradleException("locoKey not found in local.properties")
        }

        // Mapeo basado en tu configuración de Loco
        val locales = mapOf(
            "es-BO" to "values",    // Español (Bolivia) como default
            "en-GB" to "values-en"  // Inglés (UK)
        )

        locales.forEach { (locoLocale, folderName) ->
            println("Downloading translations for $locoLocale...")
            val url = "https://localise.biz/api/export/locale/$locoLocale.xml?key=$locoKey"
            
            val destinationDir = file("src/commonMain/composeResources/$folderName")
            if (!destinationDir.exists()) destinationDir.mkdirs()
            
            val destinationFile = file("${destinationDir.path}/strings.xml")
            
            try {
                val connection = URL(url).openConnection()
                connection.connect()
                val content = connection.getInputStream().bufferedReader().readText()
                destinationFile.writeText(content)
                println("Saved to ${destinationFile.path}")
            } catch (e: Exception) {
                println("Error downloading $locoLocale: ${e.message}")
            }
        }
    }
}
