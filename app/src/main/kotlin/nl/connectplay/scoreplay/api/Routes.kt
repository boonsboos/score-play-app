package nl.connectplay.scoreplay.api

/**
 * Central place for all API Endpoint paths.
 *
 * Each nested object groups routes by feature/domain (Example, Auth, Games, ...)
 *
 * This gives you:
 * - A single source of truth for paths (if the backend changes, you update it here once).
 * - Clear structure: it's obvious which endpoints belong to which feature/domain.
 * - Less duplication and fewer string-typo bugs in your API classes.
 */
object Routes {
    /**
     * Routes for the "Example" feature/domain.
     */
    object Example {
        /**
         * Base path for all Example-related endpoints.
         * Combined with your Http base URL, this becomes:
         * - https://pma.connectplay.local/example
         */
        const val root = "/example"

        /**
         * Helper for "get one Example by id":
         * - https://pma.connectplay.local/example/{exampleId}
         *
         * Usage: Routes.Example.byId("123") → "/example/123"
         */
        fun byId(exampleId: String) = "$root/$exampleId"
    }

    object Games {
        private const val root = "/games"
        fun getAll() = root
    }

    object Auth {
        const val register = "/register"
        const val login = "/login"
    }

    object Friends {
        fun getFriends(userId: Int) = "/users/$userId/friends"
    }

    object FriendRequest {
        const val getAllFriendRequests = "/users/me/friendrequests"

        fun handleFriendRequest(userId: Int, friendId: Int) = "/users/$userId/friends/$friendId"
    }


    //    object Games {
//    object Games {
//        const val root = "/games"
//        fun byId(gameId: String) = "/games/$gameId"
//
//        object Pictures {
//            fun all(gameId: String) = "/games/$gameId/pictures"
//            fun byId(gameId: String, pictureId: String) = "/games/$gameId/pictures/$pictureId"
//        }
//
//        object Followers {
//            fun all(gameId: String) = "/games/$gameId/followers"
//            fun byUser(gameId: String, userId: String) = "/games/$gameId/followers/$userId"
//        }
//
//        object Sessions {
//            fun all(gameId: String) = "/games/$gameId/sessions"
//        }
//    }
//
//    object Sessions {
//        const val root = "/sessions"
//        fun byId(sessionId: String) = "/sessions/$sessionId"
//
//        object Players {
//            fun all(sessionId: String) = "/sessions/$sessionId/players"
//            fun byId(sessionId: String, playerId: String) = "/sessions/$sessionId/players/$playerId"
//        }
//
//        object Scores {
//            fun all(sessionId: String) = "/sessions/$sessionId/scores"
//            fun byId(sessionId: String, scoreId: String) = "/sessions/$sessionId/scores/$scoreId"
//        }
//    }
//
    object Users {
        const val root = "/users"
        fun getAll() = root
        const val me = "$root/me"
        fun deleteMe() = "$root/me"
        fun byId(userId: Int) = "$root/$userId"
        fun sessions(userId: Int) = "$root/$userId/sessions"
        fun followedGames(userId: Int) = "$root/$userId/followed"
    }

    //
    object Notifications {
        const val root = "/notifications"
        fun byId(notificationId: String) = "$root/$notificationId"
    }
}