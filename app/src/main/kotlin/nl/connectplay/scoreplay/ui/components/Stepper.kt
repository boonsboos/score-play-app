package nl.connectplay.scoreplay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Stepper(
    steps: List<String>,
    currentStep: Int,
    onStepChange: (Int) -> Unit,
    canGoNext: Boolean,
    content: @Composable (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Step indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                steps.forEachIndexed { index, title ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    if (index == currentStep)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            HorizontalDivider()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                content(currentStep)
            }
        }

        // BACK FAB
        if (currentStep > 0) {
            FloatingActionButton(
                onClick = { onStepChange(currentStep - 1) },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Step back",
                    tint = if (canGoNext)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // NEXT FAB
        FloatingActionButton(
            onClick = {
                if (currentStep < steps.size - 1) {
                    onStepChange(currentStep + 1)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = if (canGoNext)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Next step",
                tint = if (canGoNext)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
