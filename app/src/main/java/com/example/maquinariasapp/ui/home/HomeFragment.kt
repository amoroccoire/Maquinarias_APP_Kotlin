package com.example.maquinariasapp.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maquinariasapp.R
import com.example.maquinariasapp.data.RowData
import com.example.maquinariasapp.databinding.FragmentHomeBinding
import com.example.maquinariasapp.ui.TableAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TableAdapter

    //controla la visibilidad de las columnas
    private val headers = mutableListOf<String>("Código", "Nombre", "Linea\nProducción", "Planta\nProducción", "Ubicación", "Estado\nRegistro")
    private val visibleColumns = mutableListOf(true, true, true, true, true, true)

    private val data = listOf(
        RowData(mutableListOf("Cod1", "Nom1", "LinPro1", "PlaPro1", "Ubi1", "EstReg1")),
        RowData(mutableListOf("Cod2", "Nom2", "LinPro2", "PlaPro2", "Ubi2", "EstReg2"))
    )

    private var filteredData: List<RowData> = data
    private var selectedColumnIndex: Int = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textHome
        /*homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TableAdapter(data, visibleColumns)
        recyclerView.adapter = adapter

        generateHeaders()
        setupSpinner()
        setupFilterInput()
    }

    fun generateHeaders() {
        val headerContainer: LinearLayout = requireView().findViewById(R.id.header_container)
        headerContainer.removeAllViews()
        headers.forEachIndexed { index, headerText ->
            if (index in visibleColumns.indices && visibleColumns[index]) {
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

    private fun setupHeader(anchor: View) {
        Toast.makeText(requireContext(), "EN SETUP HEADER", Toast.LENGTH_LONG).show()
        val columnIndex = anchor.tag as Int
        val popupMenu = PopupMenu(requireContext(), anchor)
        popupMenu.menuInflater.inflate(R.menu.column_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_hide -> {
                    visibleColumns[columnIndex] = false
                    generateHeaders()
                    adapter.notifyDataSetChanged()
                    true
                }

                R.id.action_add -> {
                    showAddColumnMenu(anchor)
                    true
                }

                R.id.action_replace -> {
                    showReplaceColumnMenu(anchor, columnIndex)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showAddColumnMenu(anchor: View) {
        val popupMenu = PopupMenu(requireContext(), anchor)

        visibleColumns.forEachIndexed { index, isVisible ->
            if (!isVisible) {
                popupMenu.menu.add("+ ${headers[index]}").setOnMenuItemClickListener {
                    visibleColumns[index] = true
                    generateHeaders()
                    adapter.notifyDataSetChanged()
                    true
                }
            }
        }

        popupMenu.show()
    }

    private fun showReplaceColumnMenu(anchor: View, currentColumnIndex: Int) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        visibleColumns.forEachIndexed { index, isVisible ->
            if (!isVisible) {
                popupMenu.menu.add("<-> ${headers[index]}").setOnMenuItemClickListener {
                    visibleColumns[currentColumnIndex] = true //no entiendo
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
                    headers.forEach { a -> Log.d("HOME_F", "${a}") }
                    generateHeaders()
                    adapter.notifyDataSetChanged()
                    true
                }
            }
        }
        popupMenu.show()
    }

    private fun setupSpinner() {
        val spinner: Spinner = requireView().findViewById(R.id.spinner)
        val columnNames = headers.filterIndexed { index, _ -> visibleColumns[index] }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, columnNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Escuchar cambios en la selección del Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedColumnIndex = headers.indexOf(columnNames[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupFilterInput() {
        val filterInput: EditText = requireView().findViewById(R.id.filter_input)
        filterInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                filteredData = if (query.isNotEmpty()) {
                    data.filter { row ->
                        row.cells[selectedColumnIndex].lowercase().contains(query)
                    }
                } else {
                    data
                }
                adapter.updateData(filteredData) // Actualiza los datos en el adaptador
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}