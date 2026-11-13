package com.bober.todo

import android.app.Application
import com.bober.todo.feature.auth.data.local.TokenStore
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class TodoApp : Application() {

    @Inject
    lateinit var tokenStore: TokenStore

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            tokenStore.hydrate()
        }
    }
}
