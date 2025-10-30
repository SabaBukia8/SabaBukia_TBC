package screen.tictactoe

import androidx.navigation.fragment.findNavController
import basics.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentTictactoeConfigBinding

class TicTacToeConfigFragment :
    BaseFragment<FragmentTictactoeConfigBinding>(FragmentTictactoeConfigBinding::inflate) {

    override fun listeners() = with(binding) {
        btnStart.setOnClickListener {
            val size = when {
                gridSize4.isChecked -> 4
                gridSize5.isChecked -> 5
                else -> 3
            }
            val action = TicTacToeConfigFragmentDirections.actionTictactoeConfigFragmentToTictactoeGameFragment(size)
            findNavController().navigate(action)
        }
    }
}
