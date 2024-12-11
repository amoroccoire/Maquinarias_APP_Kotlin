package com.example.maquinariasapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.maquinariasapp.data.RowData

open class BaseViewModel : ViewModel() {
    //encabezados
    private val _headers = MutableLiveData<MutableList<String>>()
    val headers: LiveData<MutableList<String>> get() = _headers

    //columnas visibles
    private val _visibleColumns = MutableLiveData<MutableList<Boolean>>()
    val visibleColumns: LiveData<MutableList<Boolean>> get() = _visibleColumns

    // Datos de la tabla
    val _data = MutableLiveData<List<RowData>?>()
    val data: MutableLiveData<List<RowData>?> get() = _data

    //para el filtro de datos
    private val _filteredData = MutableLiveData<List<RowData>>()
    val filteredData: LiveData<List<RowData>> get() = _filteredData

    init {
        _headers.value = mutableListOf()
        _visibleColumns.value = mutableListOf()
        _data.value = emptyList()
        _filteredData.value = emptyList()
    }

    fun initializeData(headers: MutableList<String>, visibleColumns: MutableList<Boolean>, data: List<RowData>) {
        _headers.value = headers
        _visibleColumns.value = visibleColumns
        _data.value = data
        _filteredData.value = data // Inicialmente, filteredData es igual a data
    }

    fun filterData(columnIndex: Int, query: String) {
        if (query.isEmpty()) {
            _filteredData.value = _data.value // Restablece al conjunto completo
        } else {
            _filteredData.value = _data.value?.filter { row ->
                row.cells[columnIndex].lowercase().contains(query)
            }
        }
    }

    fun updateVisibleColumn(index: Int, isVisible: Boolean) {
        _visibleColumns.value?.set(index, isVisible)
        _visibleColumns.value = _visibleColumns.value // Notifica los observadores

    }

    fun swapColumns(index1: Int, index2: Int) {
        val headers = _headers.value
        val columns = _visibleColumns.value
        val rows = _data.value

        headers?.let {
            val tempHeader = it[index1]
            it[index1] = it[index2]
            it[index2] = tempHeader
        }

        columns?.let {
            it[index1] = true
            it[index2] = false
        }

        rows?.let {
            val updatedRows = it.map { row ->
                val cells = row.cells.toMutableList()
                val tempCell = cells[index1]
                cells[index1] = cells[index2]
                cells[index2] = tempCell
                RowData(cells)
            }
            _data.value = updatedRows
            _filteredData.value = updatedRows //sincronizar filteredData
        }
    }
}