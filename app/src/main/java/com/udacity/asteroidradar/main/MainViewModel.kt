package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.os.Build
import android.support.v4.os.IResultReceiver
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.udacity.asteroidradar.DatabasePictureOfDay
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.getPictureOfDay
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
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
        viewModelScope.launch {
            when (choose) {
                0 -> getWeekAsteroids()
                1 -> getTodayAsteroids()
                else -> getSavedAsteroids()
            }
        }
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
        getWeekAsteroids()
        viewModelScope.launch {
            getThePictureOfTheDay()
            refreshing()
        }
    }


    private suspend fun refreshing() {
        try {
            repository.refreshPictureOfDay()
            repository.refreshAsteroids()
        }catch (e:Exception){

        }
    }

    fun getWeekAsteroids() {
        viewModelScope.launch {
        database.asteroidDao.getAsteroidsFromThisWeek(
                LocalDate.now().toString(), LocalDate.now().plusDays(7).toString()
            ).collect() {
            _asteroidList.value = it
        }
        }
    }

    fun getSavedAsteroids() {
        viewModelScope.launch {
            database.asteroidDao.getAsteroid(LocalDate.now().toString())
                .collect() {
                _asteroidList.value = it
            }
        }
    }

    fun getTodayAsteroids() {
        viewModelScope.launch {
            database.asteroidDao.getAsteroidToday(
                LocalDate.now().toString()
            ).collect() {
                _asteroidList.value = it
            }
        }
    }

}