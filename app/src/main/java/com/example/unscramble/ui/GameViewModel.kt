package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    // Estado del juego expuesto como StateFlow de solo lectura
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // Estado local para el campo de texto ingresado por el usuario
    var userGuess by mutableStateOf("")
        private set

    // Registro de palabras ya usadas en la partida actual
    private var usedWords: MutableSet<String> = mutableSetOf()
    private lateinit var currentWord: String

    init {
        resetGame()
    }

    /*
     * Reinicia los datos del juego para comenzar una nueva partida.
     */
    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    /*
     * Actualiza la palabra que escribe el usuario.
     */
    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    /*
     * Comprueba el intento del usuario y actualiza la puntuación.
     */
    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            // Intento correcto: aumenta la puntuación y prepara la siguiente ronda
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            // Intento incorrecto: muestra error
            _uiState.update { currentState ->
                currentState.copy(isUserWordWrong = true)
            }
        }
        // Limpia el texto ingresado
        updateUserGuess("")
    }

    /*
     * Omite la palabra actual sin otorgar puntos.
     */
    fun skipWord() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    /*
     * Prepara el estado para la siguiente ronda o finaliza el juego si se alcanza el máximo.
     */
    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            // Última ronda alcanzada
            _uiState.update { currentState ->
                currentState.copy(
                    isUserWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else {
            // Siguiente palabra
            _uiState.update { currentState ->
                currentState.copy(
                    isUserWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc()
                )
            }
        }
    }

    /*
     * Elige una palabra al azar de allWords y desordena sus letras.
     */
    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }
}