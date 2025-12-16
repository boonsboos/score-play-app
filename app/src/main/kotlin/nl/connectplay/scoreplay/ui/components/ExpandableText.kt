package nl.connectplay.scoreplay.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 3,
) {
    var showMore by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .animateContentSize(animationSpec = tween(100))
            .clickable(enabled = showMore) {
                showMore = !showMore
            }
            .fillMaxWidth()
    ) {
        if (showMore) {
            Text(text = text)
        } else {
            Text(text = text, maxLines = maxLines, overflow = TextOverflow.Ellipsis)
        }
    }
}