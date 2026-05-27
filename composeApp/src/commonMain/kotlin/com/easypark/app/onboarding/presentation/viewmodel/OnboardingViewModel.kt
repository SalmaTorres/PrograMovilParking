package com.easypark.app.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypark.app.core.data.remote.RemoteConfigManager
import com.easypark.app.onboarding.data.OnboardingPreferences
import com.easypark.app.onboarding.domain.model.OnboardingConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class OnboardingViewModel(
    private val remoteConfigManager: RemoteConfigManager,
    private val preferences: OnboardingPreferences
) : ViewModel() {

    // ── State ──────────────────────────────────────────────────────────────────
    private val _slides = MutableStateFlow<List<OnboardingConfig>>(emptyList())
    val slides: StateFlow<List<OnboardingConfig>> = _slides.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    // ── Effects ────────────────────────────────────────────────────────────────
    private val _effect = MutableSharedFlow<OnboardingEffect>()
    val effect = _effect.asSharedFlow()

    // ── JSON parser (lenient to tolerate missing/extra keys) ──────────────────
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    init {
        loadSlides()
    }

    private fun loadSlides() {
        viewModelScope.launch {
            try {
                remoteConfigManager.initialize()
                val raw = remoteConfigManager.getString("onboarding_driver")
                if (raw.isNotBlank()) {
                    _slides.value = json.decodeFromString(raw)
                } else {
                    _slides.value = fallbackSlides()
                }
            } catch (e: Exception) {
                _slides.value = fallbackSlides()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onPageChange(page: Int) {
        _currentPage.value = page
    }

    fun onSkip() {
        // Navega al Home sin marcar onboarding como completado
        viewModelScope.launch { _effect.emit(OnboardingEffect.NavigateToHome) }
    }

    fun onFinish() {
        preferences.setOnboardingCompleted(true)
        viewModelScope.launch { _effect.emit(OnboardingEffect.NavigateToHome) }
    }

    fun shouldShowOnboarding(): Boolean = !preferences.isOnboardingCompleted()

    // ── Fallback hardcoded (por si Remote Config falla o está vacío) ───────────
    private fun fallbackSlides(): List<OnboardingConfig> {
        return listOf(
            OnboardingConfig(
                id = 1,
                title = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Bienvenido a EasyPark",
                    en = "Welcome to EasyPark",
                    fr = "Bienvenue sur EasyPark"
                ),
                description = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Encuentra el parqueo más cercano a ti en segundos.",
                    en = "Find the nearest parking spot in seconds.",
                    fr = "Trouvez le parking le plus proche en quelques secondes."
                ),
                image_url = ""
            ),
            OnboardingConfig(
                id = 2,
                title = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Crea tu cuenta",
                    en = "Create your account",
                    fr = "Créez votre compte"
                ),
                description = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Regístrate fácilmente para comenzar a usar EasyPark.",
                    en = "Sign up easily to start using EasyPark.",
                    fr = "Inscrivez-vous facilement pour commencer à utiliser EasyPark."
                ),
                image_url = ""
            ),
            OnboardingConfig(
                id = 3,
                title = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Registra tu vehículo",
                    en = "Register your vehicle",
                    fr = "Enregistrez votre véhicule"
                ),
                description = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Agrega tu vehículo para evitar contratiempos al reservar.",
                    en = "Add your vehicle to avoid issues when booking.",
                    fr = "Ajoutez votre véhicule pour éviter des problèmes lors de la réservation."
                ),
                image_url = ""
            ),
            OnboardingConfig(
                id = 4,
                title = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Elige tu lugar",
                    en = "Choose your spot",
                    fr = "Choisissez votre place"
                ),
                description = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Reserva un espacio de parqueo y llega sin estrés.",
                    en = "Reserve a parking space and arrive stress-free.",
                    fr = "Réservez une place de parking et arrivez sans stress."
                ),
                image_url = ""
            )
        )
    }
}

sealed class OnboardingEffect {
    object NavigateToHome : OnboardingEffect()
}
