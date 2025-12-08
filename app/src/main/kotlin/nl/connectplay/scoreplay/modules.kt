package nl.connectplay.scoreplay

import io.ktor.client.HttpClient
import nl.connectplay.scoreplay.api.AuthApi
import nl.connectplay.scoreplay.api.ExampleApi
import nl.connectplay.scoreplay.api.FriendRequestApi
import nl.connectplay.scoreplay.api.FriendsApi
import nl.connectplay.scoreplay.api.GameApi
import nl.connectplay.scoreplay.api.SearchApi
import nl.connectplay.scoreplay.api.http.Http
import nl.connectplay.scoreplay.stores.TokenDataStore
import nl.connectplay.scoreplay.stores.UserDataStore
import nl.connectplay.scoreplay.viewModels.ExampleDetailViewModel
import nl.connectplay.scoreplay.viewModels.FriendViewModel
import nl.connectplay.scoreplay.viewModels.GamesListViewModel
import nl.connectplay.scoreplay.viewModels.RegisterViewModel
import nl.connectplay.scoreplay.viewModels.SearchViewModel
import nl.connectplay.scoreplay.viewModels.login.LoginViewModel
import nl.connectplay.scoreplay.viewModels.main.MainViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// Koin module to provide ViewModels
val viewModelsModule = module {
    viewModelOf(::ExampleDetailViewModel)
    // RegisterViewModel with AuthAPI
    viewModelOf(::RegisterViewModel)
    viewModelOf(::GamesListViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
    viewModel {
        FriendViewModel(
            friendsApi = get(),
            friendRequestApi = get(),
            userDataStore = get()
        )
    }
    viewModelOf(::SearchViewModel)
}

// Koin module to provide networking / API dependencies
val apiModule = module {
    // Expose the existing Http.client via Koin
    single<HttpClient> { Http.client }

    // ExampleApi that depends on HttpClient
    single {
        ExampleApi(get()) // get<HttpClient>()
    }

    // AuthApi that depends on HttpClient
    single { AuthApi(get()) }
    single { GameApi(get()) }
    single { FriendsApi(get()) }
    single { FriendRequestApi(get()) }
    single { SearchApi(get()) }
}

// Koin module for app storage (DataStore)
val storeModule = module {
    singleOf(::TokenDataStore)
    singleOf(::UserDataStore)
}