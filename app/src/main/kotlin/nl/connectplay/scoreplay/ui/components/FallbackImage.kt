package nl.connectplay.scoreplay.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun FallbackImage(
    url: Any?,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    size: Dp,
    fallback: @Composable () -> Unit,
) {
    if (url != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(shape)
                .then(modifier),
            error = rememberVectorPainter(Icons.Outlined.Close)
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .then(modifier),
            contentAlignment = Alignment.Center
        ) { fallback() }
    }
}
