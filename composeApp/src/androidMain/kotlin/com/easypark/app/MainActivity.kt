package com.easypark.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.easypark.app.di.getModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.osmdroid.config.Configuration
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.android.inject
import com.easypark.app.core.domain.session.SessionManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {

    private val sessionManager: SessionManager by inject()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notificaciones habilitadas")
        } else {
            Log.d("MainActivity", "Notificaciones denegadas")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        askNotificationPermission()
        fetchFcmToken()
        checkAndTriggerWelcomeCampaign()
        observeUserAndUploadToken()

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName
        if (org.koin.core.context.GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@MainActivity)
                modules(getModules())
            }
        }
        setContent {
            App()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Ya tenemos permiso
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "FCM token falló al cargarse", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("MainActivity", "FCM Token actual: $token")
        })
    }

    private fun checkAndTriggerWelcomeCampaign() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

        if (isFirstLaunch) {
            try {
                // 1. Obtener instancia de Firebase Analytics
                val firebaseAnalytics = FirebaseAnalytics.getInstance(this)

                // 2. Disparar el evento personalizado configurado en la consola
                firebaseAnalytics.logEvent("primer_inicio", null)
                Log.d("MainActivity", "Evento 'primer_inicio' disparado para In-App Messaging")

                // 3. Cambiar la bandera para que no se vuelva a ejecutar en el futuro
                sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al disparar evento 'primer_inicio': ${e.message}")
            }
        } else {
            Log.d("MainActivity", "No es el primer inicio. Ignorando evento de bienvenida.")
        }
    }

    private fun observeUserAndUploadToken() {
        lifecycleScope.launch {
            sessionManager.currentUser.collectLatest { user ->
                if (user != null) {
                    uploadCurrentFcmToken(user.email)
                }
            }
        }
    }

    private fun uploadCurrentFcmToken(email: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                if (token != null) {
                    val sanitizedEmail = email.replace(".", "_")
                    FirebaseDatabase.getInstance().reference
                        .child("users")
                        .child(sanitizedEmail)
                        .child("fcmToken")
                        .setValue(token)
                        .addOnCompleteListener { uploadTask ->
                            if (uploadTask.isSuccessful) {
                                Log.d("MainActivity", "FCM token subido correctamente para: $sanitizedEmail")
                            } else {
                                Log.e("MainActivity", "FCM token no se pudo subir", uploadTask.exception)
                            }
                        }
                }
            } else {
                Log.w("MainActivity", "FCM token falló al cargarse para subir", task.exception)
            }
        }
    }
}