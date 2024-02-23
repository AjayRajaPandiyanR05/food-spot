package com.example.foodSpot.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodSpot.R
import com.example.foodSpot.adapter.HomeRecyclerAdapter
import com.example.foodSpot.model.Restaurant
import com.example.foodSpot.util.ConnectionManager
import com.google.android.material.button.MaterialButton
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var noInternetLayout: RelativeLayout
    lateinit var progressBarLayout: RelativeLayout
    lateinit var btnRetry: Button
    lateinit var btnExit: MaterialButton
    lateinit var refresh: SwipeRefreshLayout

    val restaurantList: ArrayList<Restaurant> = arrayListOf()

    private val nameComparator = Comparator<Restaurant> { restaurant1, restaurant2 ->
        restaurant1.restaurantName.compareTo(restaurant2.restaurantName, true)
    }

    private val ratingComparator = Comparator<Restaurant> { restaurant1, restaurant2 ->
        restaurant1.rating.compareTo(restaurant2.rating, true)
    }

    private val costComparator = Comparator<Restaurant> { restaurant1, restaurant2 ->
        restaurant1.price.compareTo(restaurant2.price, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ActivityCompat.finishAffinity(activity as Activity)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        noInternetLayout = view.findViewById(R.id.noInternetLayout)
        progressBarLayout = view.findViewById(R.id.progressBarLayout)
        btnRetry = view.findViewById(R.id.btnRetry)
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

        setHasOptionsMenu(true)
        progressBarLayout.visibility = View.VISIBLE

        fetchData()

        return view
    }

    private fun fetchData() {
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            noInternetLayout.visibility = View.GONE

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

                                val restaurantObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantList.add(restaurantObject)
                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, restaurantList)
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some unexpected error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
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
            recyclerAdapter =
                HomeRecyclerAdapter(activity as Context, restaurantList)
            recyclerHome.adapter = recyclerAdapter
            recyclerHome.layoutManager = layoutManager

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sort_menu, menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.name -> {
                Collections.sort(restaurantList, nameComparator)
            }
            R.id.rating -> {
                Collections.sort(restaurantList, ratingComparator)
                restaurantList.reverse()
            }
            R.id.costAsc -> {
                Collections.sort(restaurantList, costComparator)
            }
            R.id.costDesc -> {
                Collections.sort(restaurantList, costComparator)
                restaurantList.reverse()
            }
        }

        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

