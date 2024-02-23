package com.example.foodSpot.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodSpot.R
import com.example.foodSpot.model.FAQ

class FAQRecyclerAdapter(val context: Context, val itemList: ArrayList<FAQ>) :
    RecyclerView.Adapter<FAQRecyclerAdapter.FAQViewHolder>() {

    class FAQViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtQA: TextView = view.findViewById(R.id.txtQA)
        val imgAdder: ImageView = view.findViewById(R.id.imgAdder)
        val cdView: CardView = view.findViewById(R.id.cdViewFavourite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.faq_single_row, parent, false)
        return FAQViewHolder(view)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faq = itemList[position]
        val checkList = arrayListOf<Boolean>()

        for (i in 0..15)
            checkList.add(false)

        val ques = getColourSpanned(faq.question, "#f44336")

        holder.txtQA.text = Html.fromHtml(ques)
        holder.imgAdder.setImageResource(faq.downArrow)

        holder.cdView.setOnClickListener {
            val check = checkList[position]
            if (!check) {
                holder.txtQA.append("\n\n")
                holder.txtQA.append(faq.answer)
                holder.imgAdder.setImageResource(faq.upArrow)
                checkList[position] = true
            } else {
                holder.txtQA.text = Html.fromHtml(ques)
                holder.imgAdder.setImageResource(faq.downArrow)
                checkList[position] = false
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    private fun getColourSpanned(text: String?, color: String): String {
        return "<font color=$color>$text</font>"
    }
}