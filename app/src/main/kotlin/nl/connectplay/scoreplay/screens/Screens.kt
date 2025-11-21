package nl.connectplay.scoreplay.screens

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object Screens {
    @Serializable
    data object Example : NavKey

    @Serializable
    data class ExampleDetail(val id: Int) : NavKey
}