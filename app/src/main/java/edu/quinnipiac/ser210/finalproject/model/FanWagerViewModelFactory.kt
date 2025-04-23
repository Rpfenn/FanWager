package edu.quinnipiac.ser210.finalproject.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.quinnipiac.ser210.finalproject.FanWagerViewModel
import edu.quinnipiac.ser210.finalproject.data.AppDatabase
import edu.quinnipiac.ser210.finalproject.data.FanWagerRepository

class FanWagerViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = FanWagerRepository(db)
        if (modelClass.isAssignableFrom(FanWagerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FanWagerViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}