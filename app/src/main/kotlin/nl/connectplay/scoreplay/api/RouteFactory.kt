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
object RouteFactory {
    object Games {
        private const val root = "/games"
        fun getAll() = root
        fun single(gameId: Int) = "$root/$gameId"
        fun follow(gameId: Int) = "$root/$gameId/follow"
        fun unfollow(gameId: Int) = "$root/$gameId/unfollow"

        object Leaderboard {
            fun scores(gameId: Int) = single(gameId) + "/leaderboard"
        }
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

        fun addFriend(userId: Int) = "/users/$userId/friends"

        fun removeFriend(userId: Int, friendId: Int) = "/users/$userId/friends/$friendId"

        fun handleFriendRequest(userId: Int, friendId: Int) = "/users/$userId/friends/$friendId"
    }

    object Sessions {
        const val root = "/sessions"

        fun byId(sessionId: String) = "$root/$sessionId"

        fun picture(sessionId: String) = byId(sessionId) + "/picture"

        fun byUserAndSessionId(userId: Int, sessionId: String) = "/users/$userId/sessions/$sessionId"

        object Players {
            fun all(sessionId: String) = "$root/$sessionId/players"
            fun byId(sessionId: String, playerId: String) = "$root/$sessionId/players/$playerId"
        }

        object Scores {
            fun all(sessionId: String) = "$root/$sessionId/scores"
        }
    }

    object Users {
        const val root = "/users"
        fun getAll() = root
        const val me = "$root/me"

        const val uploadPicture = "$root/me/picture"

        fun byId(userId: Int) = "$root/$userId"
        fun sessions(userId: Int) = "$root/$userId/sessions"
        const val recent = "$root/me/recent"
        fun followedGames(userId: Int, withPodium: Boolean) = "$root/$userId/followed" +
                if (withPodium) "?withPodium" else ""
    }

    object Notifications {
        const val root = "/notifications"
        const val live = "$root/live" // SSE endpoint for live notifications
        fun byId(notificationId: String) = "$root/$notificationId"
    }
}