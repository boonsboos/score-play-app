package nl.connectplay.scoreplay.exceptions

class InvalidTokenException(message: String? = null) :
    Exception(message ?: "The provided token is invalid.")