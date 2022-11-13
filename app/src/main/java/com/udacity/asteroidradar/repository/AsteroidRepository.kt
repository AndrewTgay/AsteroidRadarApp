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
class AsteroidRepository (private val database:AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroid()){
        it.asDomainModel()
    }

    val picture : LiveData<PictureOfDay> = database.pictureOfDayDao.getPicture()

    suspend fun refreshAsteroids(){
        withContext(Dispatchers.IO){
            val media = Network.asteroidNetwork2.getPhotoOfTheDay().await()
            if(media.mediaType=="image"){database.pictureOfDayDao.insert(media)}
            val response = Network.asteroidNetwork.getAsteroidList().await()
            val gson = JsonParser().parse(response).asJsonObject
            val jo2 = JSONObject(gson.toString())
            val asteroids = parseAsteroidsJsonResult(jo2)
            database.asteroidDao.insertAllAsteroid(asteroids)
        }
    }

    suspend fun putDumyData(){
        withContext(Dispatchers.IO){
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