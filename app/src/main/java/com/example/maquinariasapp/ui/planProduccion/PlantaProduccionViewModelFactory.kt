package com.example.maquinariasapp.ui.planProduccion

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlantaProduccionViewModelFactory(){

}

/*class PlantaProduccionViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantaProduccionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantaProduccionViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}*/