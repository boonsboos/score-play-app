package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.connectplay.scoreplay.ui.components.ScorePlayButton
import nl.connectplay.scoreplay.viewModels.ExampleDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExampleDetailScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExampleDetailViewModel = koinViewModel()
) {
    // Collecting the example state from the ViewModel
    val exampleState by viewModel.exampleState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = exampleState.name,
            fontSize = 26.sp,
            modifier = Modifier
                // Adding rounded corners and background color from the state
                .clip(RoundedCornerShape(10.dp))
                // Setting the background color based on the example state
                .background(exampleState.color)
                .padding(16.dp)
        )
        // This is just to add some space between the text and the button
        Spacer(modifier = Modifier.height(16.dp))
        // Using the custom ScorePlayButton component
        // we pass the label and onClick action
        ScorePlayButton(label = "Go back", onClick = onBackClick)
    }
}