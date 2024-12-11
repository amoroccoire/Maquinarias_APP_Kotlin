package com.example.maquinariasapp.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.maquinariasapp.NavigationHandler
import com.example.maquinariasapp.R
import com.example.maquinariasapp.data.RowData

class TableAdapter (
    private var data: List<RowData>,
    private val visibleColumns: List<Boolean>,
    //private val navigationHandler: NavigationHandler
    private val onRowClick: (RowData) -> Unit

): RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    private var isClickable: Boolean = false

    fun setClickable(clickable: Boolean) {
        isClickable = clickable
        Log.d("EN ADAPTER TABLE updateData()", "VALOR DE isClickable ${isClickable}")
        notifyDataSetChanged()
    }
    fun updateData(newData: List<RowData>) {
        data = newData
        notifyDataSetChanged()
    }

    inner class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cellContainer: LinearLayout = itemView.findViewById(R.id.cellContainer)
        private val editRow: ImageView = itemView.findViewById(R.id.editRow)

        /*private val cellViews = listOf<TextView>(
            itemView.findViewById(R.id.itemCod),
            itemView.findViewById(R.id.itemNom),
            itemView.findViewById(R.id.itemLinPro),
            itemView.findViewById(R.id.itemPlaPro),
            itemView.findViewById(R.id.itemUbi),
            itemView.findViewById(R.id.itemEstReg)
        )*/

        fun bind(rowData: RowData, visibleColumns: List<Boolean>, isClickable: Boolean) {
            cellContainer.removeAllViews()
            Log.d("EN BIND", "LUEGO DE REMOVER AQUIIIIIIIIIIIIIIIIIIIIIIIIIIIII")
            rowData.cells.forEachIndexed { index, cell ->
                if (visibleColumns.getOrNull(index) == true) {
                    val textView = TextView(itemView.context).apply {
                        text = cell
                        layoutParams = LinearLayout.LayoutParams(
                            1,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f // Peso igual para cada celda
                        )
                        setPadding(8, 8, 8, 8)
                        //setBackgroundResource(R.color.black)
                        gravity = android.view.Gravity.CENTER
                    }
                    //textView.setTextColor( getColor(R.color.black))
                    cellContainer.addView(textView)
                    Log.d("EN BIND", "Dentro del if AQUIIIIIIIIIIIIIIIIIIIIIIIIIIIII")
                }
            }

            /*rowData .cells.forEachIndexed{index, cell ->
                if (index < cellViews.size) {
                    cellViews[index].visibility =
                        if (visibleColumns[index]) View.VISIBLE else View.GONE
                    cellViews[index].text = cell
                }
            }*/
            // Configura el clic solo si está habilitado

            if (isClickable) {
                Log.d("EN ADAPTER TABLE bind()", "LUEGO DE IF ${isClickable}")
                editRow.visibility = View.VISIBLE
                editRow.setOnClickListener {
                    onRowClick(rowData)
                }
            } else {
                editRow.visibility = View.GONE
                editRow.setOnClickListener(null) // Desactiva el clic
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_row, parent, false)
        return TableViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(data[position], visibleColumns, isClickable)
    }
}