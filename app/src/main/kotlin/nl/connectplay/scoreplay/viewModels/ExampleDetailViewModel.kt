package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import nl.connectplay.scoreplay.models.exampleList

/*
 * A ViewModel needs to extend the ViewModel class from the lifecycle library
 *
 * It should only contain logic for a single screen. Unless the logic is shared between multiple screens.
 * In that case you can create a shared ViewModel that is used by multiple screens.
 * Like in an authentication flow where multiple screens have almost the same logic.
 */

class ExampleDetailViewModel(private val exampleId: Int) : ViewModel() {

    /*
     * Using a private MutableStateFlow to hold the state of the screen.
     * We can update the state internally, without exposing the ability to change it from outside.
     */
    private val _exampleState = MutableStateFlow(
        exampleList.first { it.id == exampleId }
    )
    /*
     * Here we expose the state as a read-only StateFlow
     * So the screen can observe it, but not change it.
     * To update the state, we would create functions inside the ViewModel
     */
    val exampleState = _exampleState.asStateFlow()
}