package nl.connectplay.scoreplay

import nl.connectplay.scoreplay.viewModels.ExampleDetailViewModel
import nl.connectplay.scoreplay.viewModels.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


// Koin module to provide ViewModels
val viewModels = module {
    viewModelOf(::ExampleDetailViewModel)
    viewModelOf(::LoginViewModel)
}