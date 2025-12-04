package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.GameDetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameId: Int,
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) {
    val gameDetail: GameDetailViewModel = koinViewModel()
    val state = gameDetail.uiState.collectAsState()

    val carouselState = rememberCarouselState(0, { state.value.images.size } )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = ""/* TODO Game title */) },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box {
                HorizontalUncontainedCarousel(
                    state = carouselState,
                    itemWidth = 200.dp
                ) {

                }
            }
            // button group
            Row() {
                Text("kankerkankernkaerknakrknefkaekanrknkenakenkranekrnark")
            }
        }
    }
}