package com.example.lanegame

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var gameArea: android.widget.FrameLayout
    private var currentLane = Lane.CENTER
    private lateinit var car: ImageView
    private lateinit var btnLeft: ImageButton
    private lateinit var btnRight: ImageButton
    private val gameTickDelay = 1000L
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val obstaclesQueue = ArrayDeque<ImageView>()
    private var tickCounter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        gameArea = findViewById(R.id.gameArea)
        car = findViewById(R.id.car)
        btnLeft = findViewById(R.id.btnLeft)
        btnRight = findViewById(R.id.btnRight)

        gameArea.post {
            updateCarPosition()
        }
        btnLeft.setOnClickListener {
            currentLane = when (currentLane) {
                Lane.RIGHT -> Lane.CENTER
                Lane.CENTER -> Lane.LEFT
                Lane.LEFT -> Lane.LEFT
            }
            updateCarPosition()
        }
        btnRight.setOnClickListener {
            currentLane = when (currentLane) {
                Lane.RIGHT -> Lane.RIGHT
                Lane.CENTER -> Lane.RIGHT
                Lane.LEFT -> Lane.CENTER
            }
            updateCarPosition()

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateCarPosition() {
        val areaWidth = gameArea.width.toFloat()
        val carWidth = car.width.toFloat()

        val leftX = 0f
        val centerX = (areaWidth - carWidth) / 2f
        val rightX = areaWidth - carWidth

        car.x = when (currentLane) {
            Lane.LEFT -> leftX
            Lane.CENTER -> centerX
            Lane.RIGHT -> rightX
        }
    }

    private val gameTick = object : Runnable {
        override fun run() {
            tickCounter++
            if (tickCounter == 3) {
                val obstacle = spawnObstacle()
                obstaclesQueue.addLast(obstacle)
                tickCounter = 0
            }
            for (ob in obstaclesQueue) {
                ob.y += 100f
            }
            if (obstaclesQueue.isNotEmpty()) {
                val firstObstacle = obstaclesQueue.first()
                if (firstObstacle.y  >= car.y + firstObstacle.height/2) {
                    gameArea.removeView(firstObstacle)
                    obstaclesQueue.removeFirst()
                    Log.d("LaneGame", "obstacle removed")
                }
            }
            handler.postDelayed(this, gameTickDelay)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(gameTick)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(gameTick)
    }

    private fun dp(dp: Float): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()

    private fun spawnObstacle(): ImageView {
        val obstacle = ImageView(this).apply {
            setImageResource(R.drawable.obstacle)
            layoutParams = FrameLayout.LayoutParams(dp(72f), dp(72f))
            scaleType = ImageView.ScaleType.FIT_CENTER
            contentDescription = "Obstacle"
        }

        val laneIndex = Random.nextInt(3)

        val xLeft = 0f
        val xCenter = ((gameArea.width - obstacle.layoutParams.width) / 2f)
        val xRight = (gameArea.width - obstacle.layoutParams.width).toFloat()

        obstacle.x = when (laneIndex) {
            0 -> xLeft
            1 -> xCenter
            else -> xRight
        }
        obstacle.y = 120f

        gameArea.addView(obstacle)
        return obstacle
    }


}
