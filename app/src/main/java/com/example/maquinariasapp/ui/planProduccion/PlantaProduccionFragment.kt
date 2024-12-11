package com.example.maquinariasapp.ui.planProduccion

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.maquinariasapp.BaseFragment
import com.example.maquinariasapp.LineaProdFragment
import com.example.maquinariasapp.NavigationHandler
import com.example.maquinariasapp.PlanProdFragment
import com.example.maquinariasapp.R
import com.example.maquinariasapp.data.RowData
import com.example.maquinariasapp.ui.linProduccion.LineaProduccionViewModel
import com.example.maquinariasapp.ui.maeMaquinaria.MaestroMaquinariaViewModel
import com.example.maquinariasapp.ui.maeMaquinaria.MaestroMaquinariaViewModelFactory

class PlantaProduccionFragment : BaseFragment<PlantaProduccionViewModel>() {

    private lateinit var btnAddRegister: ImageView

    override fun getViewModelClass(): Class<PlantaProduccionViewModel> {
        return PlantaProduccionViewModel::class.java
    }

    override fun navegationWithRow(rowData: RowData) {
        viewModel.selectRowData(rowData)
        val plaProFragment = PlanProdFragment().apply {
            arguments = Bundle().apply {
                putInt("operationType", 1) // 1 para edición
            }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, plaProFragment)
            .addToBackStack(null)
            .commit()
    }

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAddRegister = view.findViewById(R.id.addRegister)
        //val factory = PlantaProduccionViewModelFactory(requireContext())
        //viewModel = ViewModelProvider(this, factory).get(PlantaProduccionViewModel::class.java)
        setUpAddRegister(view)
    }

    private fun setUpAddRegister(view: View) {
        btnAddRegister.setOnClickListener {
            val plaProFragment = PlanProdFragment().apply {
                arguments = Bundle().apply {
                    putInt("operationType", 2) // 2 para nuevo registro
                }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, plaProFragment)
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