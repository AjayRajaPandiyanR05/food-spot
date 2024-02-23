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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodSpot.R
import com.example.foodSpot.util.ConnectionManager
import com.android.volley.Response
import org.json.JSONException
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity() {

    lateinit var btnRegister: Button
    lateinit var etEnterName: EditText
    lateinit var etEmailAddress: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etDelivery: EditText
    lateinit var etChoosePassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var txtNameError: TextView
    lateinit var txtPasswordError: TextView
    lateinit var toolbar: Toolbar
    lateinit var shared: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        toolbar = findViewById(R.id.toolbar)
        btnRegister = findViewById(R.id.btnRegister)
        etEnterName = findViewById(R.id.etEnterName)
        etEmailAddress = findViewById(R.id.etEmailAddress)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etDelivery = findViewById(R.id.etDelivery)
        etChoosePassword = findViewById(R.id.etChoosePassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        txtNameError = findViewById(R.id.txtNameError)
        txtPasswordError = findViewById(R.id.txtPasswordError)
        shared = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Sign Up"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        btnRegister.setOnClickListener {

            if (etEnterName.text.toString() == "" || etEmailAddress.text.toString() == "" ||
                etMobileNumber.text.toString() == "" || etDelivery.text.toString() == "" ||
                etChoosePassword.text.toString() == "" || etConfirmPassword.text.toString() == ""
            ) {
                Toast.makeText(applicationContext, "Fields cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                txtNameError.text = ""
                txtPasswordError.text = ""
            } else if (etEnterName.text.toString().length < 3) {
                txtNameError.text = "Name must have min. of 3 characters"
                txtPasswordError.text = ""
            } else if (etChoosePassword.text.toString().length < 4) {
                txtPasswordError.text = "Password must have min. of 4 characters"
                txtNameError.text = ""
            } else if (etChoosePassword.text.toString() != etConfirmPassword.text.toString()) {
                Toast.makeText(applicationContext, "Passwords doesn't match", Toast.LENGTH_SHORT)
                    .show()
                txtNameError.text = ""
                txtPasswordError.text = ""
            } else {
                txtNameError.text = ""
                txtPasswordError.text = ""

                fetchData()
            }
        }
    }

    private fun fetchData() {
        if (ConnectionManager().checkConnectivity(applicationContext)) {
            val queue = Volley.newRequestQueue(this@RegistrationActivity)
            val url = "http://13.235.250.119/v2/register/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("name", etEnterName.text.toString())
            jsonParams.put("mobile_number", etMobileNumber.text.toString())
            jsonParams.put("password", etChoosePassword.text.toString())
            jsonParams.put("address", etDelivery.text.toString())
            jsonParams.put("email", etEmailAddress.text.toString())

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

                            val intent =
                                Intent(this@RegistrationActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()

                            Toast.makeText(
                                applicationContext,
                                "Successfully Registered",
                                Toast.LENGTH_SHORT
                            )
                                .show()
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