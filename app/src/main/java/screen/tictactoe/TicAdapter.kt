package screen.tictactoe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.R

class TicAdapter(
    private val onCellClick: (TicCell) -> Unit
) : ListAdapter<TicCell, TicAdapter.VH>(Diff) {

    init {
        setHasStableIds(true)
    }

    object Diff : DiffUtil.ItemCallback<TicCell>() {
        override fun areItemsTheSame(oldItem: TicCell, newItem: TicCell): Boolean =
            oldItem.index == newItem.index

        override fun areContentsTheSame(oldItem: TicCell, newItem: TicCell): Boolean =
            oldItem == newItem
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.icon)

        fun bind(item: TicCell) {
            when (item.value) {
                CellValue.EMPTY -> icon.setImageDrawable(null)
                CellValue.X -> icon.setImageResource(R.drawable.ic_x)
                CellValue.O -> icon.setImageResource(R.drawable.ic_o)
            }

            itemView.isEnabled = item.value == CellValue.EMPTY
            itemView.setOnClickListener {
                if (item.value == CellValue.EMPTY) onCellClick(item)
            }
        }
    }

    override fun getItemId(position: Int): Long = getItem(position).index.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tictactoe_cell, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}
