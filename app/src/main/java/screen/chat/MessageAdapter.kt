package screen.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.databinding.ItemMessageLeftBinding
import com.example.sababukia_tbc.databinding.ItemMessageRightBinding
import model.Message

private const val TYPE_LEFT = 0
private const val TYPE_RIGHT = 1

class MessageAdapter(
    private val timestampProvider: (Long) -> String
) : ListAdapter<Message, RecyclerView.ViewHolder>(Diff()) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).onLeft) TYPE_LEFT else TYPE_RIGHT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_LEFT) {
            val binding = ItemMessageLeftBinding.inflate(inflater, parent, false)
            LeftVH(binding)
        } else {
            val binding = ItemMessageRightBinding.inflate(inflater, parent, false)
            RightVH(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is LeftVH -> holder.bind(item, timestampProvider)
            is RightVH -> holder.bind(item, timestampProvider)
        }
    }

    private class LeftVH(private val binding: ItemMessageLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message, ts: (Long) -> String) = with(binding) {
            tvMessage.text = item.text
            tvTime.text = ts(item.timestamp.toEpochMilliseconds())
        }
    }

    private class RightVH(private val binding: ItemMessageRightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message, ts: (Long) -> String) = with(binding) {
            tvMessage.text = item.text
            tvTime.text = ts(item.timestamp.toEpochMilliseconds())
        }
    }

    private class Diff : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
            oldItem == newItem
    }
}
