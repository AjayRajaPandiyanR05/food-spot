package com.example.foodSpot.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodSpot.R
import com.example.foodSpot.adapter.CartRecyclerAdapter
import com.example.foodSpot.model.CartItems
import com.example.foodSpot.util.ConnectionManager
import com.google.android.material.button.MaterialButton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var toolbar: Toolbar
    lateinit var txtRestaurantName: TextView
    lateinit var btnPlaceOrder: Button
    lateinit var shared: SharedPreferences
    lateinit var noInternetLayout: RelativeLayout
    lateinit var progressBarLayout: RelativeLayout
    lateinit var btnRetry: Button
    lateinit var btnExit: MaterialButton
    lateinit var refresh: SwipeRefreshLayout

    var totalAmount = 0
    var cartListItems = arrayListOf<CartItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbar = findViewById(R.id.toolbar)
        recyclerCart = findViewById(R.id.recyclerCart)
        layoutManager = LinearLayoutManager(this@CartActivity)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        txtRestaurantName = findViewById(R.id.txtRestaurantName)
        noInternetLayout = findViewById(R.id.noInternetLayout)
        progressBarLayout = findViewById(R.id.progressBarLayout)
        shared = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE)
        btnRetry = findViewById(R.id.btnRetry)
        btnExit = findViewById(R.id.btnExit)
        refresh = findViewById(R.id.refresh)

        btnPlaceOrder.visibility = View.INVISIBLE

        refresh.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    refresh.isRefreshing = false
                    fetchData()
                },
                1000
            )
        }

        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        progressBarLayout.visibility = View.VISIBLE

        val restaurantName = intent.getStringExtra("restaurantName")
        txtRestaurantName.text = restaurantName

        fetchData()

        btnPlaceOrder.setOnClickListener {
            sendData()
        }
    }

    private fun fetchData() {

        val restaurantId = intent.getStringExtra("restaurantId")
        val selectedItemsId = intent.getStringArrayListExtra("selectedItemsId")

        val queue = Volley.newRequestQueue(this@CartActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

        if (ConnectionManager().checkConnectivity(applicationContext)) {
            noInternetLayout.visibility = View.GONE
            btnPlaceOrder.visibility = View.VISIBLE

            val jsonObjectRequest =
                @SuppressLint("SetTextI18n")
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progressBarLayout.visibility = View.GONE
                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")

                        cartListItems.clear()
                        if (success) {
                            val data = response.getJSONArray("data")
                            totalAmount = 0

                            for (i in 0 until data.length()) {
                                val cartItem = data.getJSONObject(i)
                                if (selectedItemsId!!.contains(cartItem.getString("id"))) {
                                    val menuObject = CartItems(
                                        cartItem.getString("id"),
                                        cartItem.getString("name"),
                                        cartItem.getString("cost_for_one"),
                                        cartItem.getString("restaurant_id")
                                    )

                                    totalAmount += cartItem.getString("cost_for_one").toString()
                                        .toInt()
                                    cartListItems.add(menuObject)
                                    recyclerAdapter =
                                        CartRecyclerAdapter(
                                            this@CartActivity,
                                            cartListItems
                                        )
                                    recyclerCart.adapter = recyclerAdapter
                                    recyclerCart.layoutManager = layoutManager
                                }
                            }
                            btnPlaceOrder.text = "Place Order(Total: Rs. $totalAmount)"
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Some unexpected error occurred",
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
            queue.add(jsonObjectRequest)
        } else {
            recyclerAdapter =
                CartRecyclerAdapter(
                    this@CartActivity,
                    cartListItems
                )
            recyclerCart.adapter = recyclerAdapter
            recyclerCart.layoutManager = layoutManager

            btnPlaceOrder.visibility = View.INVISIBLE
            progressBarLayout.visibility = View.GONE
            noInternetLayout.visibility = View.VISIBLE
            Toast.makeText(
                applicationContext,
                "No internet connection",
                Toast.LENGTH_SHORT
            ).show()

            btnRetry.setOnClickListener {
                progressBarLayout.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        fetchData()
                    },
                    1000
                )
            }

            btnExit.setOnClickListener {
                ActivityCompat.finishAffinity(this@CartActivity)
            }
        }
    }

    private fun sendData() {

        val restaurantId = intent.getStringExtra("restaurantId")
        val selectedItemsId = intent.getStringArrayListExtra("selectedItemsId")

        if (ConnectionManager().checkConnectivity(applicationContext)) {
            noInternetLayout.visibility = View.GONE
            btnPlaceOrder.visibility = View.VISIBLE

            val foodArray = JSONArray()

            if (selectedItemsId != null) {
                for (foodItem in selectedItemsId) {
                    val singleItemObject = JSONObject()
                    singleItemObject.put("food_item_id", foodItem)
                    foodArray.put(singleItemObject)
                }
            }

            val jsonParams = JSONObject()
            jsonParams.put("user_id", shared.getString("Id", "0"))
            jsonParams.put("restaurant_id", restaurantId)
            jsonParams.put("total_cost", totalAmount)
            jsonParams.put("food", foodArray)

            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"

            val jsonObjectRequest = object :
                JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        progressBarLayout.visibility = View.GONE

                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")
                        if (success) {
                            Toast.makeText(
                                this,
                                "Order Placed",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val errorMessage =
                                response.getString("errorMessage")
                            Toast.makeText(
                                this,
                                errorMessage.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        val intent = Intent(this, OrderPlacedActivity::class.java)
                        startActivity(intent)
                        finish()

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
            queue.add(jsonObjectRequest)
        } else {
            btnPlaceOrder.visibility = View.INVISIBLE
            progressBarLayout.visibility = View.GONE
            noInternetLayout.visibility = View.VISIBLE

            Toast.makeText(
                applicationContext,
                "No internet connection",
                Toast.LENGTH_SHORT
            ).show()

            btnRetry.setOnClickListener {
                progressBarLayout.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        fetchData()
                    },
                    1000
                )
            }

            btnExit.setOnClickListener {
                ActivityCompat.finishAffinity(this@CartActivity)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}