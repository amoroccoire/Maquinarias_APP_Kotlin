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
import com.example.maquinariasapp.ui.maeMaquinaria.MaestroMaquinariaViewModel
import org.w3c.dom.Text

class MaquinariaFragment : Fragment() {
    lateinit var viewModel: MaestroMaquinariaViewModel
    private var operationType: Int = 0

    lateinit var editMCod: EditText
    lateinit var txtMCod: TextView

    lateinit var btnMGuardar: Button
    lateinit var btnMEliminar: Button

    lateinit var cod: EditText
    lateinit var nom: EditText
    lateinit var linPro: EditText
    lateinit var plaPro: EditText
    lateinit var ubi: EditText
    lateinit var checkM: CheckBox


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
        viewModel = ViewModelProvider(requireActivity())[MaestroMaquinariaViewModel::class.java]
        return inflater.inflate(R.layout.fragment_maquinaria, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observa el RowData seleccionado
        cod = view.findViewById(R.id.editMCod)
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
                linPro.setText(rowData.cells[2])
                plaPro.setText(rowData.cells[3])
                ubi.setText(rowData.cells[4])
                checkM.isChecked = rowData.cells[5] == "A"
            } else {
                Log.d("EditFragment", "selectedRowData es null")
            }
        }
        btnMGuardar = view.findViewById(R.id.btnMGuardar)
        btnMEliminar = view.findViewById(R.id.btnMEliminar)


        setUpButtonGuardar(view)
        if (operationType == 1)
            setUpButtonEliminar(view)
        if (operationType == 1) {
            editMCod = view.findViewById(R.id.editMCod)
            editMCod.isEnabled = false
            editMCod.isFocusable = false
        }
        else if (operationType == 2) { //operacion guardar el boton eliminar se desactiva
            editMCod = view.findViewById(R.id.editMCod)
            txtMCod = view.findViewById(R.id.txtMCod)
            btnMEliminar.isEnabled = false
            btnMEliminar.alpha = 0.5f
            checkM.isChecked = true
            //editMCod.isEnabled = false
            //editMCod.visibility = View.GONE
            //txtMCod.visibility = View.GONE
        }
    }

    private fun setUpButtonGuardar(view: View) {
        nom = view.findViewById(R.id.editMNom)
        linPro = view.findViewById(R.id.editMLinPro)
        plaPro = view.findViewById(R.id.editMPlaPro)
        ubi = view.findViewById(R.id.editMUbi)
        checkM = view.findViewById(R.id.checkM)

        btnMGuardar.setOnClickListener {
            val nombre = nom.text.toString()
            val lineaProduccion = linPro.text.toString()
            val plantaProduccion = plaPro.text.toString()
            val ubicacion = ubi.text.toString()
            val estadoRegistro = if (checkM.isChecked) "A" else "I"

            if (operationType == 1) {
                Log.d("EN SETUPBUTTON GUARDAR", "La operacion es 1, antes de funcion")
                viewModel.updateSelectedRowDataWithoutCode(
                    nombre,
                    lineaProduccion,
                    plantaProduccion,
                    ubicacion,
                    estadoRegistro
                )
                Log.d("EN SETUPBUTTON GUARDAR", "La operacion es 1, luego de funcion")
                requireActivity().supportFragmentManager.popBackStack()
            }
            else if (operationType == 2) {
                //enviar a la base de datos
                cod = view.findViewById(R.id.editMCod)
                val codigo = cod.text.toString()

                val newData = RowData(
                    cells = mutableListOf(
                        codigo,
                        nombre,
                        lineaProduccion,
                        plantaProduccion,
                        ubicacion,
                        "A"
                    )
                )
                viewModel.addRowData(newData)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

    }

    private fun setUpButtonEliminar(view: View) {
        btnMEliminar.setOnClickListener {
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