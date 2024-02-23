package com.example.foodSpot.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import com.example.foodSpot.R

class OrderPlacedActivity : AppCompatActivity() {

    lateinit var successLayout: RelativeLayout
    lateinit var btnOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)

        successLayout = findViewById(R.id.successLayout)
        btnOk = findViewById(R.id.btnOk)

        btnOk.setOnClickListener {
            val intent = Intent(this@OrderPlacedActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
