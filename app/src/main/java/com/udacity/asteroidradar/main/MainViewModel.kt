package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import android.support.v4.os.IResultReceiver
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

enum class AsteroidStatus{LOADING , DONE , ERROR}

//@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel(application: Application): AndroidViewModel(application) {

    private val _status = MutableLiveData<AsteroidStatus>()
    val status:LiveData<AsteroidStatus>
        get() = _status

    private var _pictureOfTheDay = MutableLiveData<PictureOfDay>()
    val pictureOfTheDay : LiveData<PictureOfDay>
        get() = _pictureOfTheDay


    private var _asteroidList = MutableLiveData<List<Asteroid>>()
    val asteroidList : LiveData<List<Asteroid>>
        get() = repository.asteroids


    private val _natigateToAsteroidDetails = MutableLiveData<Asteroid?>()
    val natigateToAsteroidDetails: LiveData<Asteroid?>
        get() = _natigateToAsteroidDetails

    fun onAsteroidClicked(asteroid: Asteroid) {
        _natigateToAsteroidDetails.value = asteroid
    }
    fun onAsteroidClicked() {
        _natigateToAsteroidDetails.value = null
    }

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application)
    private val repository = AsteroidRepository(database)
    init {
        viewModelScope.launch {
      //      _pictureOfTheDay = repository.picture as MutableLiveData<PictureOfDay>
            _asteroidList = repository.asteroids as MutableLiveData<List<Asteroid>>
            getAllAsteroidList()
            }
    }

    private suspend fun getAllAsteroidList() {
        repository.refreshAsteroids()
        _asteroidList = repository.asteroids as MutableLiveData<List<Asteroid>>
    }
}