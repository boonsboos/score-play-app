package nl.connectplay.scoreplay

import androidx.room.Room
import io.ktor.client.HttpClient
import nl.connectplay.scoreplay.api.AuthApi
import nl.connectplay.scoreplay.api.ExampleApi
import nl.connectplay.scoreplay.api.GameApi
import nl.connectplay.scoreplay.api.SessionApi
import nl.connectplay.scoreplay.api.http.Http
import nl.connectplay.scoreplay.dao.SessionDao
import nl.connectplay.scoreplay.database.SessionDatabase
import nl.connectplay.scoreplay.viewModels.ExampleDetailViewModel
import nl.connectplay.scoreplay.viewModels.RegisterViewModel
import nl.connectplay.scoreplay.viewModels.GamesListViewModel
import nl.connectplay.scoreplay.viewModels.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import nl.connectplay.scoreplay.stores.TokenDataStore
import nl.connectplay.scoreplay.viewModels.MainViewModel
import nl.connectplay.scoreplay.viewModels.SessionViewModel
import org.koin.core.module.dsl.singleOf

// Koin module to provide ViewModels
val viewModelsModule = module {
    viewModelOf(::ExampleDetailViewModel)

    // RegisterViewModel with AuthAPI
    viewModelOf(::RegisterViewModel)
    viewModelOf(::GamesListViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::SessionViewModel)
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
    single { SessionApi(get()) }
}

// Koin module for app storage (DataStore)
val storeModule = module {
    singleOf(::TokenDataStore)
}

val databaseModule = module {

    single {
        Room.databaseBuilder(
            get(),
            SessionDatabase::class.java,
            "scoreplay.db"
        ).build()
    }

    single<SessionDao> {
        get<SessionDatabase>().dao
    }

}