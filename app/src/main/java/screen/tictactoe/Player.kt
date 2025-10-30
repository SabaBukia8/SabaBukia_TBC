package screen.tictactoe

enum class Player {
    PLAYER_X,
    PLAYER_O;

    fun next() = if (this == PLAYER_X) PLAYER_O else PLAYER_X
}