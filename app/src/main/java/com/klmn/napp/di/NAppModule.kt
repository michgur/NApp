package com.klmn.napp.di

import android.content.Context
import com.klmn.napp.R
import com.klmn.napp.data.Repository
import com.klmn.napp.data.RepositoryImpl
import com.klmn.napp.data.network.OpenFoodFactsAPI
import com.klmn.napp.data.network.PixabayAPI
import com.klmn.napp.data.network.entities.ProductEntity
import com.klmn.napp.data.network.entities.ProductNetworkEntityMapper
import com.klmn.napp.model.Product
import com.klmn.slapp.common.EntityModelMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NAppModule {
    private const val OFF_URL = "https://world.openfoodfacts.org/"
    const val PIXABAY_URL = "https://pixabay.com/api/"

    @Provides @Singleton
    fun provideOFFAPI(): OpenFoodFactsAPI = Retrofit.Builder()
            .baseUrl(OFF_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsAPI::class.java)

    @Provides @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor { chain ->
                val key = context.resources.getString(R.string.pixabay_api)
                val url = chain.request().url().newBuilder()
                        .addQueryParameter("key", key)
                        .build()
                chain.proceed(
                    chain.request().newBuilder().url(url).build()
                )
            }.build()

    @Provides @Singleton
    fun providePixabayAPI(okHttpClient: OkHttpClient): PixabayAPI = Retrofit.Builder()
            .baseUrl(PIXABAY_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixabayAPI::class.java)

    @Provides @Singleton
    fun provideNetworkEntityMapper(): EntityModelMapper<ProductEntity, Product> = ProductNetworkEntityMapper

    @Provides @Singleton
    fun provideRepository(
            openFoodFactsAPI: OpenFoodFactsAPI,
            networkEntityMapper: EntityModelMapper<ProductEntity, Product>,
            pixabayAPI: PixabayAPI
    ): Repository = RepositoryImpl(openFoodFactsAPI, networkEntityMapper, pixabayAPI)
}