@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

/** Where the game stands, derived — never stored — from the snapshot. */
enum class GameStatus {
    PLAYING,

    /** 2048 was reached and "Keep playing" has not been chosen yet. */
    WON,

    /** No empty cell and no equal orthogonal neighbours. */
    GAME_OVER,
}

/**
 * Everything needed to continue a game exactly: the board, the score, the
 * generator position and the two flags. `rule.md` §5 — a board is never lost, and
 * every board is reproducible from `(seed, moves)`.
 */
data class GameSnapshot(
    val board: Board,
    val score: Int,
    val randomState: Long,
    val reached2048: Boolean,
    val keepPlaying: Boolean,
    val moves: Int,
) {
    init {
        require(score >= 0) { "score cannot be negative: $score" }
        require(moves >= 0) { "moves cannot be negative: $moves" }
    }
}

/**
 * One game of Tilefold: an immutable value plus, optionally, the single snapshot
 * that [undo] restores.
 *
 * Every operation returns a **new** [Game]; the instance handed in is never
 * mutated, so a ViewModel can hold it in one `StateFlow` and the screenshot tests
 * can render it without surprises.
 */
class Game private constructor(
    val snapshot: GameSnapshot,
    private val previous: GameSnapshot?,
) {
    val board: Board
        get() = snapshot.board

    val score: Int
        get() = snapshot.score

    val moves: Int
        get() = snapshot.moves

    /** One free undo is available while [previous] exists (limits are a UI/Policy concern). */
    val canUndo: Boolean
        get() = previous != null

    /** The snapshot [undo] would restore, exposed so the save can keep it. */
    val undoSnapshot: GameSnapshot?
        get() = previous

    /** True while the win dialog is still owed to the player. */
    val hasWon: Boolean
        get() = snapshot.reached2048 && !snapshot.keepPlaying

    val status: GameStatus
        get() =
            when {
                hasWon -> GameStatus.WON
                isGameOver(board) -> GameStatus.GAME_OVER
                else -> GameStatus.PLAYING
            }

    /**
     * Slides everything and spawns the follow-up tile.
     *
     * A move that changes nothing returns **this** instance: no spawn, no score,
     * no RNG draw — an invalid swipe must be free.
     */
    fun move(direction: Direction): Game {
        // No input while a dialog is owed or the game is over: a refused swipe
        // must be free (no spawn, no RNG draw), so the dialog cannot be skipped.
        if (status != GameStatus.PLAYING) return this

        val outcome = Moves.apply(board, direction)
        if (outcome !is Moves.Moved) return this

        val random = EngineRandom.fromState(snapshot.randomState)
        val spawned = Spawner.spawn(outcome.board, random) ?: return this

        val next =
            GameSnapshot(
                board = spawned,
                score = snapshot.score + outcome.scoreDelta,
                randomState = random.state,
                reached2048 = snapshot.reached2048 || spawned.highestTile >= WINNING_TILE,
                keepPlaying = snapshot.keepPlaying,
                moves = snapshot.moves + 1,
            )
        return Game(next, snapshot)
    }

    /** Restores the state before the last move, including the generator position. */
    fun undo(): Game? = previous?.let { Game(it, null) }

    /** Accepts the win and continues on the same board. */
    fun keepPlaying(): Game = Game(snapshot.copy(keepPlaying = true), previous)

    /** For a Phase-3 "resume": rebuild a game from a stored snapshot. */
    companion object {
        const val WINNING_TILE = 2048
        private const val STARTING_TILES = 2

        /**
         * A fresh game: two tiles placed from [seed]. The same seed always gives
         * the same opening board (`docs/DECISIONS.md`, "Daily seed convention").
         */
        fun newGame(seed: Long): Game {
            val random = EngineRandom.fromSeed(seed)
            var board = Board.EMPTY
            repeat(STARTING_TILES) {
                board = Spawner.spawn(board, random) ?: board
            }
            val snapshot =
                GameSnapshot(
                    board = board,
                    score = 0,
                    randomState = random.state,
                    reached2048 = false,
                    keepPlaying = false,
                    moves = 0,
                )
            return Game(snapshot, null)
        }

        /** Rebuilds a game from a persisted snapshot (no undo history). */
        fun restore(snapshot: GameSnapshot): Game = Game(snapshot, null)

        /** Rebuilds a game together with the single snapshot its undo restores. */
        fun restoreWithUndo(
            snapshot: GameSnapshot,
            undo: GameSnapshot,
        ): Game = Game(snapshot, undo)

        /** Game over = the board is full **and** no orthogonal neighbours are equal. */
        fun isGameOver(board: Board): Boolean = board.isFull && !hasEqualNeighbour(board)

        private fun hasEqualNeighbour(board: Board): Boolean =
            (0 until Board.WIDTH).any { row ->
                (0 until Board.WIDTH).any { col ->
                    val value = board.valueAt(row, col)
                    val rightMatches = col + 1 < Board.WIDTH && board.valueAt(row, col + 1) == value
                    val downMatches = row + 1 < Board.WIDTH && board.valueAt(row + 1, col) == value
                    value != 0 && (rightMatches || downMatches)
                }
            }
    }
}
