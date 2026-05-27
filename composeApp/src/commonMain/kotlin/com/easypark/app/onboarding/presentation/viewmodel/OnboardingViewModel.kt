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
                val raw = remoteConfigManager.getString("onboarding_driver_config")
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
                    es = "Regístrate de manera rápida y segura para comenzar.",
                    en = "Sign up quickly and securely to get started.",
                    fr = "Inscrivez-vous rapidement et en toute sécurité pour commencer."
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
                    es = "Agrega los datos de tu auto para facilitar tus reservas.",
                    en = "Add your car details to make booking easier.",
                    fr = "Ajoutez les détails de votre voiture pour faciliter la réservation."
                ),
                image_url = ""
            ),
            OnboardingConfig(
                id = 4,
                title = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Reserva en un toque",
                    en = "Book in a tap",
                    fr = "Réservez en un clic"
                ),
                description = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Selecciona tu lugar de estacionamiento y resérvalo al instante.",
                    en = "Select your parking spot and book it instantly.",
                    fr = "Sélectionnez votre place de parking et réservez-la instantanément."
                ),
                image_url = ""
            ),
            OnboardingConfig(
                id = 5,
                title = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Navega sin estrés",
                    en = "Navigate stress-free",
                    fr = "Naviguez sans stress"
                ),
                description = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Obtén indicaciones en tiempo real para llegar a tu parqueo reservado.",
                    en = "Get real-time directions to your reserved parking spot.",
                    fr = "Obtenez des itinéraires en temps réel vers votre place de parking réservée."
                ),
                image_url = ""
            ),
            OnboardingConfig(
                id = 6,
                title = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "¡Listo para arrancar!",
                    en = "Ready to go!",
                    fr = "Prêt à partir !"
                ),
                description = com.easypark.app.onboarding.domain.model.LocalizedText(
                    es = "Explora el mapa y disfruta de una experiencia de estacionamiento premium.",
                    en = "Explore the map and enjoy a premium parking experience.",
                    fr = "Explorez la carte et profitez d'une expérience de stationnement premium."
                ),
                image_url = ""
            )
        )
    }
}

sealed class OnboardingEffect {
    object NavigateToHome : OnboardingEffect()
}
