package com.example.maquinariasapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maquinariasapp.data.RowData
import com.example.maquinariasapp.ui.BaseViewModel
import com.example.maquinariasapp.ui.TableAdapter

abstract class BaseFragment<VM : BaseViewModel> : Fragment(), NavigationHandler {

    protected lateinit var viewModel: VM
    abstract fun getViewModelClass() : Class<VM>

    /*protected abstract val headers: MutableList<String>
    protected abstract val visibleColumns: MutableList<Boolean>
    protected abstract val data: List<RowData>*/

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TableAdapter
    //private var filteredData: List<RowData> = data
    private var selectedColumnIndex: Int = 0
    private var isClickable: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(requireActivity())[getViewModelClass()]
        setupObservers()

        //adapter = TableAdapter(data, visibleColumns, this)
        //adapter = TableAdapter(emptyList(), emptyList(), this)
        adapter = TableAdapter(
            data = viewModel.filteredData.value ?: emptyList(),
            visibleColumns = viewModel.visibleColumns.value ?: emptyList()
        ) { rowData ->
            navegationWithRow(rowData)
        }
        recyclerView.adapter = adapter

        setupSpinner()
        setupFilterInput()
        setupCheckBox()
    }

    private fun setupObservers() {
        viewModel.filteredData.observe(viewLifecycleOwner) { filteredData ->
            if (!::adapter.isInitialized) {
                adapter = TableAdapter(filteredData, viewModel.visibleColumns.value ?: emptyList()) { rowData ->
                    navegationWithRow(rowData)
                }
                recyclerView.adapter = adapter
            } else {
                adapter.updateData(filteredData)
            }
        }


        // Observa los datos filtrados y actualiza el adaptador
        viewModel.filteredData.observe(viewLifecycleOwner) { filteredData ->
            adapter.updateData(filteredData)
        }

        // Observa las columnas visibles y actualiza el adaptador
        viewModel.visibleColumns.observe(viewLifecycleOwner) { visibleColumns ->
            adapter.updateData(viewModel.filteredData.value ?: emptyList())
        }

        // Observa encabezados y regenera los encabezados en la interfaz
        viewModel.headers.observe(viewLifecycleOwner) {
            generateHeaders()
        }
    }

    protected fun generateHeaders() {
        val headerContainer: LinearLayout = requireView().findViewById(R.id.header_container)
        headerContainer.removeAllViews()
        val headers = viewModel.headers.value ?: return
        val visibleColumns = viewModel.visibleColumns.value ?: return
        visibleColumns.forEach { a -> Log.d("EN GENERATEHEADER", "antes del forEachIndexed ${a}") }

        headers.forEachIndexed { index, headerText ->
            //if (index in visibleColumns.indices && visibleColumns[index])
            if (visibleColumns[index]){
                val headerView = LayoutInflater.from(requireContext()).inflate(
                    R.layout.header_item, headerContainer, false
                )

                val headerTextView = headerView.findViewById<TextView>(R.id.header_text)
                val headerIconView = headerView.findViewById<ImageView>(R.id.header_icon)

                headerTextView.text = headerText
                headerIconView.tag = index // Asigna el índice al ícono
                headerIconView.setOnClickListener { setupHeader(it) }

                headerContainer.addView(headerView)
            }
        }
    }

    protected fun setupHeader(anchor: View) {

        val columnIndex = anchor.tag as Int
        val popupMenu = PopupMenu(requireContext(), anchor)
        popupMenu.menuInflater.inflate(R.menu.column_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_hide -> {
                    Toast.makeText(
                        requireContext(),
                        "Desde LineaProduccionFragment",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.updateVisibleColumn(columnIndex, false)
                    //visibleColumns[columnIndex] = false
                    generateHeaders()
                    adapter.notifyDataSetChanged()
                    true
                }

                R.id.action_add -> {
                    showAddColumnMenu(anchor)
                    true
                }

                /*R.id.action_replace -> {
                    showReplaceColumnMenu(anchor, columnIndex)
                    true
                }*/
                else -> false
            }
        }

        popupMenu.show()
    }

    protected fun showAddColumnMenu(anchor: View) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        val headers = viewModel.headers.value ?: return
        val visibleColumns = viewModel.visibleColumns.value ?: return

        visibleColumns.forEachIndexed { index, isVisible ->
            if (!isVisible) {
                popupMenu.menu.add("+ ${headers[index]}").setOnMenuItemClickListener {
                    viewModel.updateVisibleColumn(index, true)
                    //visibleColumns[index] = true
                    generateHeaders()
                    adapter.notifyDataSetChanged()
                    true
                }
            }
        }

        popupMenu.show()
    }

    protected fun showReplaceColumnMenu(anchor: View, currentColumnIndex: Int) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        val headers = viewModel.headers.value ?: return
        val visibleColumns = viewModel.visibleColumns.value ?: return

        visibleColumns.forEachIndexed { index, isVisible ->
            if (!isVisible) {
                popupMenu.menu.add("<-> ${headers[index]}").setOnMenuItemClickListener {
                    viewModel.swapColumns(currentColumnIndex, index)
                    /*visibleColumns[currentColumnIndex] = true //no entiendo
                    visibleColumns[index] = false //no entiendo

                    val aux = headers[currentColumnIndex]
                    headers[currentColumnIndex] = headers[index]
                    headers[index] = aux

                    data.forEach { row ->
                        val auxData = row.cells[currentColumnIndex]
                        row.cells[currentColumnIndex] = row.cells[index]
                        row.cells[index] = auxData
                    }
                    visibleColumns.forEach { a -> Log.d("HOME_FFFF", "${a}") }
                    headers.forEach { a -> Log.d("HOME_F", "${a}") }*/
                    generateHeaders()
                    adapter.notifyDataSetChanged()
                    true
                }
            }
        }
        popupMenu.show()
    }

    protected fun setupSpinner() {
        val spinner: Spinner = requireView().findViewById(R.id.spinner)
        val columnNames = viewModel.headers.value?.filterIndexed { index, _ ->
            viewModel.visibleColumns.value?.get(index) == true
            //visibleColumns[index]
        } ?: emptyList()

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, columnNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Escuchar cambios en la selección del Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedColumnIndex = columnNames.indexOf(columnNames[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    protected fun setupFilterInput() {
        val filterInput: EditText = requireView().findViewById(R.id.filter_input)
        filterInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                viewModel.filterData(selectedColumnIndex, query)
                /*filteredData = if (query.isNotEmpty()) {
                    data.filter { row ->
                        row.cells[selectedColumnIndex].lowercase().contains(query)
                    }
                } else {
                    data
                }
                adapter.updateData(filteredData) // Actualiza los datos en el adaptador*/
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    protected fun setupCheckBox() {
        val checkBox: CheckBox = requireView().findViewById(R.id.checkOpciones)
        val msj = requireView().findViewById<TextView>(R.id.msjCrud)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            isClickable = isChecked
            adapter.setClickable(isClickable) // Actualiza el estado clickeable en el adaptador
            if (isClickable) {
                msj.visibility = View.VISIBLE
            }
            else {
                msj.visibility = View.INVISIBLE
            }
        }
    }
}