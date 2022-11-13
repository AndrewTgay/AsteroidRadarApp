package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.gson.JsonParser
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.await

//this will fetching asteroid from the network to the local database
class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroid()) {
            it.asDomainModel()
        }

    val pictures: LiveData<List<PictureOfDay>> = database.pictureOfDayDao.getPicture()

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val media = Network.asteroidNetwork2.getPhotoOfTheDay()
                if (media.isSuccessful) {
                    media.body()?.let {
                        if (it.mediaType == "image") {
                            database.pictureOfDayDao.insert(it)
                        }
                    }
                }
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
            }catch (e: Exception) {
                Log.i("Loooooog", "my exception in asteroid is ${e.message}")
            }
            finally {
                try {
                    Log.i("Loooooog", "${pictures.value!!.size}")
                }catch (e:Exception){
                    Log.i("Loooooog", "my exception in load pictures is ${e.message}")
                }
            }
        }
    }

    suspend fun putDumyData() {
        withContext(Dispatchers.IO) {
//            database.asteroidDao.insertAllAsteroid(
//                DatabaseAsteroid(
//                    1, "hi", "1-1-1",
//                    12.5, 13.5,
//                    19.2, 1560.5,
//                    true),
//                DatabaseAsteroid(
//                    2, "hi2", "21-21-21",
//                    12.5, 13.5,
//                    19.2, 1560.5,
//                    true
//                ),
//                DatabaseAsteroid(
//                    3, "hi3", "13-13-13",
//                    12.5, 13.5,
//                    19.2, 1560.5,
//                    false
//                ),DatabaseAsteroid(
//                    4, "hi44", "14-4-14",
//                    12.5, 13.5,
//                    19.2, 1560.5,
//                    false
//                )
//            )
        }
    }
}