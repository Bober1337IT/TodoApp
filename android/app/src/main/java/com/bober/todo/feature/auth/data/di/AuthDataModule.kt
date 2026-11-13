package com.bober.todo.feature.auth.data.di

import com.bober.todo.feature.auth.data.AuthRepository
import com.bober.todo.feature.auth.data.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl,
    ): AuthRepository
}
