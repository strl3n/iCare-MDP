package com.istts.finalproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istts.finalproject.data.local.entity.MoodEntity
import com.istts.finalproject.data.remote.repository.MoodRepository
import com.istts.finalproject.data.remote.repository.MoodStats
import com.istts.finalproject.utils.Resource
import kotlinx.coroutines.launch

class MoodViewModel(
    private val moodRepository: MoodRepository
) : ViewModel() {

    private val _saveMoodResult = MutableLiveData<Resource<MoodEntity>>()
    val saveMoodResult: LiveData<Resource<MoodEntity>> = _saveMoodResult

    private val _moodHistory = MutableLiveData<Resource<List<MoodEntity>>>()
    val moodHistory: LiveData<Resource<List<MoodEntity>>> = _moodHistory

    private val _latestMood = MutableLiveData<Resource<MoodEntity?>>()
    val latestMood: LiveData<Resource<MoodEntity?>> = _latestMood

    private val _weeklyAverage = MutableLiveData<Resource<Double?>>()
    val weeklyAverage: LiveData<Resource<Double?>> = _weeklyAverage

    private val _moodStats = MutableLiveData<Resource<MoodStats>>()
    val moodStats: LiveData<Resource<MoodStats>> = _moodStats

    private val _clearMoodResult = MutableLiveData<Resource<Unit>>()
    val clearMoodResult: LiveData<Resource<Unit>> = _clearMoodResult

    fun saveMood(token: String, userId: Int, moodLevel: Int, note: String?) {
        viewModelScope.launch {
            _saveMoodResult.value = Resource.Loading()
            val result = moodRepository.saveMood(token, userId, moodLevel, note)
            _saveMoodResult.value = result
            // Refresh data
            if (result is Resource.Success) {
                getMoodHistory(userId)
                getLatestMood(userId)
                getWeeklyAverage(userId)
                getMoodStats(userId)
            }
        }
    }

    fun getMoodHistory(userId: Int) {
        viewModelScope.launch {
            _moodHistory.value = Resource.Loading()
            val result = moodRepository.getMoodHistory(userId)
            _moodHistory.value = result
        }
    }

    fun getLatestMood(userId: Int) {
        viewModelScope.launch {
            val result = moodRepository.getLatestMood(userId)
            _latestMood.value = result
        }
    }

    fun getWeeklyAverage(userId: Int) {
        viewModelScope.launch {
            val result = moodRepository.getWeeklyAverage(userId)
            _weeklyAverage.value = result
        }
    }

    fun getMoodStats(userId: Int) {
        viewModelScope.launch {
            val result = moodRepository.getMoodStats(userId)
            _moodStats.value = result
        }
    }

    fun deleteMood(moodId: Int, userId: Int) {
        viewModelScope.launch {
            moodRepository.deleteMood(moodId)
            getMoodHistory(userId)
            getMoodStats(userId)
        }
    }

    fun clearUserMoods(userId: Int) {
        viewModelScope.launch {
            _clearMoodResult.value = Resource.Loading()
            val result = moodRepository.clearUserMoods(userId)
            _clearMoodResult.value = result
        }
    }
}