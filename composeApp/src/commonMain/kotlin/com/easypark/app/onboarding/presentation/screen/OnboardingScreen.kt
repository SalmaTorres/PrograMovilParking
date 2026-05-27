package com.easypark.app.onboarding.presentation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.easypark.app.navigation.NavRoute
import com.easypark.app.onboarding.domain.model.OnboardingConfig
import com.easypark.app.onboarding.domain.model.getLocalized
import com.easypark.app.onboarding.presentation.viewmodel.OnboardingEffect
import com.easypark.app.onboarding.presentation.viewmodel.OnboardingViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

// ── Paleta de colores de la pantalla ──────────────────────────────────────────
private val PrimaryGreen   = Color(0xFF2ECC71)
private val DarkBackground = Color(0xFF0D1B2A)
private val CardBackground = Color(0xFF1B2B3C)
private val TextPrimary    = Color(0xFFECF0F1)
private val TextSecondary  = Color(0xFFBDC3C7)
private val DotInactive    = Color(0xFF34495E)

@Composable
fun OnboardingScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val slides     by viewModel.slides.collectAsState()
    val isLoading  by viewModel.isLoading.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope      = rememberCoroutineScope()

    // Sincroniza pagerState → ViewModel
    LaunchedEffect(pagerState.currentPage) {
        viewModel.onPageChange(pagerState.currentPage)
    }

    // Consume efectos de navegación
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is OnboardingEffect.NavigateToHome -> {
                    navController.navigate(NavRoute.FindParking) { popUpTo(0) }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PrimaryGreen
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Pager principal ─────────────────────────────────────────
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    OnboardingPage(slide = slides[page])
                }

                // ── Controles inferiores ─────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Indicadores de puntos
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        slides.indices.forEach { index ->
                            PagerDot(isSelected = index == pagerState.currentPage)
                            if (index != slides.lastIndex) Spacer(Modifier.width(8.dp))
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    val isLastPage = pagerState.currentPage == slides.lastIndex

                    if (isLastPage) {
                        // Botón "Iniciar" → finaliza onboarding
                        Button(
                            onClick = { viewModel.onFinish() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                        ) {
                            Text(
                                text = "¡Empezar!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        // Fila: Anterior (oculto en slide 0) + Siguiente
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Botón "Anterior" (oculto en la primera página)
                            if (pagerState.currentPage > 0) {
                                TextButton(onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }) {
                                    Text("Anterior", color = TextSecondary)
                                }
                            } else {
                                Spacer(Modifier.width(80.dp))
                            }

                            // Botón "Siguiente"
                            Button(
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                            ) {
                                Text("Siguiente", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Botón "Omitir"
                        TextButton(onClick = { viewModel.onSkip() }) {
                            Text("Omitir", color = TextSecondary, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

// ── Slide individual ──────────────────────────────────────────────────────────
@Composable
private fun OnboardingPage(slide: OnboardingConfig) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Imagen (si hay URL) o placeholder con degradado
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground),
            contentAlignment = Alignment.Center
        ) {
            if (slide.image_url.isNotBlank()) {
                AsyncImage(
                    model = slide.image_url,
                    contentDescription = slide.title.getLocalized(),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Degradado decorativo cuando no hay imagen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    PrimaryGreen.copy(alpha = 0.4f),
                                    DarkBackground
                                )
                            )
                        )
                )
                Text(
                    text = "🚗",
                    fontSize = 80.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        // Título
        Text(
            text = slide.title.getLocalized(),
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Spacer(Modifier.height(16.dp))

        // Descripción
        Text(
            text = slide.description.getLocalized(),
            color = TextSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

// ── Dot indicador ─────────────────────────────────────────────────────────────
@Composable
private fun PagerDot(isSelected: Boolean) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 24.dp else 8.dp,
        animationSpec = tween(300),
        label = "dot_width"
    )
    val color by animateColorAsState(
        targetValue = if (isSelected) PrimaryGreen else DotInactive,
        animationSpec = tween(300),
        label = "dot_color"
    )
    Box(
        modifier = Modifier
            .height(8.dp)
            .width(width)
            .clip(CircleShape)
            .background(color)
    )
}
