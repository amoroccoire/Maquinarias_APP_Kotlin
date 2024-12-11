package com.example.maquinariasapp.ui.linProduccion

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.maquinariasapp.BaseFragment
import com.example.maquinariasapp.LineaProdFragment
import com.example.maquinariasapp.MaquinariaFragment
import com.example.maquinariasapp.NavigationHandler
import com.example.maquinariasapp.R
import com.example.maquinariasapp.data.RowData
import com.example.maquinariasapp.ui.maeMaquinaria.MaestroMaquinariaViewModel
import com.example.maquinariasapp.ui.maeMaquinaria.MaestroMaquinariaViewModelFactory
import com.example.maquinariasapp.ui.planProduccion.PlantaProduccionViewModel

class LineaProduccionFragment : BaseFragment<LineaProduccionViewModel>(), NavigationHandler {

    private lateinit var btnAddRegister: ImageView

    override fun getViewModelClass(): Class<LineaProduccionViewModel> {
        return LineaProduccionViewModel::class.java
    }

    override fun navegationWithRow(rowData: RowData) {
        viewModel.selectRowData(rowData)
        val linProFragment = LineaProdFragment().apply {
            arguments = Bundle().apply {
                putInt("operationType", 1) // 1 para edición
            }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, linProFragment)
            .addToBackStack(null)
            .commit()
    }

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAddRegister = view.findViewById(R.id.addRegister)
        //val factory = MaestroMaquinariaViewModelFactory(requireContext())
        //viewModel = ViewModelProvider(this, factory).get(LineaProduccionViewModel::class.java)
        setUpAddRegister(view)
    }

    private fun setUpAddRegister(view: View) {
        btnAddRegister.setOnClickListener {
            val lineaProFragment = LineaProdFragment().apply {
                arguments = Bundle().apply {
                    putInt("operationType", 2) // 2 para nuevo registro
                }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, lineaProFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        //viewModel.fetchData()
    }

    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/
}