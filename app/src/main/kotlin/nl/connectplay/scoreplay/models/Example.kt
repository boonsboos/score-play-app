package nl.connectplay.scoreplay.models

import androidx.compose.ui.graphics.Color
import nl.connectplay.scoreplay.screens.ExampleScreen
import kotlin.random.Random

data class Example(
    val id: Int,
    val name: String,
    val color: Color
)

val exampleList = List(50){
    Example(
        id= it,
        name = "Example #$it",
        color = Color(Random.nextLong(0xFFFFFFFF)).copy(alpha = 0.5f)
    )
}