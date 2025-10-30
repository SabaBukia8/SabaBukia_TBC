package screen.tictactoe

data class TicCell(
    val index: Int,
    val row: Int,
    val col: Int,
    val value: CellValue = CellValue.EMPTY,
)
