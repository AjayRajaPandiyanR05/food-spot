package com.example.foodSpot.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodSpot.R
import com.example.foodSpot.model.CartItems
import com.example.foodSpot.model.OrderHistory
import com.example.foodSpot.util.ConnectionManager
import org.json.JSONException

class OrderHistoryRecyclerAdapter(val context: Context, var itemList: ArrayList<OrderHistory>) :
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val total: TextView = view.findViewById(R.id.txtTotal)
        var recyclerDishList: RecyclerView = view.findViewById(R.id.recyclerDishList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_single_row, parent, false)
        return OrderHistoryViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val order = itemList[position]
        holder.txtRestaurantName.text = order.restaurantName
        holder.txtDate.text = order.date.substring(0, 9)
        holder.total.text = """Total : Rs. ${order.totalCost}"""

        var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        lateinit var recyclerAdapter: CartRecyclerAdapter
        var cartListItems = arrayListOf<CartItems>()
        val userId = order.userId

        val queue = Volley.newRequestQueue(context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        if (ConnectionManager().checkConnectivity(context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")
                        if (success) {
                            val data = response.getJSONArray("data")
                            val restaurantObjectRetrieved = data.getJSONObject(position)
                            cartListItems.clear()
                            val foodOrdered = restaurantObjectRetrieved.getJSONArray("food_items")

                            for (j in 0 until foodOrdered.length()) {
                                val eachFoodItem = foodOrdered.getJSONObject(j)
                                val itemObject = CartItems(
                                    eachFoodItem.getString("food_item_id"),
                                    eachFoodItem.getString("name"),
                                    eachFoodItem.getString("cost"), "000"
                                )

                                cartListItems.add(itemObject)
                            }

                            recyclerAdapter =
                                CartRecyclerAdapter(
                                    context,
                                    cartListItems
                                )
                            holder.recyclerDishList.adapter = recyclerAdapter
                            holder.recyclerDishList.layoutManager = layoutManager
                        }
                    } catch (e: JSONException) {

                    }
                }, Response.ErrorListener {

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val header = HashMap<String, String>()
                        header["Content-type"] = "application/json"
                        header["token"] = "b00ba8ac540168"
                        return header
                    }
                }
            queue.add(jsonObjectRequest)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}