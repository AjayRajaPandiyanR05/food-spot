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

class LogInActivity : AppCompatActivity() {

    lateinit var txtRegister: TextView
    lateinit var txtForgotPassword: TextView
    lateinit var btnLogIn: Button
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var toolbar: Toolbar
    lateinit var txtMobileError: TextView
    lateinit var txtPasswordError: TextView
    lateinit var shared: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        toolbar = findViewById(R.id.toolbar)
        txtRegister = findViewById(R.id.txtRegister)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        btnLogIn = findViewById(R.id.btnLogIn)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        txtMobileError = findViewById(R.id.txtMobileError)
        txtPasswordError = findViewById(R.id.txtPasswordError)
        shared = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Log In"

        txtRegister.setOnClickListener {
            val intent = Intent(this@LogInActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }

        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LogInActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        btnLogIn.setOnClickListener {

            if (etMobileNumber.text.toString() == "" || etPassword.text.toString() == "") {
                Toast.makeText(applicationContext, "Fields cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                txtMobileError.text = ""
                txtPasswordError.text = ""
            } else if (etMobileNumber.text.toString().length < 10) {
                txtMobileError.text = "Mobile Number should contain 10 digits"
                txtPasswordError.text = ""
            } else if (etPassword.text.toString().length < 4) {
                txtMobileError.text = ""
                txtPasswordError.text = "Password must have min. of 4 characters"
            } else {
                txtMobileError.text = ""
                txtPasswordError.text = ""

                fetchData()
            }
        }
    }

    private fun fetchData() {
        if (ConnectionManager().checkConnectivity(applicationContext)) {
            val queue = Volley.newRequestQueue(this@LogInActivity)
            val url = "http://13.235.250.119/v2/login/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", etMobileNumber.text.toString())
            jsonParams.put("password", etPassword.text.toString())

            val jsonRequest = object :
                JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {

                            val userData = data.getJSONObject("data")
                            val userId = userData.getString("user_id")
                            val userName = userData.getString("name")
                            val userEmail = userData.getString("email")
                            val userMobile = userData.getString("mobile_number")
                            val userAddress = userData.getString("address")

                            shared.edit().putString("Id", userId).apply()
                            shared.edit().putString("Name", userName).apply()
                            shared.edit().putString("Email", userEmail).apply()
                            shared.edit().putString("Mobile", userMobile).apply()
                            shared.edit().putString("Address", userAddress).apply()

                            shared.edit().putBoolean("IsLogin", true).apply()

                            val intent =
                                Intent(this@LogInActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()

                            Toast.makeText(
                                applicationContext,
                                "Log in successful",
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
}