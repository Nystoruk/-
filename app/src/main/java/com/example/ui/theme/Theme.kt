package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class LiturgicalColors(
    val feastCrimson: Color,
    val feastCrimsonContainer: Color,
    val goldAccent: Color,
    val goldContainer: Color,
    val lentPurple: Color,
    val lentPurpleContainer: Color,
    val fastFreeGreen: Color,
    val fastFreeGreenContainer: Color,
    val fastingAmber: Color,
    val fastingAmberContainer: Color,
    val cardElevated: Color
)

private val LightLiturgicalColors = LiturgicalColors(
    feastCrimson = ByzantineCrimson,
    feastCrimsonContainer = CrimsonContainerLight,
    goldAccent = LiturgicalGold,
    goldContainer = GoldContainerLight,
    lentPurple = LentPurple,
    lentPurpleContainer = LentPurpleContainer,
    fastFreeGreen = FastFreeGreen,
    fastFreeGreenContainer = FastFreeGreenContainer,
    fastingAmber = FastingAmber,
    fastingAmberContainer = FastingAmberContainer,
    cardElevated = ParchmentCard
)

private val DarkLiturgicalColors = LiturgicalColors(
    feastCrimson = DarkCrimson,
    feastCrimsonContainer = DarkCrimsonContainer,
    goldAccent = DarkGold,
    goldContainer = DarkGoldContainer,
    lentPurple = DarkLentPurple,
    lentPurpleContainer = DarkLentPurpleContainer,
    fastFreeGreen = DarkFastFreeGreen,
    fastFreeGreenContainer = DarkFastFreeGreenContainer,
    fastingAmber = DarkFastingAmber,
    fastingAmberContainer = DarkFastingAmberContainer,
    cardElevated = DarkCard
)

val LocalLiturgicalColors = staticCompositionLocalOf { LightLiturgicalColors }

private val LightColorScheme = lightColorScheme(
    primary = ByzantineCrimson,
    onPrimary = Color.White,
    primaryContainer = CrimsonContainerLight,
    onPrimaryContainer = OnCrimsonContainerLight,
    secondary = LiturgicalGold,
    onSecondary = Color.White,
    secondaryContainer = GoldContainerLight,
    onSecondaryContainer = OnGoldContainerLight,
    tertiary = FastFreeGreen,
    onTertiary = Color.White,
    tertiaryContainer = FastFreeGreenContainer,
    onTertiaryContainer = Color(0xFF10331D),
    background = ParchmentBackground,
    onBackground = InkPrimary,
    surface = ParchmentSurface,
    onSurface = InkPrimary,
    surfaceVariant = ParchmentVariant,
    onSurfaceVariant = InkSecondary,
    outline = OutlineWarm,
    outlineVariant = Color(0xFFE7DEC9)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkCrimson,
    onPrimary = Color(0xFF470B11),
    primaryContainer = DarkCrimsonContainer,
    onPrimaryContainer = Color(0xFFFFD9DC),
    secondary = DarkGold,
    onSecondary = Color(0xFF3E2C02),
    secondaryContainer = DarkGoldContainer,
    onSecondaryContainer = Color(0xFFFFE7AE),
    tertiary = DarkFastFreeGreen,
    onTertiary = Color(0xFF0C381F),
    tertiaryContainer = DarkFastFreeGreenContainer,
    onTertiaryContainer = Color(0xFFC5F2D5),
    background = DarkBackground,
    onBackground = DarkInkPrimary,
    surface = DarkSurface,
    onSurface = DarkInkPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkInkSecondary,
    outline = DarkOutline,
    outlineVariant = Color(0xFF3B312B)
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun OrthodoxCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val liturgicalColors = if (darkTheme) DarkLiturgicalColors else LightLiturgicalColors

    CompositionLocalProvider(LocalLiturgicalColors provides liturgicalColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}
