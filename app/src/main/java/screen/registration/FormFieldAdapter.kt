package screen.registration

import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.ImageRequest
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.ItemFieldChooserBinding
import com.example.sababukia_tbc.databinding.ItemFieldInputBinding
import model.FieldType
import model.FormField
import model.KeyboardType

class FormFieldAdapter(
    private val onFieldValueChanged: (fieldId: Int, value: String) -> Unit,
    private val onChooserClick: (fieldId: Int, hint: String) -> Unit,
    private val getFieldValue: (fieldId: Int) -> String = { "" }
) : ListAdapter<FormField, RecyclerView.ViewHolder>(FieldDiff()) {

    companion object {
        private const val TYPE_INPUT = 0
        private const val TYPE_CHOOSER = 1

        private const val HINT_BIRTHDAY = "birthday"
        private const val HINT_GENDER = "gender"
        private const val HINT_NAME = "name"
        private const val HINT_PIN = "pin"
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).fieldType) {
            FieldType.INPUT -> TYPE_INPUT
            FieldType.CHOOSER -> TYPE_CHOOSER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_INPUT -> {
                val binding = ItemFieldInputBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                InputViewHolder(binding)
            }

            else -> {
                val binding = ItemFieldChooserBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ChooserViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val field = getItem(position)
        when (holder) {
            is InputViewHolder -> holder.bind(field)
            is ChooserViewHolder -> holder.bind(field)
        }
    }

    inner class InputViewHolder(
        private val binding: ItemFieldInputBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var isUpdating = false

        fun bind(field: FormField) = with(binding) {
            root.hint = field.hint

            etValue.inputType = when (field.keyboard) {
                KeyboardType.TEXT -> InputType.TYPE_CLASS_TEXT
                KeyboardType.NUMBER -> InputType.TYPE_CLASS_NUMBER
                KeyboardType.EMAIL -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }

            val errorDrawable = when {
                field.keyboard == KeyboardType.EMAIL -> R.drawable.ic_email
                field.keyboard == KeyboardType.NUMBER -> R.drawable.ic_phone
                field.hint.contains(HINT_NAME, ignoreCase = true) -> R.drawable.ic_person
                field.hint.contains(HINT_PIN, ignoreCase = true) -> R.drawable.ic_lock
                else -> R.drawable.ic_person
            }.let { AppCompatResources.getDrawable(itemView.context, it) }

            val request = ImageRequest.Builder(itemView.context)
                .data(field.icon)
                .target { result ->
                    root.endIconDrawable = result
                }
                .error(errorDrawable)
                .build()
            itemView.context.imageLoader.enqueue(request)


            val currentValue = getFieldValue(field.fieldId)
            if (etValue.text.toString() != currentValue) {
                isUpdating = true
                etValue.setText(currentValue)
                isUpdating = false
            }

            etValue.doAfterTextChanged { text ->
                if (!isUpdating) {
                    onFieldValueChanged(field.fieldId, text?.toString() ?: "")
                }
            }
        }
    }

    inner class ChooserViewHolder(
        private val binding: ItemFieldChooserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(field: FormField) = with(binding) {
            root.hint = field.hint

            val currentValue = getFieldValue(field.fieldId)
            etValue.setText(currentValue, false)

            val errorDrawable = when {
                field.hint.contains(HINT_BIRTHDAY, ignoreCase = true) -> R.drawable.ic_calendar
                field.hint.contains(HINT_GENDER, ignoreCase = true) -> R.drawable.ic_person
                else -> R.drawable.ic_calendar
            }.let { AppCompatResources.getDrawable(itemView.context, it) }

            val request = ImageRequest.Builder(itemView.context)
                .data(field.icon)
                .target { result ->
                    root.endIconDrawable = result
                }
                .error(errorDrawable)
                .build()
            itemView.context.imageLoader.enqueue(request)

            root.setOnClickListener { onChooserClick(field.fieldId, field.hint) }
            etValue.setOnClickListener { onChooserClick(field.fieldId, field.hint) }
        }
    }

    private class FieldDiff : DiffUtil.ItemCallback<FormField>() {
        override fun areItemsTheSame(oldItem: FormField, newItem: FormField): Boolean {
            return oldItem.fieldId == newItem.fieldId
        }

        override fun areContentsTheSame(oldItem: FormField, newItem: FormField): Boolean {
            return oldItem == newItem
        }
    }
}
