package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofitForPictureOfDay = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()

interface PictureOfDayApiService {
    @GET("planetary/apod?&api_key=${Constants.API_KEY}")
    suspend fun getPictureOfDay(): PictureOfDay
}

object PictureOfDayApi {
    val retrofitService: PictureOfDayApiService by lazy {
        retrofitForPictureOfDay.create(PictureOfDayApiService::class.java)
    }
}
////////////////////////////////////////////////
private val retrofitForAsteroids = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .build()

interface AsteroidsApiService {
    @GET("neo/rest/v1/feed?&api_key=${Constants.API_KEY}")
    suspend fun getAsteroids(
    ): String
}

object AsteroidsApi {
    val retrofitService: AsteroidsApiService by lazy {
        retrofitForAsteroids.create(
            AsteroidsApiService::class.java
        )
    }
}
