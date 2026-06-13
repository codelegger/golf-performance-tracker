package com.codelegger.golfperformancetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.codelegger.golfperformancetracker.theme.clubColor
import com.codelegger.golfperformancetracker.theme.onClubColor
import com.codelegger.golfperformancetracker.theme.personColor

/**
 * Circular player avatar. Loads [imageUrl] via Coil when present; otherwise (or while loading)
 * shows the person's initials on a stable per-name color. The colored circle is the one place
 * we tint player chrome — matching Rapsodo's colored circles.
 */
@Composable
fun PlayerAvatar(name: String, imageUrl: String?, size: Int, modifier: Modifier = Modifier) {
    val initials = name.trim().split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
    val background: Color = personColor(name)
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        // Initials sit underneath as the fallback / placeholder while the image loads.
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = onClubColor(background),
        )
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "$name avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
            )
        }
    }
}

/** Category (club) badge — the one place color carries meaning. */
@Composable
fun ClubBadge(club: String, modifier: Modifier = Modifier) {
    val background = clubColor(club)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(background)
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = club.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = onClubColor(background),
        )
    }
}
