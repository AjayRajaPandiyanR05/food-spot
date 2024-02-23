package com.example.foodSpot.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodSpot.R
import com.example.foodSpot.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var btnNext: Button
    lateinit var etMobileNumber: EditText
    lateinit var etEmailAddress: EditText
    lateinit var toolbar: Toolbar
    lateinit var txtMobileError: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        toolbar = findViewById(R.id.toolbar)
        btnNext = findViewById(R.id.btnNext)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmailAddress = findViewById(R.id.etEmailAddress)
        txtMobileError = findViewById(R.id.txtMobileError)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        btnNext.setOnClickListener {

            if (etMobileNumber.text.toString() == "" || etEmailAddress.text.toString() == "") {
                Toast.makeText(applicationContext, "Fields cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                txtMobileError.text = ""
            } else if (etMobileNumber.text.toString().length < 10) {
                txtMobileError.text = "Mobile Number should contain 10 digits"
            } else {
                txtMobileError.text = ""

                fetchData()
            }
        }
    }

    private fun fetchData() {
        if (ConnectionManager().checkConnectivity(applicationContext)) {
            val queue = Volley.newRequestQueue(applicationContext)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", etMobileNumber.text.toString())
            jsonParams.put("email", etEmailAddress.text.toString())

            val jsonRequest = object :
                JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val intent = Intent(
                                this@ForgotPasswordActivity,
                                ResetPasswordActivity::class.java
                            )

                            val bundle = bundleOf()
                            bundle.putString(
                                "mobile_number",
                                etMobileNumber.text.toString()
                            )
                            intent.putExtras(bundle)
                            startActivity(intent)

                            Toast.makeText(
                                applicationContext,
                                "If you need to reset the password multiple times within 24 hours kindly use the same OTP",
                                Toast.LENGTH_LONG
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
}