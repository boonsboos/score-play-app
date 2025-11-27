package nl.connectplay.scoreplay.api

object Routes {
    object Auth {
        const val signup = "/auth/signup"
        const val signin = "auth/signin"
    }
    object Games {
        const val root = "/games"
        fun byId(gameId: String) = "/games/$gameId"

        object Pictures {
            fun all(gameId: String) = "/games/$gameId/pictures"
            fun byId(gameId: String, pictureId: String) = "/games/$gameId/pictures/$pictureId"
        }

        object Followers {
            fun all(gameId: String) = "/games/$gameId/followers"
            fun byUser(gameId: String, userId: String) = "/games/$gameId/followers/$userId"
        }

        object Sessions {
            fun all(gameId: String) = "/games/$gameId/sessions"
        }
    }

    object Sessions {
        const val root = "/sessions"
        fun byId(sessionId: String) = "/sessions/$sessionId"

        object Players {
            fun all(sessionId: String) = "/sessions/$sessionId/players"
            fun byId(sessionId: String, playerId: String) = "/sessions/$sessionId/players/$playerId"
        }

        object Scores {
            fun all(sessionId: String) = "/sessions/$sessionId/scores"
            fun byId(sessionId: String, scoreId: String) = "/sessions/$sessionId/scores/$scoreId"
        }
    }

    object Users {
        const val root = "/users"
        fun byId(userId: String) = "/users/$userId"
        fun followed(userId: String) = "/users/$userId/followed"
        fun friends(userId: String) = "/users/$userId/friends"
        fun friendById(userId: String, friendId: String) = "/users/$userId/friends/$friendId"
        fun sessions(userId: String) = "/users/$userId/sessions"
        fun sessionById(userId: String, sessionId: String) = "/users/$userId/sessions/$sessionId"

        object Picture {
            fun one(userId: String) = "/users/$userId/picture"
        }
    }

    object Notifications {
        const val root = "/notifications"
        fun byId(notificationId: String) = "/notifications/$notificationId"
    }
}