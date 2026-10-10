package com.charan.yourday

import androidx.compose.foundation.Image
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeViewport
import com.composeunstyled.theme.ColorScheme
import com.composeunstyled.theme.Theme
import kotlinx.browser.window
import org.jetbrains.compose.resources.painterResource
import yourday.webapp.generated.resources.Res
import yourday.webapp.generated.resources.logo

internal val BrandCoral = Color(0xFFE25A53)
internal val PageBackground = Color(0xFF09090B)
internal val ZincBorder = Color(0xFF27272A)
internal val ZincCardBg = Color(0xFF111114)
internal val ZincMuted = Color(0xFFA1A1AA)

private object NoIndicationNodeFactory : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): Modifier.Node {
        return object : Modifier.Node() {}
    }
    override fun hashCode(): Int = -1
    override fun equals(other: Any?): Boolean = other === this
}

sealed interface Screen {
    data object Home : Screen
    data class AuthRedirect(val provider: String, val code: String?, val error: String?) : Screen
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        var currentScreen by remember { mutableStateOf<Screen>(resolveCurrentScreen()) }

        AppTheme(
            colorScheme = ColorScheme.Dark,
        ) {
            CompositionLocalProvider(LocalIndication provides NoIndicationNodeFactory) {
                when (val screen = currentScreen) {
                    is Screen.Home -> {
                        Homepage()
                    }
                    is Screen.AuthRedirect -> {
                        AuthRedirectPage(
                            provider = screen.provider,
                            code = screen.code,
                            error = screen.error,
                            onGoHome = { currentScreen = Screen.Home },
                        )
                    }
                }
            }
        }
    }
}

private fun resolveCurrentScreen(): Screen {
    return try {
        val pathname = window.location.pathname
        val search = window.location.search

        fun extractParam(key: String): String? {
            val query = if (search.startsWith("?")) search.substring(1) else search
            return query.split("&")
                .map { it.split("=") }
                .firstOrNull { it.size == 2 && it[0] == key }
                ?.get(1)
        }

        when {
            pathname.contains("/authentication/todoist") -> Screen.AuthRedirect(
                provider = "todoist",
                code = extractParam("code"),
                error = extractParam("error"),
            )
            pathname.contains("/authentication/ticktick") -> Screen.AuthRedirect(
                provider = "ticktick",
                code = extractParam("code"),
                error = extractParam("error"),
            )
            else -> Screen.Home
        }
    } catch (_: Throwable) {
        Screen.Home
    }
}

@Composable
fun AppText(
    text: String,
    style: TextStyle = Theme[typography][bodyMedium],
    color: Color = Theme[colors][onBackground],
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    textDecoration: TextDecoration? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    BasicText(
        text = text,
        style = style.copy(
            color = color,
            textAlign = textAlign,
            textDecoration = textDecoration,
        ),
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun AuthRedirectPage(
    provider: String,
    code: String?,
    error: String?,
    onGoHome: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val deepLink = remember(provider, code, error) {
        when {
            code != null -> "yourday://code=$code,provider=$provider"
            error != null -> "yourday://error=$error,provider=$provider"
            else -> "yourday://"
        }
    }

    LaunchedEffect(deepLink) {
        try {
            window.location.href = deepLink
        } catch (_: Throwable) {
            // Fallback button provided
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, ZincBorder, RoundedCornerShape(20.dp))
                .background(ZincCardBg)
                .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "YourDay Logo",
                modifier = Modifier.size(80.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppText(
                text = if (error == null) "Connecting to YourDay..." else "Authentication Failed",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    lineHeight = 30.sp,
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppText(
                text = if (error == null) {
                    "Redirecting back to the YourDay app. If the app doesn't open automatically, tap the button below."
                } else {
                    "An error occurred while authenticating with $provider: $error"
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                ),
                color = ZincMuted,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(28.dp))

            val buttonInteractionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable(
                        interactionSource = buttonInteractionSource,
                        indication = null,
                        onClick = {
                            try {
                                window.location.href = deepLink
                            } catch (_: Throwable) {
                                uriHandler.openUri(deepLink)
                            }
                        },
                    )
                    .pointerHoverIcon(PointerIcon.Hand)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                AppText(
                    text = "Open iOS App",
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    ),
                    color = Color(0xFF09090B),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val homeInteractionSource = remember { MutableInteractionSource() }
            AppText(
                text = "Back to Homepage",
                style = TextStyle(
                    fontSize = 13.sp,
                    textDecoration = TextDecoration.Underline,
                ),
                color = ZincMuted,
                modifier = Modifier
                    .pointerHoverIcon(PointerIcon.Hand)
                    .clickable(
                        interactionSource = homeInteractionSource,
                        indication = null,
                        onClick = onGoHome,
                    ),
            )
        }
    }
}
