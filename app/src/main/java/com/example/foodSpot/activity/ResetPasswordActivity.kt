package com.example.foodSpot.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodSpot.R
import com.example.foodSpot.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var btnReset: Button
    lateinit var etChoosePassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var toolbar: Toolbar
    lateinit var etOTP: EditText
    lateinit var txtOTPError: TextView
    lateinit var txtPasswordError: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        toolbar = findViewById(R.id.toolbar)
        btnReset = findViewById(R.id.btnReset)
        etChoosePassword = findViewById(R.id.etChoosePassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        txtPasswordError = findViewById(R.id.txtPasswordError)
        etOTP = findViewById(R.id.etOTP)
        txtOTPError = findViewById(R.id.txtOTPError)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Reset Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        btnReset.setOnClickListener {
            if (etChoosePassword.text.toString() == "" || etConfirmPassword.text.toString() == "" || etOTP.text.toString() == "") {
                Toast.makeText(applicationContext, "Fields cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                txtPasswordError.text = ""
                txtOTPError.text = ""
            } else if (etOTP.text.toString().length < 4) {
                txtPasswordError.text = ""
                txtOTPError.text = "OTP field should contain 4 characters"
            } else if (etChoosePassword.text.toString().length < 4) {
                txtPasswordError.text = "Password must have min. of 4 characters"
                txtOTPError.text = ""
            } else if (etChoosePassword.text.toString() != etConfirmPassword.text.toString()) {
                Toast.makeText(applicationContext, "Passwords doesn't match", Toast.LENGTH_SHORT)
                    .show()
                txtPasswordError.text = ""
                txtOTPError.text = ""
            } else {
                txtPasswordError.text = ""
                txtOTPError.text = ""

                fetchData()
            }
        }
    }

    private fun fetchData() {
        if (ConnectionManager().checkConnectivity(applicationContext)) {
            val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
            val url = "http://13.235.250.119/v2/reset_password/fetch_result"

            val bundle = intent.extras
            val mobileNumber = bundle?.getString("mobile_number")

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobileNumber)
            jsonParams.put("password", etChoosePassword.text.toString())
            jsonParams.put("otp", etOTP.text.toString())

            val jsonRequest = object :
                JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val successMessage = data.getString("successMessage")
                            val intent = Intent(
                                this@ResetPasswordActivity,
                                LogInActivity::class.java
                            )
                            startActivity(intent)
                            finish()

                            Toast.makeText(
                                applicationContext,
                                successMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val errorMessage = data.getString("errorMessage")
                            Toast.makeText(
                                applicationContext,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            applicationContext,
                            "Some unexpected error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        applicationContext,
                        "Some unexpected error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val header = HashMap<String, String>()
                    header["Content-type"] = "application/json"
                    header["token"] = "b00ba8ac540168"
                    return header
                }
            }
            queue.add(jsonRequest)
        } else {
            Toast.makeText(applicationContext, "No internet connection", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val intent = Intent(this@ResetPasswordActivity, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }
}