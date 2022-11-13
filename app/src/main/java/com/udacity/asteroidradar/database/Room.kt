package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.domain.Asteroid

@Dao
interface AsteroidDao{
    @Query("SELECT * FROM asteroid_table")
    fun getAsteroid():LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    fun insertAllAsteroid(asteroid: List<DatabaseAsteroid>)

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate >= :startDay AND closeApproachDate <= :endDay ORDER BY closeApproachDate")
     fun getAsteroidsFromThisWeek(startDay: String, endDay: String): List<DatabaseAsteroid>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate = :today ")
     fun getAsteroidToday(today: String): List<DatabaseAsteroid>

    @Query("DELETE FROM asteroid_table")
     fun clear()
}

@Dao
interface PictureDao {

    @Query("DELETE FROM picture_table")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(pictureOfDay: PictureOfDay)

    @Query("SELECT * FROM picture_table")
     fun getPicture(): LiveData<PictureOfDay>

}

@Database(entities = [PictureOfDay::class , DatabaseAsteroid::class], version = 2, exportSchema = false)
abstract class AsteroidDatabase:RoomDatabase(){
    abstract val asteroidDao:AsteroidDao
    abstract val pictureOfDayDao: PictureDao
}

lateinit var INSTANCE:AsteroidDatabase

fun getDatabase(context: Context):AsteroidDatabase{
    synchronized(AsteroidDatabase::class.java){
        if(!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidDatabase::class.java,
                "asteroid_cash_database"
            ).fallbackToDestructiveMigration()
                .build()
        }}
    return INSTANCE
}

