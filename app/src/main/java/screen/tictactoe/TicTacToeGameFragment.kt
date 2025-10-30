package screen.tictactoe

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import basics.BaseFragment
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentTictactoeGameBinding

class TicTacToeGameFragment :
    BaseFragment<FragmentTictactoeGameBinding>(FragmentTictactoeGameBinding::inflate) {

    private val args: TicTacToeGameFragmentArgs by navArgs()

    private var size = 3
    private var cells: MutableList<TicCell> = mutableListOf()
    private val adapter = TicAdapter { onCellClicked(it) }
    private var current = Player.PLAYER_X
    private var finished = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        size = args.boardSize
        setupRecycler()
        initBoard()
        updateStatus()
    }

    private fun setupRecycler() = with(binding) {
        grid.layoutManager = GridLayoutManager(requireContext(), size)
        grid.adapter = adapter
    }

    private fun initBoard() {
        cells = MutableList(size * size) { idx ->
            val r = idx / size
            val c = idx % size
            TicCell(index = idx, row = r, col = c, value = CellValue.EMPTY)
        }
        adapter.submitList(cells.toList())
    }

    private fun onCellClicked(cell: TicCell) {
        if (finished || cell.value != CellValue.EMPTY) return
        val index = cell.index
        val newValue = if (current == Player.PLAYER_X) CellValue.X else CellValue.O
        cells = cells.toMutableList().also { list ->
            list[index] = list[index].copy(value = newValue)
        }
        adapter.submitList(cells.toList())

        if (hasKInARow(newValue, size)) {
            finished = true
            showEnd(if (current == Player.PLAYER_X) getString(R.string.x_wins) else getString(R.string.o_wins))
            return
        }
        if (cells.all { it.value != CellValue.EMPTY }) {
            finished = true
            showEnd(getString(R.string.draw))
            return
        }
        current = current.next()
        updateStatus()
    }

    private fun updateStatus() = with(binding) {
        status.text =
            if (current == Player.PLAYER_X) getString(R.string.player_x_turn) else getString(R.string.player_o_turn)
    }

    private fun showEnd(message: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(getString(R.string.play_again)) { _, _ -> reset() }
            .setNegativeButton(getString(R.string.back)) { _, _ -> findNavController().popBackStack() }
            .setCancelable(false)
            .show()
    }

    private fun reset() {
        current = Player.PLAYER_X
        finished = false
        initBoard()
        updateStatus()
    }

    private fun hasKInARow(v: CellValue, k: Int): Boolean {
        val n = size
        fun at(r: Int, c: Int) = cells[r * n + c].value
        val dirs = arrayOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)
        for (row in 0 until n) for (col in 0 until n) if (at(row, col) == v) {
            for ((dr, dc) in dirs) {
                var count = 0
                var rr = row
                var cc = col
                while (rr in 0 until n && cc in 0 until n && at(rr, cc) == v) {
                    count++
                    if (count == k) return true
                    rr += dr
                    cc += dc
                }
            }
        }
        return false
    }
}
