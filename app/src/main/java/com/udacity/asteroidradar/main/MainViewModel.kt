package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.os.Build
import android.support.v4.os.IResultReceiver
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.DatabasePictureOfDay
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.getPictureOfDay
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

enum class AsteroidStatus { LOADING, DONE, ERROR }

//@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application)
    private val repository = AsteroidRepository(database)

    private var _pictureOfTheDay = MutableLiveData<PictureOfDay?>()
    val pictureOfTheDay: LiveData<PictureOfDay?>
        get() = _pictureOfTheDay


    private var _asteroidList = MutableLiveData<List<Asteroid>>()
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList


    private val _natigateToAsteroidDetails = MutableLiveData<Asteroid?>()
    val natigateToAsteroidDetails: LiveData<Asteroid?>
        get() = _natigateToAsteroidDetails

    fun menuItemSeletcet(choose: Int) {
        when (choose) {
            0 -> repository.getWeekAsteroids()
            1 -> repository.getTodayAsteroids()
            else -> repository.getSavedAsteroids()
        }
        _asteroidList = repository.asteroids as MutableLiveData<List<Asteroid>>
    }

    suspend fun getThePictureOfTheDay() {
        try {
            withContext(Dispatchers.IO) {
                listOfPicturesOfTheDay = database.pictureOfDayDao.getPicture()
            }
            if (listOfPicturesOfTheDay.isNotEmpty()) {
                val pictureOfTheDayTemp =
                    listOfPicturesOfTheDay.get(listOfPicturesOfTheDay.size - 1)
                _pictureOfTheDay.value = PictureOfDay(
                    pictureOfTheDayTemp.mediaType,
                    pictureOfTheDayTemp.title,
                    pictureOfTheDayTemp.url
                )
            } else
                _pictureOfTheDay.value = null
        } catch (e:Exception) {

        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _natigateToAsteroidDetails.value = asteroid
    }

    fun onAsteroidClicked() {
        _natigateToAsteroidDetails.value = null
    }

    private lateinit var listOfPicturesOfTheDay: List<DatabasePictureOfDay>

    init {

        viewModelScope.launch {
            _asteroidList = repository.asteroids
            getThePictureOfTheDay()
            refreshingg()
        }
    }


    private suspend fun refreshingg() {
        try {
            refreshPictureOfDay()
            repository.refreshAsteroids()
        }catch (e:Exception){

        }
    }

    private suspend fun refreshPictureOfDay() {
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
        _pictureOfTheDay.value = picture
    }

}