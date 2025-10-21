package basics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class BaseFragment <VB : ViewBinding>(private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB) : Fragment() {
    private var _binding: VB? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
        bind()
    }

    abstract fun listeners()

    abstract fun bind()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}