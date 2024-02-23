package com.example.foodSpot.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodSpot.R
import com.example.foodSpot.adapter.OrderHistoryRecyclerAdapter
import com.example.foodSpot.model.OrderHistory
import com.example.foodSpot.util.ConnectionManager
import com.google.android.material.button.MaterialButton
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderHistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerOderHistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    lateinit var shared: SharedPreferences
    lateinit var noInternetLayout: RelativeLayout
    lateinit var progressBarLayout: RelativeLayout
    lateinit var btnRetry: Button
    lateinit var btnExit: MaterialButton
    lateinit var refresh: SwipeRefreshLayout
    lateinit var noResultsLayout: RelativeLayout
    var orderHistoryList: ArrayList<OrderHistory> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerOderHistory = view.findViewById(R.id.recyclerOrderHistory)
        layoutManager = LinearLayoutManager(activity as Context)
        shared = requireActivity().getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE)
        noInternetLayout = view.findViewById(R.id.noInternetLayout)
        progressBarLayout = view.findViewById(R.id.progressBarLayout)
        btnRetry = view.findViewById(R.id.btnRetry)
        noResultsLayout = view.findViewById(R.id.noResultsLayout)
        btnExit = view.findViewById(R.id.btnExit)
        refresh = view.findViewById(R.id.refresh)

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
        noResultsLayout.visibility = View.INVISIBLE
        fetchData()

        return view
    }

    private fun fetchData() {
        val userId = shared.getString("Id", "0")

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            noInternetLayout.visibility = View.GONE

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progressBarLayout.visibility = View.GONE

                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        orderHistoryList.clear()
                        if (success) {
                            val resArray = data.getJSONArray("data")
                            if (resArray.length() == 0) {
                                progressBarLayout.visibility = View.GONE
                                noResultsLayout.visibility = View.VISIBLE
                            } else {
                                progressBarLayout.visibility = View.GONE
                                noResultsLayout.visibility = View.GONE
                                for (i in 0 until resArray.length()) {
                                    val orderHistoryJsonObject = resArray.getJSONObject(i)

                                    val orderHistoryObject = OrderHistory(
                                        orderHistoryJsonObject.getString("order_id"),
                                        orderHistoryJsonObject.getString("restaurant_name"),
                                        orderHistoryJsonObject.getString("total_cost"),
                                        orderHistoryJsonObject.getString("order_placed_at"),
                                        userId
                                    )
                                    orderHistoryList.add(orderHistoryObject)
                                    recyclerAdapter = OrderHistoryRecyclerAdapter(
                                        activity as Context,
                                        orderHistoryList
                                    )
                                    recyclerOderHistory.adapter = recyclerAdapter
                                    recyclerOderHistory.layoutManager = layoutManager
                                }
                            }
                        } else {
                            val errorMessage = data.getString("errorMessage")
                            Toast.makeText(
                                activity as Context,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        if (activity != null) {
                            Toast.makeText(
                                activity as Context,
                                "Some unexpected error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
            recyclerAdapter = OrderHistoryRecyclerAdapter(
                activity as Context,
                orderHistoryList
            )
            recyclerOderHistory.adapter = recyclerAdapter
            recyclerOderHistory.layoutManager = layoutManager
            progressBarLayout.visibility = View.GONE
            noInternetLayout.visibility = View.VISIBLE

            Toast.makeText(
                activity as Context,
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
                ActivityCompat.finishAffinity(activity as Activity)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderHistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}