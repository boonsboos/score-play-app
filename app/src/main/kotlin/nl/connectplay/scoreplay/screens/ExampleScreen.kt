package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.connectplay.scoreplay.models.exampleList

@Composable
fun ExampleScreen(
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        // Populate the list with example items
        items(exampleList, key = { example -> example.id }) { example ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
                    .background(example.color)
                    .clickable {
                        onClick(example.id)
                    }
            ) {
                Text(
                    text = example.name,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 8.dp),
                    fontSize = 18.sp
                )
            }
        }
    }
}