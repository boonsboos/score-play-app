package nl.connectplay.scoreplay.models.session

data class PlayerUi(
    val id: Int,
    val name: String,
    val isCurrentUser: Boolean = false
)