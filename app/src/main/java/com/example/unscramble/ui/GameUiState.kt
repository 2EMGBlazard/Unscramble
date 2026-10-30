package com.example.unscramble.ui

/**
 * Clase de datos que representa el estado de la IU para el juego Unscramble.
 */
data class GameUiState(
    val currentScrambledWord: String = "",
    val currentWordCount: Int = 1,
    val score: Int = 0,
    val isUserWordWrong: Boolean = false,
    val isGameOver: Boolean = false
)