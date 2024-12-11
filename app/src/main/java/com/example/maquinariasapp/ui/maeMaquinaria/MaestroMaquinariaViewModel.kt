package com.example.maquinariasapp.ui.maeMaquinaria

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

class MaestroMaquinariaViewModel() : BaseViewModel() {
    private val _selectedRowData = MutableLiveData<RowData?>()
    val selectedRowData: LiveData<RowData?> get() = _selectedRowData
    //private val dao = AppDatabase.getDatabase(context).maestroMaquinariaDao()
    init {
        // Inicializa los datos específicos de este ViewModel
        initializeData(
            headers = mutableListOf(
                "Código",
                "Nombre",
                "Linea\nProducción",
                "Planta\nProducción",
                "Ubicación",
                "Estado\nRegistro"
            ),
            visibleColumns = mutableListOf(true, true, true, true, true, true),
            data = mutableListOf(
                RowData(mutableListOf("MM0001", "Nom1", "LP0001", "PP0001", "Zona A1", "A")),
                RowData(mutableListOf("MM0002", "Nom2", "LP0002", "PP0001", "Zona A2", "A")),
                RowData(mutableListOf("MM0003", "Nom3", "LP0003", "PP0002", "Zona B1", "A")),
                RowData(mutableListOf("MM0004", "Nom4", "LP0007", "PP0003", "Zona B2", "A"))
            )
        )
    }

    fun fetchData() {
        viewModelScope.launch {
            RetrofitClient.apiService.getMaestroMaquinarias().enqueue(object : Callback<List<MaestroMaquinariaResponse>> {
                override fun onResponse(
                    call: Call<List<MaestroMaquinariaResponse>>,
                    response: Response<List<MaestroMaquinariaResponse>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { data ->
                            initializeData(
                                headers = mutableListOf(
                                    "Código",
                                    "Nombre",
                                    "Linea\nProducción",
                                    "Planta\nProducción",
                                    "Ubicación",
                                    "Estado\nRegistro"
                                ),
                                visibleColumns = mutableListOf(true, true, true, true, true, true),
                                data = data.map {
                                    RowData(
                                        mutableListOf(
                                            it.codigo,
                                            it.nombre,
                                            it.lineaDeProduccion.codigo,
                                            it.plantaDeProduccion.codigo,
                                            it.ubicacion,
                                            it.estadoDeRegistro
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<List<MaestroMaquinariaResponse>>, t: Throwable) {
                    // Manejo de errores
                    Log.e("MaestroMaquinariaVM", "Error al obtener datos: ${t.message}")
                }
            })
        }
        /*viewModelScope.launch {
            initializeData(
                headers = mutableListOf(
                    "Código",
                    "Nombre",
                    "Linea\nProducción",
                    "Planta\nProducción",
                    "Ubicación",
                    "Estado\nRegistro"
                ),
                visibleColumns = mutableListOf(true, true, true, true, true, true),
                data = dao.getAll().map {
                    RowData(
                        mutableListOf(
                            it.codigo,
                            it.nombre,
                            it.lineaDeProduccionCodigo,
                            it.plantaDeProduccionCodigo,
                            it.ubicacion,
                            it.estadoDeRegistro
                        )
                    )
                }
            )
        }*/
    }

    //Metodo para seleccionar un RowData
    fun selectRowData(rowData: RowData) {
        _selectedRowData.value = rowData
        Log.d("VIEW MODEL MAQUINARIA", "RowData seleccionado: ${rowData.cells}")
    }

    //Metodo para limpiar el RowData seleccionado
    fun clearSelectedRowData() {
        _selectedRowData.value = null
    }

    fun updateSelectedRowDataWithoutCode (
        nombre: String,
        lineaProduccion: String,
        plantaProduccion: String,
        ubicacion: String,
        estadoRegistro: String
    ) {
        _selectedRowData.value?.let { selectedRow ->
            // Actualiza únicamente las celdas necesarias (excepto el código)
            selectedRow.cells[1] = nombre
            selectedRow.cells[2] = lineaProduccion
            selectedRow.cells[3] = plantaProduccion
            selectedRow.cells[4] = ubicacion
            selectedRow.cells[5] = estadoRegistro

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

    /*fun saveNewMaquinaria(
        nombre: String,
        ubicacion: String,
        estadoDeRegistro: String,
        lineaDeProduccionCodigo: String,
        plantaDeProduccionCodigo: String
    ) {
        viewModelScope.launch {
            val newCodigo = generateNextCodigo("MM") { dao.getLastCodigo() }
            val nuevaMaquinaria = MaestroMaquinariaEntity(
                codigo = newCodigo,
                nombre = nombre,
                ubicacion = ubicacion,
                estadoDeRegistro = estadoDeRegistro,
                lineaDeProduccionCodigo = lineaDeProduccionCodigo,
                plantaDeProduccionCodigo = plantaDeProduccionCodigo
            )
            dao.insert(nuevaMaquinaria)
        }
    }*/
}