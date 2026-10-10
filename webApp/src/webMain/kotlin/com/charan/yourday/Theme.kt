package com.charan.yourday

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ColorScheme as MaterialColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.composeunstyled.theme.ColorScheme
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildThemeV2
import org.jetbrains.compose.resources.Font
import yourday.webapp.generated.resources.Res

val ZincLightColorScheme = lightColorScheme(
    primary = Color(0xFF18181B),
    onPrimary = Color(0xFFFAFAFA),
    primaryContainer = Color(0xFFE4E4E7),
    onPrimaryContainer = Color(0xFF18181B),

    secondary = Color(0xFF71717A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF4F4F5),
    onSecondaryContainer = Color(0xFF27272A),

    tertiary = Color(0xFF52525B),
    onTertiary = Color(0xFFFFFFFF),

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF18181B),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF18181B),
    surfaceVariant = Color(0xFFF4F4F5),
    onSurfaceVariant = Color(0xFF52525B),

    outline = Color(0xFFE4E4E7),
    outlineVariant = Color(0xFFF4F4F5),

    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),

    inverseSurface = Color(0xFF27272A),
    inverseOnSurface = Color(0xFFFAFAFA),
    inversePrimary = Color(0xFFD4D4D8),
)

val ZincDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFAFAFA),
    onPrimary = Color(0xFF18181B),
    primaryContainer = Color(0xFF27272A),
    onPrimaryContainer = Color(0xFFF4F4F5),

    secondary = Color(0xFFA1A1AA),
    onSecondary = Color(0xFF18181B),
    secondaryContainer = Color(0xFF27272A),
    onSecondaryContainer = Color(0xFFF4F4F5),

    tertiary = Color(0xFF71717A),
    onTertiary = Color(0xFFFAFAFA),

    background = Color(0xFF09090B),
    onBackground = Color(0xFFFAFAFA),

    surface = Color(0xFF121215),
    onSurface = Color(0xFFFAFAFA),
    surfaceVariant = Color(0xFF1E1E22),
    onSurfaceVariant = Color(0xFFA1A1AA),

    outline = Color(0xFF27272A),
    outlineVariant = Color(0xFF1E1E22),

    error = Color(0xFFF87171),
    onError = Color(0xFF18181B),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),

    inverseSurface = Color(0xFFF4F4F5),
    inverseOnSurface = Color(0xFF27272A),
    inversePrimary = Color(0xFF18181B),
)

object ZincTokens {
    val colors = ThemeProperty<Color>("colors")

    val primary = ThemeToken<Color>("primary")
    val onPrimary = ThemeToken<Color>("on_primary")
    val primaryContainer = ThemeToken<Color>("primary_container")
    val onPrimaryContainer = ThemeToken<Color>("on_primary_container")

    val secondary = ThemeToken<Color>("secondary")
    val onSecondary = ThemeToken<Color>("on_secondary")
    val secondaryContainer = ThemeToken<Color>("secondary_container")
    val onSecondaryContainer = ThemeToken<Color>("on_secondary_container")

    val tertiary = ThemeToken<Color>("tertiary")
    val onTertiary = ThemeToken<Color>("on_tertiary")

    val background = ThemeToken<Color>("background")
    val onBackground = ThemeToken<Color>("on_background")

    val surface = ThemeToken<Color>("surface")
    val onSurface = ThemeToken<Color>("on_surface")
    val surfaceVariant = ThemeToken<Color>("surface_variant")
    val onSurfaceVariant = ThemeToken<Color>("on_surface_variant")

    val outline = ThemeToken<Color>("outline")
    val outlineVariant = ThemeToken<Color>("outline_variant")

    val error = ThemeToken<Color>("error")
    val onError = ThemeToken<Color>("on_error")
    val errorContainer = ThemeToken<Color>("error_container")
    val onErrorContainer = ThemeToken<Color>("on_error_container")

    val inverseSurface = ThemeToken<Color>("inverse_surface")
    val inverseOnSurface = ThemeToken<Color>("inverse_on_surface")
    val inversePrimary = ThemeToken<Color>("inverse_primary")

    // Typography
    val typography = ThemeProperty<TextStyle>("typography")

    val displayLarge = ThemeToken<TextStyle>("display_large")
    val displayMedium = ThemeToken<TextStyle>("display_medium")
    val headlineLarge = ThemeToken<TextStyle>("headline_large")
    val headlineMedium = ThemeToken<TextStyle>("headline_medium")
    val titleLarge = ThemeToken<TextStyle>("title_large")
    val titleMedium = ThemeToken<TextStyle>("title_medium")
    val titleSmall = ThemeToken<TextStyle>("title_small")
    val bodyLarge = ThemeToken<TextStyle>("body_large")
    val bodyMedium = ThemeToken<TextStyle>("body_medium")
    val bodySmall = ThemeToken<TextStyle>("body_small")
    val labelLarge = ThemeToken<TextStyle>("label_large")
    val labelMedium = ThemeToken<TextStyle>("label_medium")
    val labelSmall = ThemeToken<TextStyle>("label_small")
    val code = ThemeToken<TextStyle>("code")
}

