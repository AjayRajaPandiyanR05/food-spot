package com.example.foodSpot.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.foodSpot.R
import com.example.foodSpot.adapter.FavouriteRecyclerAdapter
import com.example.foodSpot.databases.RestaurantDatabase
import com.example.foodSpot.databases.RestaurantEntity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavouriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerFavourite: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    lateinit var progressBarLayout: RelativeLayout
    lateinit var noResultsLayout: RelativeLayout
    lateinit var refresh: SwipeRefreshLayout

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
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        layoutManager = LinearLayoutManager(activity)
        progressBarLayout = view.findViewById(R.id.progressBarLayout)
        noResultsLayout = view.findViewById(R.id.noResultsLayout)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavouriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun fetchData() {
        val dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()

        if (dbRestaurantList.isEmpty()) {
            progressBarLayout.visibility = View.GONE
            noResultsLayout.visibility = View.VISIBLE
        } else {
            if (dbRestaurantList != null && activity != null) {
                progressBarLayout.visibility = View.GONE
                noResultsLayout.visibility = View.GONE

                recyclerAdapter =
                    FavouriteRecyclerAdapter(activity as Context, dbRestaurantList)
                recyclerFavourite.adapter = recyclerAdapter
                recyclerFavourite.layoutManager = layoutManager
            }
        }
    }

    class RetrieveFavourites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db")
                .build()
            return db.restaurantDao().getAllRestaurant()
        }
    }
}