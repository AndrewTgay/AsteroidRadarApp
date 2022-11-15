package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.gson.JsonParser
import com.udacity.asteroidradar.DatabasePictureOfDay
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.getPictureOfDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate

//this will fetching asteroid from the network to the local database
class AsteroidRepository(private val database: AsteroidDatabase) {

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val response = Network.asteroidNetwork.getAsteroidList()
                if (response.isSuccessful) {
                    response.body()?.let {
                        val gson = JsonParser().parse(it).asJsonObject
                        val jo2 = JSONObject(gson.toString())
                        val asteroids = parseAsteroidsJsonResult(jo2)
                        database.asteroidDao.insertAllAsteroid(asteroids)
                    }
                } else {

                }
                Log.i("Looooog", "finish try")
            } catch (e: Exception) {
                Log.i("Loooooog", "my exception in asteroid is ${e.message}")
            }
        }

    }
    suspend fun refreshPictureOfDay() {
        val picture = getPictureOfDay()
        withContext(Dispatchers.IO) {
            if (picture != null) {
                database.pictureOfDayDao.insert(
                    DatabasePictureOfDay(
                        picture.mediaType,
                        picture.title,
                        picture.url
                    )
                )
            }
        }
    }
}