package screen.card

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.sababukia_tbc.databinding.BottomsheetConfirmDeleteBinding

class DeleteCardBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetConfirmDeleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetConfirmDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cardId = requireArguments().getString(ARG_CARD_ID) ?: return
        binding.btnYes.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                Bundle().apply { putString(RESULT_CARD_ID, cardId) }
            )
            dismiss()
        }
        binding.btnNo.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val REQUEST_KEY = "delete_card_request"
        const val RESULT_CARD_ID = "result_card_id"
        private const val ARG_CARD_ID = "arg_card_id"
        fun new(cardId: String) = DeleteCardBottomSheet().apply {
            arguments = Bundle().apply { putString(ARG_CARD_ID, cardId) }
        }
    }
}
