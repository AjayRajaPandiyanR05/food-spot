package com.example.foodSpot.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodSpot.R
import com.example.foodSpot.activity.RestaurantDetailsActivity
import com.example.foodSpot.databases.RestaurantDatabase
import com.example.foodSpot.databases.RestaurantEntity
import com.example.foodSpot.model.Restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val imgRestaurant: ImageView = view.findViewById(R.id.imgRestaurant)
        val imgFavourite: ImageView = view.findViewById(R.id.imgFavourite)
        val cdView: CardView = view.findViewById(R.id.cdView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]

        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtPrice.text = "Rs. ${restaurant.price}/person"
        holder.txtRating.text = restaurant.rating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.default_restaurant)
            .into(holder.imgRestaurant)
        holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24)

        holder.cdView.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("restaurant_id", restaurant.restaurantId)
            intent.putExtra("restaurant_name", restaurant.restaurantName)
            context.startActivity(intent)
        }

        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId.toInt(),
            restaurant.restaurantName,
            restaurant.rating,
            restaurant.price,
            restaurant.restaurantImage
        )

        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav)
            holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_24)
        else
            holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24)

        holder.imgFavourite.setOnClickListener {

            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show()
                    holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_24)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show()
                    holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurant_id.toString())
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}