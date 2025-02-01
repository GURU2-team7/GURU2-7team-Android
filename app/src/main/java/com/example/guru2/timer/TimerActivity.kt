package com.example.guru2.timer

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R

class TimerActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var resetButton: ImageButton

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 90 * 1000 // 기본값 90초
    private var isTimerRunning = false
    private var initialTimeMillis: Long = 90 * 1000 // 초기 타이머 값 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        // UI 요소 초기화
        timerText = findViewById(R.id.timerText)
        progressBar = findViewById(R.id.progressBar)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        resetButton = findViewById(R.id.resetButton)

        // ✅ 순서 변경: indeterminate를 먼저 false로 설정한 후, progressDrawable 적용
        progressBar.isIndeterminate = false
        progressBar.progress = 100  // ✅ 초기 진행률 100% 유지
        progressBar.progressDrawable = resources.getDrawable(R.drawable.circular_progress, null)
        progressBar.visibility = View.VISIBLE

        val cookingTimeMillis = intent.getLongExtra("timeInMillis", 90 * 1000)
        if (cookingTimeMillis > 0) {
            timeLeftInMillis = cookingTimeMillis
            initialTimeMillis = cookingTimeMillis // 초기값 저장
        }

        updateTimerText()

        playButton.setOnClickListener {
            if (!isTimerRunning) {
                startTimer(timeLeftInMillis)
            }
        }

        pauseButton.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            }
        }

        resetButton.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer(startTime: Long) {
        countDownTimer?.cancel()
        isTimerRunning = true

        // ✅ ProgressBar가 줄어들지 않는 문제 해결
        progressBar.isIndeterminate = false  // ✅ indeterminate 해제
        progressBar.post {
            progressBar.progressDrawable = resources.getDrawable(R.drawable.circular_progress, null) // ✅ 스타일 적용
            progressBar.invalidate()  // ✅ 강제 UI 갱신
            progressBar.requestLayout()  // ✅ 레이아웃 다시 그리기
        }

        countDownTimer = object : CountDownTimer(startTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
                updateProgressBar() // ✅ 프로그레스 바 업데이트
            }

            override fun onFinish() {
                isTimerRunning = false
                updateTimerText()
                progressBar.post {
                    progressBar.progress = 0
                    progressBar.invalidate()
                    progressBar.requestLayout()
                }
            }
        }.start()

        updateProgressBar()
        playButton.isEnabled = false
        pauseButton.isEnabled = true
        resetButton.isEnabled = true
    }




    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        playButton.isEnabled = true
        pauseButton.isEnabled = false
        resetButton.isEnabled = true
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = initialTimeMillis
        isTimerRunning = false
        updateTimerText()

        progressBar.isIndeterminate = false  // ✅ indeterminate 제거
        progressBar.progress = 100  // ✅ 즉시 반영

        progressBar.post {
            progressBar.invalidate()  // ✅ UI 강제 갱신
            progressBar.requestLayout()  // ✅ 레이아웃 다시 그리기
        }

        playButton.isEnabled = true
        pauseButton.isEnabled = false
        resetButton.isEnabled = false

        Log.d("ProgressBar", "Reset progress to 100")
    }



    private fun updateTimerText() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        timerText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun updateProgressBar() {
        if (isTimerRunning) {
            val progress = ((timeLeftInMillis.toFloat() / initialTimeMillis.toFloat()) * 100).toInt()

            progressBar.post {
                progressBar.progress = progress.coerceIn(0, 100)  // ✅ 값 반영
                progressBar.invalidate()  // ✅ 강제 UI 갱신
                progressBar.requestLayout()  // ✅ 레이아웃 다시 그리기
            }

            Log.d("ProgressBar", "Updated progress: $progress")
        }
    }


}
