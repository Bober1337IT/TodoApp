package com.bober.todo.feature.task.data.di

import com.bober.todo.feature.task.data.TaskRepository
import com.bober.todo.feature.task.data.TaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TaskDataModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl,
    ): TaskRepository
}
