@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * The save blob: `(schemaVersion, board, score, rng position, flags)`.
 *
 * Two rules from `rule.md` §5 drive this file:
 * - **a board is never lost** — anything unreadable returns `null` so the caller
 *   can fall back to a new game *and* show the storage-error banner, instead of
 *   crashing or restoring a half-board;
 * - **destructive migration is a data-loss bug** — an unknown *newer* version is
 *   refused rather than guessed at, and every field has a default so an *older*
 *   payload still decodes.
 *
 * The undo snapshot is persisted too, so the free undo survives a process kill.
 */
object SaveCodec {
    /** Bump on every incompatible change and add a decode test for the old shape. */
    const val CURRENT_SCHEMA_VERSION = 1

    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = false
        }

    fun encode(game: Game): String = json.encodeToString(SaveDto.from(game.snapshot, game.undoSnapshot))

    /**
     * @return a restorable [Game], or `null` when the payload is missing, corrupt,
     *         from a future schema, or internally impossible.
     */
    fun decode(payload: String?): Game? {
        if (payload.isNullOrBlank()) return null
        val dto = runCatching { json.decodeFromString<SaveDto>(payload) }.getOrNull() ?: return null
        return dto.toGame()
    }

    @Serializable
    private data class SaveDto(
        val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
        val board: List<Int> = emptyList(),
        val score: Int = 0,
        val randomState: Long = 1L,
        val reached2048: Boolean = false,
        val keepPlaying: Boolean = false,
        val moves: Int = 0,
        val previous: SaveDto? = null,
    ) {
        fun toGame(): Game? {
            if (schemaVersion < 1 || schemaVersion > CURRENT_SCHEMA_VERSION) return null
            val current = toSnapshot() ?: return null
            val undo = previous?.toSnapshot()
            return if (undo == null) Game.restore(current) else Game.restoreWithUndo(current, undo)
        }

        private fun toSnapshot(): GameSnapshot? {
            if (board.size != Board.CELLS) return null
            if (score < 0 || moves < 0) return null
            // A zero generator state is degenerate for xorshift: refuse, never loop.
            if (randomState == 0L) return null
            if (!board.all { it == 0 || Board.isTileValue(it) }) return null
            val cells = Board.of(board)
            return GameSnapshot(
                board = cells,
                score = score,
                randomState = randomState,
                // Tolerate a payload that reached 2048 before the flag existed.
                reached2048 = reached2048 || cells.highestTile >= Game.WINNING_TILE,
                keepPlaying = keepPlaying,
                moves = moves,
            )
        }

        companion object {
            fun from(
                snapshot: GameSnapshot,
                undo: GameSnapshot?,
            ): SaveDto =
                SaveDto(
                    schemaVersion = CURRENT_SCHEMA_VERSION,
                    board = snapshot.board.values,
                    score = snapshot.score,
                    randomState = snapshot.randomState,
                    reached2048 = snapshot.reached2048,
                    keepPlaying = snapshot.keepPlaying,
                    moves = snapshot.moves,
                    previous = undo?.let { from(it, null) },
                )
        }
    }
}
