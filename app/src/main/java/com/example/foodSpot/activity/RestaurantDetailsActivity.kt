package com.example.foodSpot.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
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
import com.example.foodSpot.adapter.RestaurantDetailsRecyclerAdapter
import com.example.foodSpot.model.RestaurantDetails
import com.example.foodSpot.util.ConnectionManager
import com.google.android.material.button.MaterialButton
import org.json.JSONException

class RestaurantDetailsActivity : AppCompatActivity() {

    lateinit var recyclerRestaurantDetails: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantDetailsRecyclerAdapter
    lateinit var toolbar: Toolbar
    lateinit var noInternetLayout: RelativeLayout
    lateinit var progressBarLayout: RelativeLayout
    lateinit var btnProceedToCart: Button
    lateinit var btnRetry: Button
    lateinit var btnExit: MaterialButton
    lateinit var refresh: SwipeRefreshLayout

    var restaurantList = arrayListOf<RestaurantDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        toolbar = findViewById(R.id.toolbar)
        recyclerRestaurantDetails = findViewById(R.id.recyclerRestaurantDetails)
        layoutManager = LinearLayoutManager(this@RestaurantDetailsActivity)
        noInternetLayout = findViewById(R.id.noInternetLayout)
        progressBarLayout = findViewById(R.id.progressBarLayout)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        btnRetry = findViewById(R.id.btnRetry)
        btnExit = findViewById(R.id.btnExit)
        refresh = findViewById(R.id.refresh)

        refresh.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    refresh.isRefreshing = false
                    fetchData()
                },
                1000
            )
        }

        progressBarLayout.visibility = View.VISIBLE
        btnProceedToCart.visibility = View.INVISIBLE

        val restaurantName = intent.getStringExtra("restaurant_name")

        setSupportActionBar(toolbar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        fetchData()
    }

    private fun fetchData() {

        val restaurantId = intent.getStringExtra("restaurant_id")
        val restaurantName = intent.getStringExtra("restaurant_name")

        val queue = Volley.newRequestQueue(this@RestaurantDetailsActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

        if (ConnectionManager().checkConnectivity(applicationContext)) {
            noInternetLayout.visibility = View.GONE
            btnProceedToCart.visibility = View.VISIBLE

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progressBarLayout.visibility = View.GONE

                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        restaurantList.clear()
                        if (success) {
                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val restaurantJsonObject = resArray.getJSONObject(i)

                                val restaurantObject = RestaurantDetails(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("restaurant_id")
                                )
                                restaurantList.add(restaurantObject)
                                recyclerAdapter =
                                    RestaurantDetailsRecyclerAdapter(
                                        this@RestaurantDetailsActivity,
                                        restaurantId,
                                        restaurantName,
                                        btnProceedToCart,
                                        restaurantList
                                    )
                                recyclerRestaurantDetails.adapter = recyclerAdapter
                                recyclerRestaurantDetails.layoutManager = layoutManager
                            }
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
                RestaurantDetailsRecyclerAdapter(
                    this@RestaurantDetailsActivity,
                    restaurantId,
                    restaurantName,
                    btnProceedToCart,
                    restaurantList
                )
            recyclerRestaurantDetails.layoutManager = layoutManager
            recyclerRestaurantDetails.adapter = recyclerAdapter

            btnProceedToCart.visibility = View.INVISIBLE
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
                ActivityCompat.finishAffinity(this@RestaurantDetailsActivity)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}