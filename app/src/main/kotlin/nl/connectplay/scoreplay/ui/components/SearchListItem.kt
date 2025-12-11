package nl.connectplay.scoreplay.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun SearchListItem(
    title: String,
    subtitle: String? = null,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // this will go to the detail page of user or game
        leadingContent = icon,
        headlineContent = { Text(title) },
        supportingContent = {
            // only show subtitle when it exist (games only)
            if (subtitle != null) Text(subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    )
    // creates a line under the item
    HorizontalDivider()
}