package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import nl.connectplay.scoreplay.models.Example

/**
 * This class contains all API calls related to "Example".
 * It depends on a Ktor HttpClient, which is configured elsewhere (base URL, JSON, etc.).
 */
class ExampleApi(
    private val client: HttpClient
) {
    /**
     * GET /example
     *
     * Fetch all Example items.
     * Full URL = <baseUrl> + Routes.Example.root
     *
     * With your Http client config:
     * - baseUrl: https://api.connectplay.local
     * - Routes.Example.root: "/example"
     *   → https://api.connectplay.local/example
     *
     * .body<List<Example>>() uses the ContentNegotiation + json configuration:
     * - It reads the JSON response from the server
     * - It automatically deserializes it into List<Example> (because Example is @Serializable).
     */
    suspend fun getExampleList(): List<Example> {
        return client.get(Routes.Example.root).body()
    }

     /**
      * GET /example/{id}
      *
      * Fetch a single Example by its ID.
      *
      * Routes.Example.byId(id) builds a relative path like "/example/123",
      * which is combined with the base URL in your Http client.
     */
    suspend fun getExampleById(id: String): Example {
        return client
            .get(Routes.Example.byId(id))
            .body()
    }

    /**
     * POST /example
     *
     * Create a new `Example`.
     *
     * The [setBody] call sends the [example] object as the request body.
     *
     * Because the [HttpClient] has `ContentNegotiation { json(...) }` installed:
     * - The [example] object is automatically serialized to JSON.
     * - The `Content-Type: application/json` header is automatically added.
     * - You do NOT need to manually set `contentType(ContentType.Application.Json)`.
     *
     * The `body<Example>()` call then deserializes the JSON response
     * into an [Example] instance returned by the server.
     */
    suspend fun createExample(example: Example): Example {
        return client
            .post(Routes.Example.root) {
                setBody(example)
            }
            .body()
    }

    /**
     * PATCH /example/{id}
     *
     * Update an existing `Example` identified by [id].
     *
     * [Routes.Example.byId] is used to build the relative path `"/example/{id}"`,
     * which is combined with the base URL configured in the [HttpClient].
     *
     * Just like in [createExample], [setBody] together with
     * `ContentNegotiation { json(...) }` will:
     * - Serialize [example] to JSON.
     * - Set `Content-Type: application/json` automatically.
     *
     * The `body<Example>()` call reads and deserializes the updated `Example`
     * returned by the server.
     */
    suspend fun updateExample(id: String, example: Example): Example {
        return client
            .patch(Routes.Example.byId(id)) {
                setBody(example)
            }
            .body()
    }
}
