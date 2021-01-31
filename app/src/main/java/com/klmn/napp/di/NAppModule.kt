package com.klmn.napp.di

import com.klmn.napp.data.Repository
import com.klmn.napp.data.RepositoryImpl
import com.klmn.napp.data.network.OFFAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NAppModule {
    val BASE_URL = "https://world.openfoodfacts.org/"

    @Provides @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides @Singleton
    fun provideOFFAPI(retrofit: Retrofit): OFFAPI = retrofit.create(OFFAPI::class.java)

    @Provides @Singleton
    fun provideRepository(api: OFFAPI): Repository = RepositoryImpl(api)
}