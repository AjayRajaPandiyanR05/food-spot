package com.example.foodSpot.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodSpot.R
import com.example.foodSpot.activity.CartActivity
import com.example.foodSpot.model.RestaurantDetails


class RestaurantDetailsRecyclerAdapter(
    val context: Context,
    private val restaurantId: String?,
    private val restaurantName: String?,
    private val btnProceedToCart: Button,
    private val itemList: ArrayList<RestaurantDetails>
) :
    RecyclerView.Adapter<RestaurantDetailsRecyclerAdapter.RestaurantDetailsViewHolder>() {

    var itemsSelectedId = arrayListOf<String>()
    var itemSelectedCount: Int = 0

    class RestaurantDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSerialNumber: TextView = view.findViewById(R.id.txtSerialNumber)
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishRate: TextView = view.findViewById(R.id.txtDishRate)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantDetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_details_single_row, parent, false)
        return RestaurantDetailsViewHolder(view)
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onBindViewHolder(holder: RestaurantDetailsViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtSerialNumber.text = (position + 1).toString()
        holder.txtDishName.text = restaurant.dishName

        holder.txtDishRate.text = "Rs. ${restaurant.dishRate}"
        holder.btnAddToCart.tag = restaurant.dishId + ""

        btnProceedToCart.setOnClickListener {

            if (itemSelectedCount > 0) {
                val intent = Intent(context, CartActivity::class.java)
                intent.putExtra("restaurantId", restaurantId)
                intent.putExtra("restaurantName", restaurantName)
                intent.putExtra("selectedItemsId", itemsSelectedId)
                context.startActivity(intent)
            }
        }

        holder.btnAddToCart.setOnClickListener {

            if (holder.btnAddToCart.text.toString() == "Remove") {
                itemSelectedCount--
                println(itemsSelectedId.remove(holder.btnAddToCart.tag.toString()))
                holder.btnAddToCart.text = "Add"
                holder.btnAddToCart.setBackgroundColor(Color.rgb(171, 71, 188))
            } else {
                itemSelectedCount++
                println(itemsSelectedId.add(holder.btnAddToCart.tag.toString()))
                holder.btnAddToCart.text = "Remove"
                holder.btnAddToCart.setBackgroundColor(Color.rgb(255, 195, 0))
            }

            if (itemSelectedCount > 0) {
                btnProceedToCart.setBackgroundColor(Color.rgb(171, 71, 188))
            } else {
                btnProceedToCart.setBackgroundColor(Color.rgb(158, 158, 158))
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
