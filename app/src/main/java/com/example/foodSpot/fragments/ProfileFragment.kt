package com.example.foodSpot.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.example.foodSpot.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var shared: SharedPreferences
    lateinit var cdViewName: CardView
    lateinit var cdViewMobile: CardView
    lateinit var cdViewEmail: CardView
    lateinit var cdViewAddress: CardView
    lateinit var txtUserName: TextView
    lateinit var txtMobileNumber: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        Toast.makeText(activity as Context, "Click on the fields to see details", Toast.LENGTH_LONG)
            .show()

        shared = requireActivity().getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE)
        cdViewName = view.findViewById(R.id.cdViewName)
        cdViewMobile = view.findViewById(R.id.cdViewMobile)
        cdViewEmail = view.findViewById(R.id.cdViewEmail)
        cdViewAddress = view.findViewById(R.id.cdViewAddress)
        txtUserName = view.findViewById(R.id.txtUserName)
        txtMobileNumber = view.findViewById(R.id.txtMobileNumber)
        txtEmail = view.findViewById(R.id.txtMobile)
        txtAddress = view.findViewById(R.id.txtAddress)

        var checkName: Boolean = false
        cdViewName.setOnClickListener {
            checkName = if (!checkName) {
                showDetails(txtUserName, "Name", "User Name")
                true
            } else {
                showHeaders(txtUserName, "Name")
                false
            }
        }

        var checkMobile: Boolean = false
        cdViewMobile.setOnClickListener {
            checkMobile = if (!checkMobile) {
                showDetails(txtMobileNumber, "Mobile", "Mobile Number")
                true
            } else {
                showHeaders(txtMobileNumber, "Mobile Number")
                false
            }

        }

        var checkEmail: Boolean = false
        cdViewEmail.setOnClickListener {
            checkEmail = if (!checkEmail) {
                showDetails(txtEmail, "Email", "Email Address")
                true
            } else {
                showHeaders(txtEmail, "Email Address")
                false
            }

        }

        var checkAddress: Boolean = false
        cdViewAddress.setOnClickListener {
            checkAddress = if (!checkAddress) {
                showDetails(txtAddress, "Address", "Delivery Address")
                true
            } else {
                showHeaders(txtAddress, "Delivery Address")
                false
            }
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showDetails(textField: TextView, getField: String, putField: String) {
        textField.text = shared.getString(getField, putField)
        textField.setTextColor(
            resources.getColor(
                R.color.black,
                resources.newTheme()
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showHeaders(textField: TextView, putField: String) {
        textField.text = putField
        textField.setTextColor(
            resources.getColor(
                R.color.ash,
                resources.newTheme()
            )
        )
    }
}