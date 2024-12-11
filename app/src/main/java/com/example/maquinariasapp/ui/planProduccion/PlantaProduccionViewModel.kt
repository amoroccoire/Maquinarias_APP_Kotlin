package com.example.maquinariasapp.ui.planProduccion

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.maquinariasapp.data.RowData
import com.example.maquinariasapp.ui.BaseViewModel

import androidx.lifecycle.viewModelScope
import com.example.maquinariasapp.data.MaestroMaquinariaResponse
import com.example.maquinariasapp.data.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlantaProduccionViewModel() : BaseViewModel() {
    private val _selectedRowData = MutableLiveData<RowData?>()
    val selectedRowData: LiveData<RowData?> get() = _selectedRowData
    //private val dao = AppDatabase.getDatabase(context).plantaProduccionDao()
    init {
        // Inicializa los datos específicos de este ViewModel
        initializeData(
            headers = mutableListOf(
                "Código",
                "Nombre",
                "Estado\nRegistro"
            ),
            visibleColumns = mutableListOf(true, true, true),
            data = mutableListOf(
                RowData(mutableListOf("PP0001", "Lavado", "A")),
                RowData(mutableListOf("PP0002", "Tejido", "A")),
                RowData(mutableListOf("PP0003", "Planchado", "A")),
                RowData(mutableListOf("PP0004", "Tintoreria", "A"))
            )
        )
    }

    fun fetchData() {
        /*viewModelScope.launch {
            val plantas = dao.getAll()
            initializeData(
                headers = mutableListOf("Código", "Nombre", "Estado\nRegistro"),
                visibleColumns = mutableListOf(true, true, true),
                data = plantas.map {
                    RowData(mutableListOf(it.codigo, it.nombre, it.estadoDeRegistro))
                }
            )
        }*/
        viewModelScope.launch {
            RetrofitClient.apiService.getMaestroMaquinarias().enqueue(object : Callback<List<MaestroMaquinariaResponse>> {
                override fun onResponse(
                    call: Call<List<MaestroMaquinariaResponse>>,
                    response: Response<List<MaestroMaquinariaResponse>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { data ->
                            initializeData(
                                headers = mutableListOf("Código", "Nombre", "Estado\nRegistro"),
                                visibleColumns = mutableListOf(true, true, true),
                                data = data.map {
                                    RowData(
                                        mutableListOf(
                                            it.plantaDeProduccion.codigo,
                                            it.plantaDeProduccion.nombre,
                                            it.plantaDeProduccion.estadoDeRegistro
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<List<MaestroMaquinariaResponse>>, t: Throwable) {
                    Log.e("PlantaProduccionVM", "Error al obtener datos: ${t.message}")
                }
            })
        }
    }

    //Metodo para seleccionar un RowData
    fun selectRowData(rowData: RowData) {
        _selectedRowData.value = rowData
        Log.d("VIEW MODEL LINEA DE PRODUCCION", "RowData seleccionado: ${rowData.cells}")
    }

    //Metodo para limpiar el RowData seleccionado
    fun clearSelectedRowData() {
        _selectedRowData.value = null
    }

    fun updateSelectedRowDataWithoutCode (
        nombre: String,
        estadoRegistro: String
    ) {
        _selectedRowData.value?.let { selectedRow ->
            // Actualiza únicamente las celdas necesarias (excepto el código)
            selectedRow.cells[1] = nombre
            selectedRow.cells[2] = estadoRegistro

            // Notifica el cambio
            Log.d("VIEW MODEL MAQUINARIA", "RowData actualizado: ${selectedRow.cells}")
            _selectedRowData.value = selectedRow
        }
    }

    fun eliminarElementoSeleccionado() {
        val rowToRemove = _selectedRowData.value
        val currentData = _data.value as? MutableList<RowData>
        if (rowToRemove != null && currentData != null) {
            currentData.remove(rowToRemove) // Elimina el elemento de la lista
            _data.value = currentData // Notifica cambios a los observadores
        }
        clearSelectedRowData() // Limpia la selección
    }

    fun addRowData(newData: RowData) {
        // Obtén la lista actual
        val currentData = _data.value?.toMutableList() ?: mutableListOf()

        // Agrega el nuevo elemento
        currentData.add(newData)

        // Actualiza el LiveData con la nueva lista
        _data.value = currentData
    }

    /*fun saveNewPlantaProduccion(nombre: String, estadoDeRegistro: String) {
        viewModelScope.launch {
            val newCodigo = generateNextCodigo("PP") { dao.getLastCodigo() }
            val nuevaPlanta = PlantaProduccionEntity(
                codigo = newCodigo,
                nombre = nombre,
                estadoDeRegistro = estadoDeRegistro
            )
            dao.insert(nuevaPlanta)
        }
    }*/
}