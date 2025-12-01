package nl.connectplay.scoreplay

import io.ktor.client.HttpClient
import nl.connectplay.scoreplay.api.ExampleApi
import nl.connectplay.scoreplay.api.http.Http
import nl.connectplay.scoreplay.viewModels.ExampleDetailViewModel
import nl.connectplay.scoreplay.viewModels.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import nl.connectplay.scoreplay.api.AuthApi
import nl.connectplay.scoreplay.stores.TokenDataStore
import nl.connectplay.scoreplay.viewModels.MainViewModel
import org.koin.core.module.dsl.singleOf

// Koin module to provide ViewModels
val viewModelsModule = module {
    viewModelOf(::ExampleDetailViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
}

// Koin module to provide networking / API dependencies
val apiModule = module {
    // Expose the existing Http.client via Koin
    single<HttpClient> { Http.client }

    // ExampleApi that depends on HttpClient
    single { ExampleApi(get()) }// get<HttpClient>()
    single { AuthApi(get()) }
}

// Koin module for app storage (DataStore)
val storeModule = module {
    singleOf(::TokenDataStore)
}