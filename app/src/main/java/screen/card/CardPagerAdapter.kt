package screen.card

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.databinding.ItemCardBinding
import model.Card
import model.CardType
import util.CardFormatters

class CardPagerAdapter(
    private val onLongPress: (Card) -> Unit
) : ListAdapter<Card, CardPagerAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(oldItem: Card, newItem: Card) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Card, newItem: Card) = oldItem == newItem
    }

    inner class VH(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card) = with(binding) {
            tvNumber.text = CardFormatters.maskNumber(card.number)
            tvName.text = card.holderName
            tvExpiry.text = CardFormatters.displayExpiry(card.expiryMonth, card.expiryYear)
            logoVisa.alpha = if (card.type == CardType.VISA) 1f else 0.2f
            logoMc.alpha = if (card.type == CardType.MASTERCARD) 1f else 0.2f
            root.setOnLongClickListener { onLongPress(card); true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}
