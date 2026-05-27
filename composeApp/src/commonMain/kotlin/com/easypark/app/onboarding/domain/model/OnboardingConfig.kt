package com.easypark.app.onboarding.domain.model

import com.easypark.app.getSystemLanguage
import kotlinx.serialization.Serializable

@Serializable
data class LocalizedText(
    val es: String = "",
    val en: String = "",
    val fr: String = ""
)

@Serializable
data class OnboardingConfig(
    val id: Int,
    val title: LocalizedText,
    val description: LocalizedText,
    val image_url: String
)

fun LocalizedText.getLocalized(): String {
    val lang = getSystemLanguage()
    return when (lang) {
        "en" -> en
        "fr" -> fr
        else -> es
    }
}
