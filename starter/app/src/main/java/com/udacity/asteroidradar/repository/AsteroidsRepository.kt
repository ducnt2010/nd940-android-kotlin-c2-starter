package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidsApi
import com.udacity.asteroidradar.api.NetworkUtils
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    private val _today = getDateFormat(0)
    private val _endDateDay = getDateFormat(Constants.DEFAULT_END_DATE_DAYS)


    val savedAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it.asDomainModel()
        }

    val asteroidsByWeek: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsByFilter(_today, _endDateDay)) {
            it.asDomainModel()
        }

    val asteroidsOfDay: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsOfDay(_today)) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val response = AsteroidsApi.retrofitService.getAsteroids()
                val asteroidsList = NetworkUtils.parseAsteroidsJsonResult(JSONObject(response))
                database.asteroidDao.insertAll(*asteroidsList.asDatabaseModel())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun deleteAsteroidDataBeforeToday(){
        withContext(Dispatchers.IO) {
            database.asteroidDao.deleteAsteroidsBeforeToday(_today)
        }
    }

    private fun getDateFormat(gapFromToday: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, gapFromToday)

        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }
}
