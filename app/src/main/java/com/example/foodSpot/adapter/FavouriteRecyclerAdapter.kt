package com.example.foodSpot.adapter

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
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodSpot.R
import com.example.foodSpot.activity.RestaurantDetailsActivity
import com.example.foodSpot.databases.RestaurantDatabase
import com.example.foodSpot.databases.RestaurantEntity
import com.example.foodSpot.model.Restaurant
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context, val itemList: List<RestaurantEntity>) :
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val imgRestaurant: ImageView = view.findViewById(R.id.imgRestaurant)
        val imgFavourite: ImageView = view.findViewById(R.id.imgFavourite)
        val cdView: CardView = view.findViewById(R.id.cdView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.favourite_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName

        holder.txtPrice.text = ""
        holder.txtPrice.append("Rs.")
        holder.txtPrice.append(restaurant.restaurantPrice)
        holder.txtPrice.append("/person")

        holder.txtRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.default_restaurant)
            .into(holder.imgRestaurant)
        holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_24)

        holder.cdView.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("restaurant_id", restaurant.restaurant_id.toString())
            intent.putExtra("restaurant_name", restaurant.restaurantName)
            context.startActivity(intent)
        }

        val restaurantEntity = RestaurantEntity(
            restaurant.restaurant_id,
            restaurant.restaurantName,
            restaurant.restaurantRating,
            restaurant.restaurantPrice,
            restaurant.restaurantImage
        )

        val checkFav = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()
        if (isFav)
            holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_24)
        else
            holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24)

        holder.imgFavourite.setOnClickListener {
            if (!HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show()
                    holder.imgFavourite.setImageResource(R.drawable.ic_baseline_favorite_24)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 3).execute()
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