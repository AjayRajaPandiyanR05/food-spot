package com.example.foodSpot.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodSpot.R
import com.example.foodSpot.adapter.FAQRecyclerAdapter
import com.example.foodSpot.model.FAQ

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FAQFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FAQFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerFAQ: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FAQRecyclerAdapter

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
        val view = inflater.inflate(R.layout.fragment_f_a_q, container, false)

        val FAQList = arrayListOf<FAQ>(
            FAQ(
                getString(R.string.qn1),
                getString(R.string.ans1),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn2),
                getString(R.string.ans2),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn3),
                getString(R.string.ans3),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn4),
                getString(R.string.ans4),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn5),
                getString(R.string.ans5),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn6),
                getString(R.string.ans6),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn7),
                getString(R.string.ans7),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn8),
                getString(R.string.ans8),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn9),
                getString(R.string.ans9),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn10),
                getString(R.string.ans10),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn11),
                getString(R.string.ans11),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn12),
                getString(R.string.ans12),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn13),
                getString(R.string.ans13),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn14),
                getString(R.string.ans14),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            ),
            FAQ(
                getString(R.string.qn15),
                getString(R.string.ans15),
                R.drawable.ic_baseline_keyboard_arrow_down_24,
                R.drawable.ic_baseline_keyboard_arrow_up_24
            )
        )

        recyclerFAQ = view.findViewById(R.id.recyclerFAQ)
        layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = FAQRecyclerAdapter(activity as Context, FAQList)

        recyclerFAQ.adapter = recyclerAdapter
        recyclerFAQ.layoutManager = layoutManager

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FAQFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FAQFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}