val colors = ZincTokens.colors
val primary = ZincTokens.primary
val onPrimary = ZincTokens.onPrimary
val primaryContainer = ZincTokens.primaryContainer
val onPrimaryContainer = ZincTokens.onPrimaryContainer
val secondary = ZincTokens.secondary
val onSecondary = ZincTokens.onSecondary
val secondaryContainer = ZincTokens.secondaryContainer
val onSecondaryContainer = ZincTokens.onSecondaryContainer
val tertiary = ZincTokens.tertiary
val onTertiary = ZincTokens.onTertiary
val background = ZincTokens.background
val onBackground = ZincTokens.onBackground
val surface = ZincTokens.surface
val onSurface = ZincTokens.onSurface
val surfaceVariant = ZincTokens.surfaceVariant
val onSurfaceVariant = ZincTokens.onSurfaceVariant
val outline = ZincTokens.outline
val outlineVariant = ZincTokens.outlineVariant
val error = ZincTokens.error
val onError = ZincTokens.onError
val errorContainer = ZincTokens.errorContainer
val onErrorContainer = ZincTokens.onErrorContainer
val inverseSurface = ZincTokens.inverseSurface
val inverseOnSurface = ZincTokens.inverseOnSurface
val inversePrimary = ZincTokens.inversePrimary

val typography = ZincTokens.typography
val displayLarge = ZincTokens.displayLarge
val displayMedium = ZincTokens.displayMedium
val headlineLarge = ZincTokens.headlineLarge
val headlineMedium = ZincTokens.headlineMedium
val titleLarge = ZincTokens.titleLarge
val titleMedium = ZincTokens.titleMedium
val titleSmall = ZincTokens.titleSmall
val bodyLarge = ZincTokens.bodyLarge
val bodyMedium = ZincTokens.bodyMedium
val bodySmall = ZincTokens.bodySmall
val labelLarge = ZincTokens.labelLarge
val labelMedium = ZincTokens.labelMedium
val labelSmall = ZincTokens.labelSmall
val code = ZincTokens.code

private fun MaterialColorScheme.toTokenMap(): Map<ThemeToken<Color>, Color> = mapOf(
    ZincTokens.primary to this.primary,
    ZincTokens.onPrimary to this.onPrimary,
    ZincTokens.primaryContainer to this.primaryContainer,
    ZincTokens.onPrimaryContainer to this.onPrimaryContainer,

    ZincTokens.secondary to this.secondary,
    ZincTokens.onSecondary to this.onSecondary,
    ZincTokens.secondaryContainer to this.secondaryContainer,
    ZincTokens.onSecondaryContainer to this.onSecondaryContainer,

    ZincTokens.tertiary to this.tertiary,
    ZincTokens.onTertiary to this.onTertiary,

    ZincTokens.background to this.background,
    ZincTokens.onBackground to this.onBackground,

    ZincTokens.surface to this.surface,
    ZincTokens.onSurface to this.onSurface,
    ZincTokens.surfaceVariant to this.surfaceVariant,
    ZincTokens.onSurfaceVariant to this.onSurfaceVariant,

    ZincTokens.outline to this.outline,
    ZincTokens.outlineVariant to this.outlineVariant,

    ZincTokens.error to this.error,
    ZincTokens.onError to this.onError,
    ZincTokens.errorContainer to this.errorContainer,
    ZincTokens.onErrorContainer to this.onErrorContainer,

    ZincTokens.inverseSurface to this.inverseSurface,
    ZincTokens.inverseOnSurface to this.inverseOnSurface,
    ZincTokens.inversePrimary to this.inversePrimary,
)

val AppTheme = buildThemeV2 {
    name = "YourDayTheme"

    defaultTextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    )

    properties[typography] = mapOf(
        ZincTokens.displayLarge to TextStyle(
            
            fontWeight = FontWeight.Bold,
            fontSize = 48.sp,
            lineHeight = 56.sp,
            letterSpacing = (-0.8).sp,
        ),
        ZincTokens.displayMedium to TextStyle(
            
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = (-0.5).sp,
        ),
        ZincTokens.headlineLarge to TextStyle(
            
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = (-0.3).sp,
        ),
        ZincTokens.headlineMedium to TextStyle(
            
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 30.sp,
            letterSpacing = (-0.2).sp,
        ),
        ZincTokens.titleLarge to TextStyle(
            
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 26.sp,
        ),
        ZincTokens.titleMedium to TextStyle(
            
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        ZincTokens.titleSmall to TextStyle(
            
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        ZincTokens.bodyLarge to TextStyle(
            
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 26.sp,
        ),
        ZincTokens.bodyMedium to TextStyle(
            
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 22.sp,
        ),
        ZincTokens.bodySmall to TextStyle(
            
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        ),
        ZincTokens.labelLarge to TextStyle(
            
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp,
        ),
        ZincTokens.labelMedium to TextStyle(
            
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        ),
        ZincTokens.labelSmall to TextStyle(
            
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp,
        ),
        ZincTokens.code to TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        ),
    )

    colorScheme(ColorScheme.Light) {
        defaultContentColor = ZincLightColorScheme.onBackground

        defaultTextSelectionColors = TextSelectionColors(
            handleColor = ZincLightColorScheme.primary,
            backgroundColor = ZincLightColorScheme.primary.copy(alpha = 0.2f),
        )

        properties[colors] = ZincLightColorScheme.toTokenMap()
    }

    colorScheme(ColorScheme.Dark) {
        defaultContentColor = ZincDarkColorScheme.onBackground

        defaultTextSelectionColors = TextSelectionColors(
            handleColor = ZincDarkColorScheme.primary,
            backgroundColor = ZincDarkColorScheme.primary.copy(alpha = 0.25f),
        )

        properties[colors] = ZincDarkColorScheme.toTokenMap()
    }
}
