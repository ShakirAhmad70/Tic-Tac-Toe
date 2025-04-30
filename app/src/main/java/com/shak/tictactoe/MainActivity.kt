package com.shak.tictactoe

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.shak.tictactoe.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentPlayer: Char? = null
    private var count: Int = 0
    private var playerMapToLetter: MutableMap<Char, Int> = mutableMapOf()
    private var player1WinsCountNo: Int = 0
    private var player2WinsCountNo: Int = 0
    private var playerNo: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playTypingAnimation(binding.mainTxt, resources.getString(R.string.tic_tac_toe), 200)
        playTypingAnimation(binding.player1WinsCountTxt, resources.getString(R.string.player_1) + " $player1WinsCountNo")
        playTypingAnimation(binding.player2WinsCountTxt, resources.getString(R.string.player_2) + " $player2WinsCountNo")

        binding.resetGameBtn.isClickable = false //Disable the click on reset btn until throw toss

        binding.tossTxt.setOnClickListener{
            //Toss for the game
            if(playerNo == -1){  //First time toss
                playerNo = toss() //Returns who won the toss
            }
            //Choose X or O via a dialog
            lifecycleScope.launch {
                delay(330) //Wait for 330 milliseconds before showing the dialog because of the typing animation in toss

                val dialog = Dialog(this@MainActivity)
                dialog.setContentView(R.layout.choose_x_o_dialog_layout)
                dialog.setCancelable(false)

                if(playerNo == 1){
                    dialog.findViewById<AppCompatTextView>(R.id.whoWonTheTossTxt).text = resources.getString(R.string.player1_won_toss)
                } else {
                    dialog.findViewById<AppCompatTextView>(R.id.whoWonTheTossTxt).text = resources.getString(R.string.player2_won_toss)
                }

                dialog.findViewById<AppCompatButton>(R.id.letterX).setOnClickListener{
                    currentPlayer = 'X'
                    playerMapToLetter['X'] = playerNo
                    playerMapToLetter['O'] = if(playerNo == 1) 2 else 1
                    Toast.makeText(this@MainActivity, "Player $playerNo chose X", Toast.LENGTH_SHORT).show()
                    binding.resetGameBtn.isClickable = true
                    dialog.dismiss()
                }
                dialog.findViewById<AppCompatButton>(R.id.letterO).setOnClickListener{
                    currentPlayer = 'O'
                    playerMapToLetter['O'] = playerNo
                    playerMapToLetter['X'] = if(playerNo == 1) 2 else 1
                    Toast.makeText(this@MainActivity, "Player $playerNo chose O", Toast.LENGTH_SHORT).show()
                    binding.resetGameBtn.isClickable = true
                    dialog.dismiss()
                }

                dialog.show()
            }
        }

        //Set the onClickListener for all the buttons
        getAllGridButtonsList()
            .forEach {
                view -> view.setOnClickListener {
                    setXorO(view)
                }
            }

        binding.resetGameBtn.setOnClickListener{
            resetGame()
        }

        binding.clearAllBtn.setOnClickListener{
            getAllGridButtonsList()
                .forEach {
                    it.background = null  //reset the background
                    it.tag = null  //reset the tag
                }

            playTypingAnimation(binding.tossTxt, resources.getString(R.string.toss))
            binding.tossTxt.isClickable = true
            binding.resetGameBtn.isClickable = false

            playTypingAnimation(binding.totalWinsTxt, resources.getString(R.string.total_wins))

            player1WinsCountNo = 0
            player2WinsCountNo = 0
            playTypingAnimation(binding.player1WinsCountTxt, resources.getString(R.string.player_1) + " $player1WinsCountNo")
            playTypingAnimation(binding.player2WinsCountTxt, resources.getString(R.string.player_2) + " $player2WinsCountNo")
        }

    }

    private fun getAllGridButtonsList() = listOf(binding.btn1View, binding.btn2View, binding.btn3View,
        binding.btn4View, binding.btn5View, binding.btn6View,
        binding.btn7View, binding.btn8View, binding.btn9View)

    private fun setXorO(view: View) {
        if(currentPlayer != null && view.background == null){
            if(currentPlayer == 'X'){
                view.background = ResourcesCompat.getDrawable(resources, R.drawable.letter_x, null)
                currentPlayer = 'O'
                view.tag = "X"  //set the tag to X so that it can be used in checkForWin for comparison
            } else {
                view.background = ResourcesCompat.getDrawable(resources, R.drawable.letter_o, null)
                currentPlayer = 'X'
                view.tag = "O"  //set the tag to O so that it can be used in checkForWin for comparison
            }

            if (binding.tossTxt.text == resources.getString(R.string.player1_turn)) {
                binding.tossTxt.text = resources.getString(R.string.player2_turn)
            } else {
                binding.tossTxt.text = resources.getString(R.string.player1_turn)
            }

            count++
            if(count >= 5){
                checkForWin()
            }
        }
    }

    private fun checkForWin() {
        val b1 = binding.btn1View.tag
        val b2 = binding.btn2View.tag
        val b3 = binding.btn3View.tag
        val b4 = binding.btn4View.tag
        val b5 = binding.btn5View.tag
        val b6 = binding.btn6View.tag
        val b7 = binding.btn7View.tag
        val b8 = binding.btn8View.tag
        val b9 = binding.btn9View.tag

        when {
            b1 != null && b1 == b2 && b2 == b3 -> {
                declareWinner(binding.btn1View)
                lifecycleScope.launch {
                    delay(3000)
                    resetGame()
                }
            }
            b4 != null && b4 == b5 && b5 == b6 -> {
                declareWinner(binding.btn4View)
                lifecycleScope.launch {
                    delay(3000)
                    resetGame()
                }
            }
            b7 != null && b7 == b8 && b8 == b9 -> {
                declareWinner(binding.btn7View)
                lifecycleScope.launch {
                    delay(3000)
                    resetGame()
                }
            }
            b1 != null && b1 == b4 && b4 == b7 -> {
                declareWinner(binding.btn1View)
                lifecycleScope.launch {
                    delay(3000)
                    resetGame()
                }
            }
            b2 != null && b2 == b5 && b5 == b8 -> {
                declareWinner(binding.btn2View)
                lifecycleScope.launch {
                    delay(3000)
                    resetGame()
                }
            }
            b3 != null && b3 == b6 && b6 == b9 -> {
                declareWinner(binding.btn3View)
                lifecycleScope.launch {
                    delay(3000)
                    resetGame()
                }
            }
            b1 != null && b1 == b5 && b5 == b9 -> {
                declareWinner(binding.btn1View)
                lifecycleScope.launch {
                    delay(3000)
                    resetGame()
                }
            }
            b3 != null && b3 == b5 && b5 == b7 -> {
                declareWinner(binding.btn3View)
                lifecycleScope.launch {
                    delay(3000)
                    resetGame()
                }
            }
            else -> {
                if(count == 9){
                    binding.gameResultTxt.visibility = View.VISIBLE
                    playTypingAnimation(binding.gameResultTxt, resources.getString(R.string.draw))
                    lifecycleScope.launch {
                        delay(3000)
                        resetGame()
                    }
                }
            }
        }
    }

    private fun declareWinner(view: View) {
        //First disable all the buttons so that they can't be clicked again
        getAllGridButtonsList()
            .forEach {
                it.isClickable = false
            }

        binding.gameResultTxt.visibility = View.VISIBLE
        val playerNo = if(view.tag == "X"){
            playerMapToLetter['X']
        } else {
            playerMapToLetter['O']
        }
        if(playerNo == 1){
            binding.gameResultTxt.text = getString(R.string.player1_won)
            player1WinsCountNo++
        } else {
            binding.gameResultTxt.text = getString(R.string.player2_won)
            player2WinsCountNo++
        }

        playTypingAnimation(binding.totalWinsTxt, resources.getString(R.string.total_wins))
        playTypingAnimation(binding.player1WinsCountTxt, resources.getString(R.string.player_1) + " $player1WinsCountNo")
        playTypingAnimation(binding.player2WinsCountTxt, resources.getString(R.string.player_2) + " $player2WinsCountNo")
    }


    private fun resetGame() {
        currentPlayer = null  //Reset the current player
        count = 0
        binding.gameResultTxt.visibility = View.GONE

        //Change the text to Toss
        binding.tossTxt.isClickable = true
        if(playerNo == -1){  //First time toss
            playTypingAnimation(binding.tossTxt, resources.getString(R.string.toss))
        } else {  //Otherwise play again
            playTypingAnimation(binding.tossTxt, resources.getString(R.string.play_again))
        }


        //Reset the grid
        getAllGridButtonsList()
            .forEach {
                it.background = null  //reset the background
                it.tag = null  //reset the tag
                it.isClickable = true
            }
    }

    private fun toss(): Int {
        val playerNo = Random.nextInt(2) + 1
        if(playerNo == 1){
            playTypingAnimation(binding.tossTxt, resources.getString(R.string.player1_turn))
        } else {
            playTypingAnimation(binding.tossTxt, resources.getString(R.string.player2_turn))
        }
        binding.tossTxt.isClickable = false //Disable the click on toss btn until reset or someone wins
        return playerNo
    }

    private fun playTypingAnimation(viewForTyping: AppCompatTextView, textToType: String, perLetterDelay: Long = 20) {
        viewForTyping.text = "" // Clear the text first
        var index = 0

        //Using the coroutine scope for delay
//        CoroutineScope(Dispatchers.Main).launch {   //OR
        lifecycleScope.launch {
            while (index < textToType.length) {
                viewForTyping.text = textToType.substring(0, index + 1)
                index++
                delay(perLetterDelay)
            }
        }
    }

}