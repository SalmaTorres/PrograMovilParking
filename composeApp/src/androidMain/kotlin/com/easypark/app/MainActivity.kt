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
// --- NUEVOS IMPORTS PARA EL EJERCICIO ---
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.easypark.app.core.data.db.AppDatabase
import com.easypark.app.core.data.entity.AppEventEntity
import com.easypark.app.core.work.EventSyncWorker
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {

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

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName

        if (org.koin.core.context.GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@MainActivity)
                modules(getModules())
            }
        }

        // --- INICIO DEL EJERCICIO: REGISTRO DE EVENTO (ABRIR) ---
        // Disparamos el Worker para que lo registre en Room y lo suba a Firebase en segundo plano
        val data = androidx.work.workDataOf("eventType" to "APP_OPEN", "timestamp" to System.currentTimeMillis())
        val workRequest = OneTimeWorkRequestBuilder<EventSyncWorker>().setInputData(data).build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
        // --- FIN DEL EJERCICIO ---

        setContent {
            App()
        }
    }

    // --- INICIO DEL EJERCICIO: REGISTRO DE EVENTO (CERRAR) ---
    override fun onStop() {
        super.onStop()
        // Usamos WorkManager para asegurar que el guardado se haga en segundo plano
        try {
            val data = androidx.work.workDataOf("eventType" to "APP_CLOSE", "timestamp" to System.currentTimeMillis())
            val workRequest = OneTimeWorkRequestBuilder<EventSyncWorker>().setInputData(data).build()
            WorkManager.getInstance(applicationContext).enqueue(workRequest)
        } catch (e: Exception) {
            Log.e("MainActivity", "No se pudo registrar el cierre", e)
        }
    }
    // --- FIN DEL EJERCICIO ---

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
}