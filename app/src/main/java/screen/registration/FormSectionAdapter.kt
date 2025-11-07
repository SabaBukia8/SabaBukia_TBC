package screen.registration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.databinding.ItemFormSectionBinding
import model.FormSection

class FormSectionAdapter(
    private val onFieldValueChanged: (fieldId: Int, value: String) -> Unit,
    private val onChooserClick: (fieldId: Int, hint: String) -> Unit,
    private val getFieldValue: (fieldId: Int) -> String = { "" }
) : ListAdapter<FormSection, FormSectionAdapter.SectionViewHolder>(SectionDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val binding = ItemFormSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SectionViewHolder(
        binding: ItemFormSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val fieldAdapter =
            FormFieldAdapter(onFieldValueChanged, onChooserClick, getFieldValue)

        init {
            binding.rvFields.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = fieldAdapter
            }
        }

        fun bind(section: FormSection) {
            fieldAdapter.submitList(section.fields)
        }
    }

    private class SectionDiff : DiffUtil.ItemCallback<FormSection>() {
        override fun areItemsTheSame(oldItem: FormSection, newItem: FormSection): Boolean {
            return oldItem.fields.map { it.fieldId } == newItem.fields.map { it.fieldId }
        }

        override fun areContentsTheSame(oldItem: FormSection, newItem: FormSection): Boolean {
            return oldItem == newItem
        }
    }
}