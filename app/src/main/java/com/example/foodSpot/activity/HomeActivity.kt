package com.example.foodSpot.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodSpot.R
import com.example.foodSpot.fragments.*
import com.google.android.material.navigation.NavigationView


class HomeActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var txtUserName: TextView
    lateinit var txtMobile: TextView
    lateinit var shared: SharedPreferences
    var previousMenuItem: MenuItem? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationView)
        txtUserName = navigationView.getHeaderView(0).findViewById(R.id.txtUserName)
        txtMobile = navigationView.getHeaderView(0).findViewById(R.id.txtMobile)

        setUpToolbar()
        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@HomeActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        shared = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE)
        txtUserName.text = shared.getString("Name", "FoodSpot")
        txtMobile.text = "+91-" + shared.getString("Mobile", "9998887777")

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.homePage -> {
                    drawerLayout.closeDrawers()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            openHome()
                        },
                        300
                    )
                }
                R.id.profile -> {
                    drawerLayout.closeDrawers()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frame, ProfileFragment())
                                .commit()
                            supportActionBar?.title = "Profile"
                        },
                        300
                    )
                }
                R.id.favouritePage -> {
                    drawerLayout.closeDrawers()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frame, FavouriteFragment())
                                .commit()
                            supportActionBar?.title = "Favourites"
                        },
                        300
                    )
                }
                R.id.orderHistory -> {
                    drawerLayout.closeDrawers()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frame, OrderHistoryFragment())
                                .commit()
                            supportActionBar?.title = "Order History"
                        },
                        300
                    )
                }
                R.id.faq -> {
                    drawerLayout.closeDrawers()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frame, FAQFragment())
                                .commit()
                            supportActionBar?.title = "FAQ"
                        },
                        300
                    )
                }
                R.id.log_out -> {
                    drawerLayout.closeDrawers()
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            val dialog = AlertDialog.Builder(this@HomeActivity)
                            dialog.setTitle("Confirmation")
                            dialog.setMessage("Are you sure you want to log out?")
                            dialog.setPositiveButton("YES") { text, listener ->
                                val intent = Intent(this@HomeActivity, LogInActivity::class.java)
                                shared.edit().putBoolean("IsLogin", false).apply()
                                startActivity(intent)
                                finish()
                            }
                            dialog.setNegativeButton("NO") { text, listener ->
                                //Do nothing
                            }
                            dialog.create()
                            dialog.show()
                        },
                        300
                    )
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    private fun openHome() {
        supportFragmentManager.beginTransaction().replace(R.id.frame, HomeFragment())
            .commit()
        navigationView.setCheckedItem(R.id.homePage)
        supportActionBar?.title = "Home"
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when (frag) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Home"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
}