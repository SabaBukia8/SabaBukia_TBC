package screen.card

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.R
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

            tvName.text = card.holderName.uppercase()

            tvExpiry.text = CardFormatters.displayExpiry(card.expiryMonth, card.expiryYear)

            when (card.type) {
                CardType.VISA -> {
                    cardBackground.setBackgroundResource(R.drawable.card_gradient_blue)
                    logoVisa.visibility = View.VISIBLE
                    logoMc.visibility = View.GONE
                    ivCreditOverlay.visibility = View.GONE
                }

                CardType.MASTERCARD -> {
                    cardBackground.setBackgroundResource(R.drawable.card_gradient_background)
                    logoVisa.visibility = View.GONE
                    logoMc.visibility = View.VISIBLE
                    ivCreditOverlay.visibility = View.VISIBLE
                }
            }

            root.setOnLongClickListener {
                onLongPress(card)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}