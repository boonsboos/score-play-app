package nl.connectplay.scoreplay.api

class HttpError(
    val status: Int,
    val statusText: String,
    val body: String? = null
) : Exception("HTTP $status $statusText")