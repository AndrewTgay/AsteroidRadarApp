package com.udacity.asteroidradar.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate


interface AsteroidApiService{
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidList(
    @Query("start_date") start_date: String = LocalDate.now().toString(),
    @Query("end_date") end_date: String = LocalDate.now().plusDays(7).toString(),
    @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY ):
            Response<String>

    @GET("planetary/apod")
    fun getPhotoOfTheDay(
        @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY )
            : Deferred<PictureOfDay>

}


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Network {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val asteroidNetwork = retrofit.create(AsteroidApiService::class.java)

}
