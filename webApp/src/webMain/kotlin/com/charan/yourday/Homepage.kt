package com.charan.yourday

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.browser.window
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import yourday.webapp.generated.resources.Res
import yourday.webapp.generated.resources.ic_github
import yourday.webapp.generated.resources.ic_jetpack_compose
import yourday.webapp.generated.resources.ic_kotlin
import yourday.webapp.generated.resources.ic_swift
import yourday.webapp.generated.resources.logo
import yourday.webapp.generated.resources.shot_android_home_screen
import yourday.webapp.generated.resources.shot_android_onboarding
import yourday.webapp.generated.resources.shot_ios_home_screen
import yourday.webapp.generated.resources.shot_ios_onboarding

private const val GITHUB_URL = "https://github.com/sai0x20charan/YourDay"
private val ContentMaxWidth = 1040.dp
private val Hairline = Color(0x14FFFFFF)

internal fun openExternalUrl(url: String) {
    try {
        val target = window.open(url, "_blank")
        if (target == null || target.closed) {
            window.location.href = url
        }
    } catch (_: Throwable) {
        window.location.href = url
    }
}

private enum class ShowcaseCategory(
    val title: String,
    val images: List<DrawableResource>,
) {
    Android(
        title = "Android",
        images = listOf(
            Res.drawable.shot_android_home_screen,
        ),
    ),
    IOS(
        title = "iOS",
        images = listOf(
            Res.drawable.shot_ios_home_screen,
        ),
    ),
}

@Composable
fun Homepage() {
    val openGitHub = { openExternalUrl(GITHUB_URL) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground),
    ) {
        // Soft warm glow behind the hero, matching the screenshot backdrops.
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(900.dp, 560.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BrandCoral.copy(alpha = 0.12f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item { PageSection { Hero(onOpenGitHub = openGitHub) } }
            item { PageSection { HowItsBuilt() } }
            item { PageSection(maxWidth = 920.dp) { Screenshots() } }

            item { PageSection { Footer(onOpenGitHub = openGitHub) } }
        }
    }
}

@Composable
private fun PageSection(
    maxWidth: Dp = ContentMaxWidth,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .widthIn(max = maxWidth)
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        content()
    }
}

@Composable
private fun Hero(onOpenGitHub: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 104.dp, bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "YourDay logo",
            modifier = Modifier.size(64.dp),
        )

        Spacer(modifier = Modifier.height(28.dp))

        AppText(
            text = "YourDay",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 52.sp,
                lineHeight = 58.sp,
                letterSpacing = (-1.4).sp,
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppText(
            text = "Today's weather, calendar events and tasks, on one screen.",
            style = TextStyle(
                fontSize = 18.sp,
                lineHeight = 28.sp,
            ),
            color = ZincMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 540.dp),
        )

        Spacer(modifier = Modifier.height(36.dp))

        GitHubButton(onClick = onOpenGitHub)
    }
}

@Composable
private fun GitHubButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isHovered) Color(0xFFE4E4E7) else Color.White)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = 22.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_github),
            contentDescription = null,
            colorFilter = ColorFilter.tint(PageBackground),
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        AppText(
            text = "View on GitHub",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            ),
            color = PageBackground,
        )
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String? = null) {
    Column {
        AppText(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                letterSpacing = (-0.4).sp,
            ),
            color = Color.White,
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(8.dp))
            AppText(
                text = subtitle,
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                ),
                color = ZincMuted,
                modifier = Modifier.widthIn(max = 560.dp),
            )
        }
    }
}

@Composable
private fun Screenshots() {
    var selectedCategory by remember { mutableStateOf(ShowcaseCategory.Android) }
    var currentSlideIndex by remember(selectedCategory) { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CategorySegmentedControl(
            selected = selectedCategory,
            onSelect = {
                selectedCategory = it
                currentSlideIndex = 0
            },
        )

        Spacer(modifier = Modifier.height(28.dp))

        CategoryCarousel(
            category = selectedCategory,
            currentSlideIndex = currentSlideIndex,
            onSlideChange = { currentSlideIndex = it },
        )
    }
}

@Composable
private fun CategorySegmentedControl(
    selected: ShowcaseCategory,
    onSelect: (ShowcaseCategory) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFF141417))
            .border(1.dp, ZincBorder, RoundedCornerShape(999.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ShowcaseCategory.entries.forEach { category ->
            val isSelected = category == selected
            val interactionSource = remember { MutableInteractionSource() }
            val isHovered by interactionSource.collectIsHoveredAsState()

            val backgroundColor = when {
                isSelected -> BrandCoral
                isHovered -> Color(0xFF222226)
                else -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(backgroundColor)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onSelect(category) },
                    )
                    .pointerHoverIcon(PointerIcon.Hand)
                    .padding(horizontal = 18.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center,
            ) {
                AppText(
                    text = category.title,
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    ),
                    color = if (isSelected) Color.White else ZincMuted,
                )
            }
        }
    }
}

@Composable
private fun CategoryCarousel(
    category: ShowcaseCategory,
    currentSlideIndex: Int,
    onSlideChange: (Int) -> Unit,
) {
    val images = category.images
    val safeIndex = currentSlideIndex.coerceIn(0, images.lastIndex)
    val currentImage = images[safeIndex]

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CarouselViewport(
            image = currentImage,
        )
    }
}

@Composable
private fun CarouselViewport(
    image: DrawableResource,
) {
    Image(
        painter = painterResource(image),
        contentDescription = "App Screenshot",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, ZincBorder, RoundedCornerShape(24.dp)),
    )
}

private data class TechItem(
    val icon: DrawableResource,
    val title: String,
)

private val techStack = listOf(
    TechItem(Res.drawable.ic_kotlin, "Kotlin Multiplatform"),
    TechItem(Res.drawable.ic_jetpack_compose, "Jetpack Compose"),
    TechItem(Res.drawable.ic_swift, "SwiftUI"),
)

@Composable
private fun HowItsBuilt() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 100.dp),
    ) {
        SectionTitle(
            title = "Built With :",
        )

        Spacer(modifier = Modifier.height(32.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val columns = when {
                maxWidth >= 800.dp -> 3
                maxWidth >= 480.dp -> 2
                else -> 1
            }
            val gap = 12.dp

            Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                techStack.chunked(columns).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                        row.forEach { item ->
                            TechCard(
                                item = item,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(columns - row.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TechCard(item: TechItem, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isHovered) Color(0xFF17171B) else ZincCardBg)
            .border(
                1.dp,
                if (isHovered) Color(0xFF3F3F46) else Hairline,
                RoundedCornerShape(14.dp),
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(item.icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        AppText(
            text = item.title,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp,
            ),
            color = Color.White,
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Hairline),
    )
}

@Composable
private fun Footer(onOpenGitHub: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 120.dp, bottom = 48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            AppText(
                text = "YourDay",
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                color = ZincMuted,
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()
        AppText(
            text = "Source on GitHub",
            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
            color = if (isHovered) Color.White else ZincMuted,
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onOpenGitHub,
                ),
        )
    }
}
