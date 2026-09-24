package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.CardTheme
import com.example.game.Difficulty
import com.example.game.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Memory Match", appName)
    }

    @Test
    fun `verify game initialization with medium difficulty`() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = GameViewModel(context)
        val state = viewModel.gameState.value

        assertEquals(Difficulty.MEDIUM, state.difficulty)
        assertEquals(8, state.totalPairs)
        assertEquals(16, state.cards.size)
        assertEquals(0, state.moves)
        assertEquals(0, state.matchesFound)
        assertFalse(state.isVictory)
        assertFalse(state.isGameActive)
    }

    @Test
    fun `verify all difficulty card counts`() {
        assertEquals(12, Difficulty.EASY.rows * Difficulty.EASY.cols)
        assertEquals(16, Difficulty.MEDIUM.rows * Difficulty.MEDIUM.cols)
        assertEquals(20, Difficulty.HARD.rows * Difficulty.HARD.cols)
        assertEquals(24, Difficulty.EXPERT.rows * Difficulty.EXPERT.cols)

        assertEquals(6, Difficulty.EASY.pairs)
        assertEquals(8, Difficulty.MEDIUM.pairs)
        assertEquals(10, Difficulty.HARD.pairs)
        assertEquals(12, Difficulty.EXPERT.pairs)
    }

    @Test
    fun `verify all card themes have sufficient items`() {
        CardTheme.values().forEach { theme ->
            assertTrue(theme.items.size >= 12)
        }
    }
}
