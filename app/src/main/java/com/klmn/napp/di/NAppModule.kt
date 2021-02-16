package com.klmn.napp.di

import android.content.Context
import androidx.room.Room
import com.klmn.napp.R
import com.klmn.napp.data.Repository
import com.klmn.napp.data.RepositoryImpl
import com.klmn.napp.data.cache.Database
import com.klmn.napp.data.network.OpenFoodFactsAPI
import com.klmn.napp.data.network.PixabayAPI
import com.klmn.napp.util.parameterInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NAppModule {
    private const val OPEN_FOOD_FACTS_URL = "https://us.openfoodfacts.org/"
    private const val PIXABAY_URL = "https://pixabay.com/api/"

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database = Room.databaseBuilder(
            context,
            Database::class.java,
            "NAPP_DATABASE"
    ).build()

    @Provides @Singleton
    fun provideOpenFoodFactsAPI(): OpenFoodFactsAPI = Retrofit.Builder()
        .baseUrl(OPEN_FOOD_FACTS_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenFoodFactsAPI::class.java)

    @Provides @Singleton @Named("pixabay")
    fun providePixabayClient(
        @ApplicationContext context: Context
    ): OkHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(parameterInterceptor(
                "key" to context.resources.getString(R.string.pixabay_api)
            )).build()

    @Provides @Singleton
    fun providePixabayAPI(
        @Named("pixabay") okHttpClient: OkHttpClient
    ): PixabayAPI = Retrofit.Builder()
            .baseUrl(PIXABAY_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixabayAPI::class.java)

    @Provides @Singleton
    fun provideRepository(
        @ApplicationContext context: Context,
        database: Database,
        openFoodFactsAPI: OpenFoodFactsAPI,
        pixabayAPI: PixabayAPI
    ): Repository = RepositoryImpl(
        context,
        database.dao(),
        openFoodFactsAPI,
        pixabayAPI
    )
}