package com.example.guru2.timer

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R
import java.util.concurrent.TimeUnit

class TimerActivity : AppCompatActivity() {

    private var timeCountInMilliSeconds: Long = 90 * 1000
    private var initialTimeInMillis: Long = 90 * 1000
    private var timerStatus = TimerStatus.STOPPED

    private lateinit var progressBarCircle: ProgressBar
    private lateinit var timerText: TextView
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var resetButton: ImageButton
    private lateinit var backButton: ImageView

    private var countDownTimer: CountDownTimer? = null

    private enum class TimerStatus {
        STARTED, STOPPED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        // Intent에서 시간 데이터 가져오기
        intent.getLongExtra("timeInMillis", 90 * 1000).let {
            timeCountInMilliSeconds = it
            initialTimeInMillis = it
        }

        // UI 요소 초기화
        initViews()
        initListeners()

        // ProgressBar 초기화
        setProgressBarValues()
        updateTimerText()
    }

    private fun initViews() {
        timerText = findViewById(R.id.timerText)
        progressBarCircle = findViewById(R.id.progressBarCircle)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        resetButton = findViewById(R.id.resetButton)
        backButton = findViewById(R.id.backArrow)
    }

    private fun initListeners() {
        resetButton.setOnClickListener { reset() }
        playButton.setOnClickListener { startTimer() }
        pauseButton.setOnClickListener { pauseTimer() }
        backButton.setOnClickListener {
            finish()
        }
    }


    private fun startTimer() {
        if (timerStatus == TimerStatus.STARTED) return

        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 10) {
            override fun onTick(millisUntilFinished: Long) {
                timeCountInMilliSeconds = millisUntilFinished
                updateTimerText()
                updateProgressBar()
            }

            override fun onFinish() {
                timerStatus = TimerStatus.STOPPED
                updateTimerText()
                progressBarCircle.progress = 0
                progressBarCircle.invalidate()
            }
        }.start()

        timerStatus = TimerStatus.STARTED
    }

    private fun pauseTimer() {
        if (timerStatus == TimerStatus.STOPPED) return
        countDownTimer?.cancel()
        timerStatus = TimerStatus.STOPPED
    }

    private fun reset() {
        countDownTimer?.cancel()
        timeCountInMilliSeconds = initialTimeInMillis
        timerStatus = TimerStatus.STOPPED
        updateTimerText()
        setProgressBarValues()
    }

    private fun updateTimerText() {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeCountInMilliSeconds) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeCountInMilliSeconds) % 60
        timerText.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateProgressBar() {
        val progress = ((timeCountInMilliSeconds.toFloat() / initialTimeInMillis) * 1000).toInt()
        progressBarCircle.post {
            progressBarCircle.progress = progress / 10
            progressBarCircle.invalidate()
        }
    }

    private fun setProgressBarValues() {
        progressBarCircle.max = 100
        progressBarCircle.progress = 100
    }
}
