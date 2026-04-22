import org.jetbrains.kotlin.gradle.dsl.JvmTarget

import java.net.URL
import java.net.URLEncoder
import java.io.File
import java.util.Properties
import java.util.regex.Pattern

tasks.register("syncLoco") {
    group = "localization"
    val projectPath = project.projectDir
    val rootDir = project.rootProject.projectDir
    val resPath = "src/commonMain/composeResources"

    doLast {
        val properties = Properties()
        val propertiesFile = File(rootDir, "local.properties")
        if (propertiesFile.exists()) propertiesFile.inputStream().use { properties.load(it) }
        val locoKey = properties.getProperty("loco.api.key") ?: ""

        println("📡 Conectando con Loco...")

        // Intentamos con la URL más simple posible
        val baseUrlString = "https://localise.biz/api/export/locale/es.xml?key=$locoKey&format=android"

        val baseXml = try {
            val url = URL(baseUrlString)
            val conn = url.openConnection() as java.net.HttpURLConnection
            if (conn.responseCode != 200) {
                // ESTO NOS DIRÁ EL ERROR REAL
                val errorText = conn.errorStream?.bufferedReader()?.readText() ?: "Sin mensaje"
                println("ERROR DE LOCO (${conn.responseCode}): $errorText")
                return@doLast
            }
            conn.inputStream.bufferedReader().readText()
        } catch (e: Exception) {
            println("Error de conexión: ${e.message}")
            return@doLast
        }

        val strings = mutableMapOf<String, String>()
        val matcher = Pattern.compile("<string name=\"([^\"]+)\">([^<]*)</string>").matcher(baseXml)
        while (matcher.find()) {
            strings[matcher.group(1)] = matcher.group(2)
        }

        println("Se encontraron ${strings.size} llaves en español.")
        if (strings.isEmpty()) return@doLast

        val targetLanguages = listOf("en", "fr")
        targetLanguages.forEach { lang ->
            println("\nPROCESANDO IDIOMA: $lang")
            val translatedXml = StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n")

            strings.forEach { (key, value) ->
                if (value.isBlank()) return@forEach
                val translatedValue = translateText(value, lang)
                translatedXml.append("    <string name=\"$key\">$translatedValue</string>\n")
                uploadToLoco(locoKey, lang, key, translatedValue)
                Thread.sleep(350)
            }
            translatedXml.append("</resources>")

            val targetFile = File(projectPath, "$resPath/values-$lang/strings.xml")
            if (!targetFile.parentFile.exists()) targetFile.parentFile.mkdirs()
            targetFile.writeText(translatedXml.toString())
            println("Archivo local '$lang' actualizado.")
        }
    }
}

fun uploadToLoco(apiKey: String, lang: String, assetId: String, translation: String) {
    try {
        val encodedTranslation = URLEncoder.encode(translation, "UTF-8")
        val url = URL("https://localise.biz/api/translations/$assetId/$lang?key=$apiKey")
        val connection = url.openConnection() as java.net.HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.outputStream.use { it.write(encodedTranslation.toByteArray()) }

        if (connection.responseCode in 200..204) {
            println("Sincronizado: [$assetId] -> $translation")
        }
    } catch (e: Exception) { }
}

fun translateText(text: String, targetLang: String): String {
    return try {
        val encodedText = URLEncoder.encode(text, "UTF-8")
        val urlString = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=es&tl=$targetLang&dt=t&q=$encodedText"
        val response = URL(urlString).readText()
        val p = Pattern.compile("\"([^\"]+)\"")
        val m = p.matcher(response)
        if (m.find()) m.group(1) else text
    } catch (e: Exception) { text }
}

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
