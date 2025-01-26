package com.example.guru2.timer

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R

class TimerActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var resetButton: Button
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 90 * 1000 // 초기 시간 90초 (1분 30초)
    private var isTimerRunning = false
    private var timeLeftAtPause: Long = 90 * 1000 // 타이머 멈출 때의 시간 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        // UI 요소 초기화
        timerText = findViewById(R.id.timerText)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        resetButton = findViewById(R.id.resetButton)

        // 타이머 초기 값 표시
        updateTimerText()

        // 재생 버튼 클릭 리스너
        playButton.setOnClickListener {
            if (!isTimerRunning) {
                startTimer(timeLeftAtPause) // 타이머 시작
            }
        }

        // 멈춤 버튼 클릭 리스너
        pauseButton.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer() // 타이머 멈춤
            }
        }

        // 돌아가기 버튼 클릭 리스너
        resetButton.setOnClickListener {
            resetTimer() // 타이머 리셋
        }
    }

    private fun startTimer(startTime: Long) {
        countDownTimer = object : CountDownTimer(startTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                isTimerRunning = false
                updateTimerText()
            }
        }.start()

        isTimerRunning = true
        playButton.isEnabled = false // 재생 버튼 비활성화
        pauseButton.isEnabled = true // 멈춤 버튼 활성화
        resetButton.isEnabled = true // 돌아가기 버튼 활성화
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        timeLeftAtPause = timeLeftInMillis
        isTimerRunning = false

        playButton.isEnabled = true // 재생 버튼 활성화
        pauseButton.isEnabled = false // 멈춤 버튼 비활성화
        resetButton.isEnabled = true // 돌아가기 버튼 활성화
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = 90 * 1000 // 원래 시간으로 초기화 (1분 30초)
        timeLeftAtPause = 90 * 1000
        isTimerRunning = false
        updateTimerText()

        playButton.isEnabled = true // 재생 버튼 활성화
        pauseButton.isEnabled = false // 멈춤 버튼 비활성화
        resetButton.isEnabled = false // 돌아가기 버튼 비활성화
    }

    private fun updateTimerText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d:%02d", minutes, seconds, 0)
        timerText.text = timeFormatted
    }
}
