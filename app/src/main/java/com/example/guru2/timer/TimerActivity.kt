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

    private var timeCountInMilliSeconds: Long = 90 * 1000 // 기본값 90초
    private var timerStatus = TimerStatus.STOPPED

    private lateinit var progressBarCircle: ProgressBar
    private lateinit var timerText: TextView
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var resetButton: ImageButton
    private lateinit var backButton: ImageView  // ✅ 뒤로가기 버튼 추가

    private var countDownTimer: CountDownTimer? = null

    private enum class TimerStatus {
        STARTED, STOPPED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        // UI 요소 초기화
        initViews()
        initListeners()

        // ProgressBar 초기화
        setProgressBarValues()
    }

    private fun initViews() {
        timerText = findViewById(R.id.timerText)
        progressBarCircle = findViewById(R.id.progressBarCircle)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        resetButton = findViewById(R.id.resetButton)
        backButton = findViewById(R.id.backArrow) as ImageView // ✅ 뒤로가기 버튼 ID 추가
    }

    private fun initListeners() {
        resetButton.setOnClickListener { reset() }
        playButton.setOnClickListener { startTimer() }
        pauseButton.setOnClickListener { pauseTimer() }
        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }  // ✅ 뒤로가기 버튼 클릭 시 이전 화면으로 이동
    }

    /**
     * 타이머 시작
     */
    private fun startTimer() {
        if (timerStatus == TimerStatus.STARTED) return // 이미 실행 중이면 아무 동작 안 함

        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeCountInMilliSeconds = millisUntilFinished
                updateTimerText()
                updateProgressBar()
            }

            override fun onFinish() {
                timerStatus = TimerStatus.STOPPED
                updateTimerText()
                progressBarCircle.progress = 0
            }
        }.start()

        timerStatus = TimerStatus.STARTED
    }

    /**
     * 타이머 일시정지
     */
    private fun pauseTimer() {
        if (timerStatus == TimerStatus.STOPPED) return // 이미 정지 상태면 아무 동작 안 함

        countDownTimer?.cancel()
        timerStatus = TimerStatus.STOPPED
    }

    /**
     * 타이머 리셋
     */
    private fun reset() {
        countDownTimer?.cancel()
        timeCountInMilliSeconds = 90 * 1000
        timerStatus = TimerStatus.STOPPED
        updateTimerText()
        setProgressBarValues()
    }

    /**
     * 타이머 시간 UI 업데이트
     */
    private fun updateTimerText() {
        val hours = TimeUnit.MILLISECONDS.toHours(timeCountInMilliSeconds)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeCountInMilliSeconds) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeCountInMilliSeconds) % 60
        timerText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * 프로그레스바 업데이트
     */
    private fun updateProgressBar() {
        val progress = ((timeCountInMilliSeconds.toFloat() / 90000) * 100).toInt()
        progressBarCircle.post { progressBarCircle.progress = progress }
    }

    /**
     * 프로그레스바 초기화
     */
    private fun setProgressBarValues() {
        progressBarCircle.max = 100
        progressBarCircle.progress = 100
    }
}
