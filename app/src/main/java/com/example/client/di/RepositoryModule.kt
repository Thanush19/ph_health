package com.example.client.di

import com.example.client.data.api.AuthApiService
import com.example.client.data.api.BookingApiService
import com.example.client.data.api.ChatApiService
import com.example.client.data.api.SpaceApiService
import com.example.client.data.repository.AuthRepository
import com.example.client.data.repository.BookingRepository
import com.example.client.data.repository.ChatRepository
import com.example.client.data.repository.SpaceRepository
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

    @Provides
    @Singleton
    fun provideSpaceRepository(apiService: SpaceApiService): SpaceRepository {
        return SpaceRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(apiService: BookingApiService): BookingRepository {
        return BookingRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideChatRepository(apiService: ChatApiService): ChatRepository {
        return ChatRepository(apiService)
    }
}
