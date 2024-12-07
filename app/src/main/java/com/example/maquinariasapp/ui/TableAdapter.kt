package com.example.maquinariasapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maquinariasapp.R
import com.example.maquinariasapp.data.RowData

class TableAdapter (
    private var data: List<RowData>,
    private val visibleColumns: List<Boolean>

): RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    fun updateData(newData: List<RowData>) {
        data = newData
        notifyDataSetChanged()
    }

    inner class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cellViews = listOf<TextView>(
            itemView.findViewById(R.id.itemCod),
            itemView.findViewById(R.id.itemNom),
            itemView.findViewById(R.id.itemLinPro),
            itemView.findViewById(R.id.itemPlaPro),
            itemView.findViewById(R.id.itemUbi),
            itemView.findViewById(R.id.itemEstReg)
        )

        fun bind(rowData: RowData, visibleColumns: List<Boolean>) {
            rowData .cells.forEachIndexed{index, cell ->
                if (index < cellViews.size) {
                    cellViews[index].visibility =
                        if (visibleColumns[index]) View.VISIBLE else View.GONE
                    cellViews[index].text = cell
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_row, parent, false)
        return TableViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(data[position], visibleColumns)
    }
}