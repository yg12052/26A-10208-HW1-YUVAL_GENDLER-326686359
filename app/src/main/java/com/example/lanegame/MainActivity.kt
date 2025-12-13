package com.example.lanegame

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var gameArea: android.widget.FrameLayout
    private var currentLane = Lane.CENTER
    private lateinit var car: ImageView
    private lateinit var btnLeft: ImageButton
    private lateinit var btnRight: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        gameArea = findViewById(R.id.gameArea)
        gameArea.post {
            updateCarPosition()
        }

        car = findViewById(R.id.car)
        btnLeft = findViewById(R.id.btnLeft)
        btnRight = findViewById(R.id.btnRight)

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


}
