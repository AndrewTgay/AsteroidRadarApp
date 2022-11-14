package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.gson.JsonParser
import com.udacity.asteroidradar.DatabasePictureOfDay
import com.udacity.asteroidradar.PictureOfDay
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

    var asteroids: MutableLiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroid()) {
            it.asDomainModel()
        } as MutableLiveData<List<Asteroid>>


    fun getWeekAsteroids() {
        asteroids = Transformations.map(
            database.asteroidDao.getAsteroidsFromThisWeek(
                LocalDate.now().toString(), LocalDate.now().plusDays(7).toString()
            )
        )
        {
            it.asDomainModel()
        } as MutableLiveData<List<Asteroid>>
    }

    fun getSavedAsteroids() {
        asteroids = Transformations.map(database.asteroidDao.getAsteroid()) {
            it.asDomainModel()
        } as MutableLiveData<List<Asteroid>>
    }

    fun getTodayAsteroids() {
        asteroids =
            Transformations.map(database.asteroidDao.getAsteroidToday(LocalDate.now().toString())) {
                it.asDomainModel()
            } as MutableLiveData<List<Asteroid>>
    }

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
}