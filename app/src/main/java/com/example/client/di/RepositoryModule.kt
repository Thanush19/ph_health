package com.example.client.di

import com.example.client.data.api.AuthApiService
import com.example.client.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: AuthApiService): AuthRepository {
        return AuthRepository(apiService)
    }
}
