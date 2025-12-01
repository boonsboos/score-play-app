package nl.connectplay.scoreplay

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

// This is the main Application class where Koin is initialized with the necessary modules.
class ScorePlayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ScorePlayApp)
            modules(viewModelsModule, apiModule, storeModule)
        }
    }
}