package com.example.maquinariasapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.maquinariasapp.data.RowData
import com.example.maquinariasapp.ui.linProduccion.LineaProduccionViewModel
import com.example.maquinariasapp.ui.maeMaquinaria.MaestroMaquinariaViewModel


class LineaProdFragment : Fragment() {

    lateinit var viewModel: LineaProduccionViewModel
    private var operationType: Int = 0

    lateinit var editLCod: EditText
    lateinit var txtLCod: TextView

    lateinit var btnLGuardar: Button
    lateinit var btnLEliminar: Button

    lateinit var cod: EditText
    lateinit var nom: EditText
    lateinit var checkL: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            operationType = it.getInt("operationType", 0) // Por defecto, 0 si no se pasa nada
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[LineaProduccionViewModel::class.java]
        return inflater.inflate(R.layout.fragment_linea_prod, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observa el RowData seleccionado
        cod = view.findViewById(R.id.editLCod)
        Log.d("Fragment", "Actividad asociada: ${requireActivity()}")
        Log.d("EditFragment", "Antes de viewModel ${viewModel}")

        viewModel.selectedRowData.observe(viewLifecycleOwner) { rowData ->
            Log.d("EditFragment", "RowData recibido: $rowData")

            if (rowData != null) {
                Log.d("EditFragment", "RowData para editar: ${rowData.cells}")
                Log.d("EditFragment", "Tipo de operación: $operationType")
                // Configura tus vistas según el tipo de operación
                cod.setText(rowData.cells[0])
                nom.setText(rowData.cells[1])
                checkL.isChecked = rowData.cells[2] == "A"
            } else {
                Log.d("EditFragment", "selectedRowData es null")
            }
        }
        btnLGuardar = view.findViewById(R.id.btnLGuardar)
        btnLEliminar = view.findViewById(R.id.btnLEliminar)


        setUpButtonGuardar(view)
        if (operationType == 1)
            setUpButtonEliminar(view)
        if (operationType == 1) {
            editLCod = view.findViewById(R.id.editLCod)
            editLCod.isEnabled = false
            editLCod.isFocusable = false
        }
        else if (operationType == 2) { //operacion guardar el boton eliminar se desactiva
            editLCod = view.findViewById(R.id.editLCod)
            txtLCod = view.findViewById(R.id.txtLCod)
            btnLEliminar.isEnabled = false
            btnLEliminar.alpha = 0.5f
            checkL.isChecked = true
            //editLCod.isEnabled = false
            //editLCod.visibility = View.GONE
            //txtLCod.visibility = View.GONE
        }
    }

    private fun setUpButtonGuardar(view: View) {
        nom = view.findViewById(R.id.editLNom)
        checkL = view.findViewById(R.id.checkL)

        btnLGuardar.setOnClickListener {
            val nombre = nom.text.toString()
            val estadoRegistro = if (checkL.isChecked) "A" else "I"

            if (operationType == 1) {
                Log.d("EN SETUPBUTTON GUARDAR", "La operacion es 1, antes de funcion")
                viewModel.updateSelectedRowDataWithoutCode(
                    nombre,
                    estadoRegistro
                )
                Log.d("EN SETUPBUTTON GUARDAR", "La operacion es 1, luego de funcion")
                requireActivity().supportFragmentManager.popBackStack()
            }
            else if (operationType == 2) {
                //enviar a la base de datos
                cod = view.findViewById(R.id.editLCod)
                val codigo = cod.text.toString()

                val newData = RowData(
                    cells = mutableListOf(
                        codigo,
                        nombre,
                        "A"
                    )
                )
                viewModel.addRowData(newData)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

    }

    private fun setUpButtonEliminar(view: View) {
        btnLEliminar.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que deseas eliminar este elemento?")
                .setPositiveButton("Continuar") { dialog, _ ->
                    // Acción al presionar "Continuar"
                    viewModel.eliminarElementoSeleccionado()
                    dialog.dismiss() // Cierra el diálogo
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    // Acción al presionar "Cancelar"
                    dialog.dismiss() // Solo cierra el diálogo
                }
                .create()

            alertDialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpia el RowData al retroceder
        viewModel.clearSelectedRowData()
    }
}