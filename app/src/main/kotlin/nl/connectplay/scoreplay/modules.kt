package nl.connectplay.scoreplay

import io.ktor.client.HttpClient
import nl.connectplay.scoreplay.api.ExampleApi
import nl.connectplay.scoreplay.api.http.Http
import nl.connectplay.scoreplay.viewModels.ExampleDetailViewModel
import nl.connectplay.scoreplay.viewModels.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


// Koin module to provide ViewModels
val viewModels = module {
    viewModelOf(::ExampleDetailViewModel)
    viewModelOf(::LoginViewModel)
}

// Koin module to provide networking / API dependencies
val apiModule = module {
    // Expose the existing Http.client via Koin
    single<HttpClient> { Http.client }

    // ExampleApi that depends on HttpClient
    single {
        ExampleApi(get()) // get<HttpClient>()
    }
}