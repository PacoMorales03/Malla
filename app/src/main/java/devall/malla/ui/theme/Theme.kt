package devall.malla.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AccentLight,
    onPrimary = Color.White,
    secondary = GoldLight,
    onSecondary = Color.White,
    background = PaperLight,
    onBackground = InkLight,
    surface = PaperRaisedLight,
    onSurface = InkLight,
    surfaceVariant = PaperLight,
    onSurfaceVariant = MutedLight,
    outline = LineLight,
    primaryContainer = AccentSoftLight,
    onPrimaryContainer = AccentStrongLight
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentDark,
    onPrimary = PaperDark,
    secondary = GoldDark,
    onSecondary = PaperDark,
    background = PaperDark,
    onBackground = InkDark,
    surface = PaperRaisedDark,
    onSurface = InkDark,
    surfaceVariant = PaperDark,
    onSurfaceVariant = MutedDark,
    outline = LineDark,
    primaryContainer = AccentSoftDark,
    onPrimaryContainer = AccentStrongDark
)

@Composable
fun MallaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val estadoColores = if (darkTheme) EstadoColoresOscuro else EstadoColoresClaro
    val bloqueColores = if (darkTheme) BloqueColoresOscuro else BloqueColoresClaro

    CompositionLocalProvider(
        LocalEstadoColores provides estadoColores,
        LocalBloqueColores provides bloqueColores
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
