package nl.connectplay.scoreplay.api.http

class HttpError(
    val status: Int,
    val statusText: String,
    val body: String? = null
) : Exception("HTTP $status $statusText")