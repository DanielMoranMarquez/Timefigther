package com.example.timefigther

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {

    internal var gameStarted = false

    internal var score = 0

    internal lateinit var countDownTimer: CountDownTimer
    internal val initialCountDown: Long = 10000
    internal val intervalCountDown: Long = 1000

    internal var timeLeftOnTimer: Long = 10000

    internal lateinit var buttonTapMe: Button
    internal lateinit var textGameScore: TextView
    internal lateinit var textTimeLeft: TextView

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
        private const val GAME_STARTED_KEY = "GAME_STARTED_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Imprimimos por consola el siguiente mensaje para debuguear
        Log.d(TAG, "funtion onCreate called. Score is $score")

        buttonTapMe =  findViewById(R.id.buttonTapMe)
        textGameScore = findViewById(R.id.textGameScore)
        textTimeLeft = findViewById(R.id.textTimeLeft)

        buttonTapMe.setOnClickListener {view ->
            //view.alpha = 0.5f
            //view.setBackgroundColor(Color.CYAN)
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
            view.startAnimation(bounceAnimation)
            textGameScore.startAnimation(blinkAnimation)

            incrementScore()
            startCountDown()
        }

        if(savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            gameStarted = savedInstanceState.getBoolean(GAME_STARTED_KEY)
            restoreGame()

        }else{
            resetGame()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.menuAbout){
            showInfo()
        }
        return true
    }

    private fun showInfo() {

        val dialogTitle = getString(R.string.aboutTitle); BuildConfig.VERSION_NAME
        val dialogMessageQueue = getString(R.string.aboutMessage)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessageQueue)
        builder.create().show()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        outState.putBoolean(GAME_STARTED_KEY, gameStarted)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState called. Saving the score: $score,Time left: $timeLeftOnTimer and game started: $gameStarted")
    }

    override fun onDestroy(){
        super.onDestroy()

        Log.d(TAG, "onDestroy called")
    }

    private fun restoreGame(){
        val restoreTimeLeft = timeLeftOnTimer / 1000

        textGameScore.text = getString(R.string.gameScore, score)
        textTimeLeft.text = getString(R.string.timeLeft, restoreTimeLeft)

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, intervalCountDown){

            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished/1000
                textTimeLeft.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }

        if (gameStarted){
            countDownTimer.start()
        }else {
            resetGame()
        }

    }

    private fun resetGame(){
        score = 0
        val initialTimeLeft = initialCountDown/1000

        textGameScore.text = getString(R.string.gameScore, score)
        textTimeLeft.text = getString(R.string.timeLeft, initialTimeLeft)

        //buttonTapMe.text = getString(R.string.tapMe)
        //buttonTapMe.alpha = 1f
        //buttonTapMe.setBackgroundColor(Color.GRAY)

        countDownTimer = object : CountDownTimer(initialCountDown, intervalCountDown){

            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished/1000
                textTimeLeft.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false
    }

    private fun startCountDown(){

        if (!gameStarted){
            gameStarted = true
            countDownTimer.start()
            //buttonTapMe.text = getString(R.string.faster)
        }
    }

    private fun incrementScore(){
        score += 1
        val newScore = getString(R.string.gameScore, score)
        textGameScore.text = newScore
    }

    private fun endGame(){
        Toast.makeText(this, getString(R.string.scoreResult, score), Toast.LENGTH_LONG).show()
        resetGame()
    }
}