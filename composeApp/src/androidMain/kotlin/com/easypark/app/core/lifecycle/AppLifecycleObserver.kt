package com.easypark.app.core.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.easypark.app.core.data.repository.AppEventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppLifecycleObserver : DefaultLifecycleObserver, KoinComponent {
    private val repository: AppEventRepository by inject()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onStart(owner: LifecycleOwner) {
        scope.launch {
            repository.logEvent("OPEN")
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        scope.launch {
            repository.logEvent("CLOSE")
        }
    }
}
