package com.example.sababukia_tbc.userPage

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import basics.BaseFragment
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentUserPageBinding
import com.google.android.material.snackbar.Snackbar

typealias Str = R.string

class UserPageFragment : BaseFragment<FragmentUserPageBinding>(FragmentUserPageBinding::inflate) {

    private val userAdapter by lazy { UserAdapter(::onUserLongClick) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(getString(Str.add_edit_user_request)) { _, bundle ->
            val result = bundle.getString(getString(Str.result_message))
            val isSuccess = bundle.getBoolean(getString(Str.is_success))
            showSnackbar(result, isSuccess)
            updateUserList()
        }
    }

    override fun listeners() {
        binding.addUserButton.setOnClickListener {
            findNavController().navigate(R.id.action_userPageFragment_to_addUserFragment)
        }
    }

    override fun bind() {
        setupRecyclerView()
        updateUserList()
    }

    private fun setupRecyclerView() {
        binding.usersRecyclerView.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun updateUserList() {
        val users = Users.getUsers()
        userAdapter.submitList(users.toList())
        binding.userCountTextView.text = getString(R.string.user_count_format, users.size)
    }

    private fun onUserLongClick(user: User) {
        val action = UserPageFragmentDirections.actionUserPageFragmentToAddUserFragment(user)
        findNavController().navigate(action)
    }

    private fun showSnackbar(message: String?, isSuccess: Boolean) {
        if (message == null) return
        val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
        val color = if (isSuccess) R.color.green else R.color.red
        snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), color))
        snackbar.show()
    }
}
