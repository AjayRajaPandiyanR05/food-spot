package com.example.foodSpot.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodSpot.R
import com.example.foodSpot.model.CartItems

class CartRecyclerAdapter(val context: Context, val itemList: ArrayList<CartItems>) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishRate: TextView = view.findViewById(R.id.txtDishRate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val dish = itemList[position]
        holder.txtDishName.text = dish.dishName

        holder.txtDishRate.text = "Rs. ${dish.dishRate}"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}