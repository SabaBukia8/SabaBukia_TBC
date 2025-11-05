package screen.chat

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import basics.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentChatBinding
import data.ChatConstants
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class ChatFragment : BaseFragment<FragmentChatBinding>(FragmentChatBinding::inflate) {

    private val viewModel: ChatViewModel by viewModels()
    private var adapter: MessageAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setupRecycler()
        setupInput()
        observeMessages()
    }

    private fun setupHeader() = with(binding) {
        tvTitle.text = ChatConstants.ADMIN_TITLE
        tvSubtitle.text = ChatConstants.ONLINE
    }

    private fun setupRecycler() = with(binding) {
        adapter = MessageAdapter(
            timestampProvider = { millis ->
                viewModel.formatTimestamp(Instant.fromEpochMilliseconds(millis))
            }
        )
        rvMessages.layoutManager = LinearLayoutManager(requireContext())
        rvMessages.adapter = adapter
    }

    private fun setupInput() = with(binding) {
        etMessage.hint = ChatConstants.HINT_TYPE_MESSAGE
        btnSend.isEnabled = false
        etMessage.addTextChangedListener { text ->
            val enabled = !text?.toString()?.trim().isNullOrEmpty()
            btnSend.isEnabled = enabled
        }
        btnSend.setOnClickListener {
            val text = etMessage.text?.toString().orEmpty()
            viewModel.send(text)
            etMessage.setText("")
        }
    }

    private fun observeMessages() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messages.collect { list ->
                    adapter?.submitList(list)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvMessages.adapter = null
        adapter = null
        super.onDestroyView()
    }
}
