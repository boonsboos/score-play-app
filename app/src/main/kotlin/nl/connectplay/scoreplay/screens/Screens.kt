package nl.connectplay.scoreplay.screens

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// this object list all screens the app can go to
object Screens {
    @Serializable
    data object Example : NavKey

    @Serializable
    data object Register : NavKey

    @Serializable
    data object Home : NavKey

    @Serializable
    data object Friends : NavKey

    @Serializable
    data object Games : NavKey

    @Serializable
    data object Notifications : NavKey

    @Serializable
    data object Login : NavKey

    @Serializable
    data class Search(val query: String) : NavKey

    @Serializable
    data class ExampleDetail(val id: Int) : NavKey
}