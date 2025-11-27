package nl.connectplay.scoreplay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.ui.theme.ScorePlayTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorePlayTopBar(
    title: String,
    onSearched: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searching: Boolean by remember { mutableStateOf(false) }

    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()

    ScorePlayTheme {
        CenterAlignedTopAppBar(
            title = {
                Text(title, textAlign = TextAlign.Center)
            },
            navigationIcon = {
                // todo: open search bar
                if (!searching) {
                    IconButton(
                        onClick = {
                            searching = !searching
                            scope.launch {
                                searchBarState.animateToExpanded()
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                    }
                } else {
                    SearchBar(
                        state = searchBarState,
                        inputField = {
                            TopBarSearchBar(
                                searchBarState = searchBarState,
                                textFieldState = textFieldState,
                                scope = scope,
                                onBack = { searching = !searching },
                                onSearched = { query -> onSearched(query) }, // bubble the search query up
                                modifier = modifier
                            )
                        },
                    )
                }
            },
            actions = {
                // todo: navigate to user / display their picture
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = "TODO account page")
                }
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface),
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarSearchBar(
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
    scope: CoroutineScope,
    onBack: () -> Unit = {},
    onSearched: (String) -> Unit = {},
    modifier: Modifier = Modifier) {
    SearchBarDefaults.InputField(
        modifier = modifier,
        searchBarState = searchBarState,
        textFieldState = textFieldState,
        onSearch = { query ->
            onSearched(query)
            onBack()
            scope.launch {
                searchBarState.animateToCollapsed()
            }
       },
        placeholder = {
            Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search...")
        },
        leadingIcon = {
            if (searchBarState.currentValue == SearchBarValue.Expanded) {
                TooltipBox(
                    positionProvider =
                        TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Above
                        ),
                    tooltip = { PlainTooltip { Text("Back") } },
                    state = rememberTooltipState(),
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                searchBarState.animateToCollapsed()
                                onBack()
                            }
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            } else {
                Icon(Icons.Filled.Search, contentDescription = null)
            }
        }
    )
}