package cl.duoc.esfocar.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VerdeCarabineros = Color(0xFF0B4F2E)
private val VerdeOscuro = Color(0xFF06331D)
private val Dorado = Color(0xFFC9A227)

private val Light = lightColorScheme(
    primary = VerdeCarabineros, onPrimary = Color.White,
    primaryContainer = Color(0xFFC4EBD0), onPrimaryContainer = VerdeOscuro,
    secondary = Dorado, onSecondary = Color(0xFF1F1700),
    secondaryContainer = Color(0xFFF7E7A8), onSecondaryContainer = Color(0xFF3B2F00),
    tertiary = Color(0xFF4B6358),
    background = Color(0xFFF7FBF7), surface = Color(0xFFF7FBF7),
    surfaceContainer = Color(0xFFE6F0E8),
    error = Color(0xFFB3261E)
)

private val Dark = darkColorScheme(
    primary = Color(0xFF8FD6A8), onPrimary = Color(0xFF00391C),
    primaryContainer = VerdeCarabineros, onPrimaryContainer = Color(0xFFC4EBD0),
    secondary = Color(0xFFE6C75A), onSecondary = Color(0xFF3B2F00),
    secondaryContainer = Color(0xFF574500), onSecondaryContainer = Color(0xFFF7E7A8),
    background = Color(0xFF0E1A13), surface = Color(0xFF0E1A13),
    surfaceContainer = Color(0xFF16261D)
)

@Composable
fun EsfocarTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}
