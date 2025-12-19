package nl.connectplay.scoreplay.ui.components

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class SpeedDialAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SpeedDial(
    actions: List<SpeedDialAction>,
    modifier: Modifier = Modifier,
    mainIcon: @Composable () -> Unit = {
        Icon(Icons.Default.Add, contentDescription = "SpeedDial")
    }
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {

        actions.forEachIndexed { index, action ->
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(150)) + slideInVertically(
                    initialOffsetY = { it / 2 }
                ),
                exit = fadeOut(tween(150)) + slideOutVertically(
                    targetOffsetY = { it / 2 }
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        tonalElevation = 6.dp
                    ) {
                        Text(
                            text = action.label,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    SmallFloatingActionButton(
                        onClick = {
                            expanded = false
                            action.onClick()
                        }
                    ) {
                        Icon(action.icon, contentDescription = action.label)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { expanded = !expanded }
        ) {
            mainIcon()
        }
    }
}
